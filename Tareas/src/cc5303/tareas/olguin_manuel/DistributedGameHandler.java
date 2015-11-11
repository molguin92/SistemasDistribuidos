package cc5303.tareas.olguin_manuel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by arachnid92 on 04-10-15.
 */
public class DistributedGameHandler extends UnicastRemoteObject implements DistributedGameInterface
{

    private static final long serialVersionUID = 1L;

    GameThread game;
    FileReader file_reader;
    DistributedGameInterface[] serverlist;
    private String[] servers;
    public boolean active;

    public DistributedGameHandler(GameThread game, String[] servers ) throws RemoteException
    {
        super();
        this.game = game;
        try {
            this.file_reader = new FileReader("/proc/loadavg");
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        this.servers = servers;
        this.serverlist = new DistributedGameInterface[this.servers.length];
        this.active = false;
    }

    @Override
    public RemotePlayer getPlayer(int playerID) throws RemoteException {
        return game.activatePlayer();
    }

    @Override
    public RemoteBoardState getBoardState() throws RemoteException {
        return game.state;
    }

    @Override
    public float getLoadAvg() throws RemoteException {
        // returns the load average for the current server
        String line = "";
        try {
            file_reader.reset();
            BufferedReader reader = new BufferedReader(file_reader);
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] values = line.split(" ");
        return Float.parseFloat(values[0]);
    }

    @Override
    public void initConnections() throws RemoteException {
        // initiates the connections to the other servers,
        // to set up the distributed system.
        for ( int i = 0; i < servers.length; i++ )
        {
            try{
            serverlist[i] = (DistributedGameInterface) Naming.lookup("rmi://" + servers[i] + ":1099/gameserver");
            } catch (RemoteException | MalformedURLException | NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void activate() throws RemoteException {
        this.active = true;
        if ( !game.isAlive() )
            game.start();
    }

    @Override
    public void migrateGameThread(boolean running, boolean started, int dead_players, int no_players,
                                  int target_no_players, int score, float level_modifier_1,
                                  float level_modifier_2, boolean together) throws RemoteException {
        this.game.running = running;
        this.game.started = started;
        this.game.dead_players = dead_players;
        this.game.no_players = no_players;
        this.game.target_no_players = target_no_players;
        this.game.score = score;
        this.game.level_modifier_1 = level_modifier_1;
        this.game.level_modifier_2 = level_modifier_2;
        this.game.together = together;
    }

    @Override
    public void migratePlayer(int ID, int posX, int posY, float velX, float velY, boolean active, int score, int lives,
                              boolean jumping, boolean restart) throws RemoteException {
        int i = ID - 1;
        this.game.players[i].body.setLocation(posX, posY);
        this.game.players[i].velX = velX;
        this.game.players[i].velY = velY;
        this.game.players[i].active = active;
        this.game.players[i].score = score;
        this.game.players[i].lives = lives;
        this.game.players[i].jumping = jumping;
        this.game.players[i].restart = restart;
    }

    @Override
    public void migratePlatform(int x, int y, int width) throws RemoteException {
        Platform p = new Platform(
                x + width/2,
                y + Platform.THICKNESS/2,
                width
        );

        this.game.platforms.add(p);
    }

    public void migrate()
    {
        PriorityQueue<DistributedGameInterface> queue = new PriorityQueue<>(new ServerLoadComparator());
        queue.addAll(Arrays.asList(serverlist));

        DistributedGameInterface target = queue.poll();

        game.migrate = true;
        try {
            game.join();
            target.migrateGameThread(game.running,
                    game.started,
                    game.dead_players,
                    game.no_players,
                    game.target_no_players,
                    game.score,
                    game.level_modifier_1,
                    game.level_modifier_2,
                    game.together);

            this.game.platforms = new ArrayList<>();

            for(Player p: game.players )
                target.migratePlayer(p.ID, p.body.x, p.body.y,
                        p.velX, p.velY, p.active,
                        p.score, p.lives, p.jumping, p.restart);

            for (Platform p: game.platforms)
                target.migratePlatform(p.x, p.y, p.width);

            // TODO: 11-11-15 : Move references.

        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        }


    }

    private class ServerLoadComparator implements Comparator<DistributedGameInterface>
    {

        @Override
        public int compare(DistributedGameInterface server1, DistributedGameInterface server2) {
            try {
                if ( server1.getLoadAvg() < server2.getLoadAvg() )
                    return -1;
                else if ( server1.getLoadAvg() > server2.getLoadAvg() )
                    return 1;
                else
                    return 0;
            } catch (RemoteException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }


}
