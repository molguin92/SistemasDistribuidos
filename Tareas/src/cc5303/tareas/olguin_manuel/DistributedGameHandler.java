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
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by arachnid92 on 04-10-15.
 */
public class DistributedGameHandler extends UnicastRemoteObject implements DistributedGameInterface
{

    private static final long serialVersionUID = 1L;

    GameThread game;
    FileReader file_reader;
    DistributedGameInterface[] serverlist;
    DistributedGameInterface current;
    private Queue<String> servers;
    public boolean active;
    public String own_ip;

    public DistributedGameHandler(GameThread game, Queue<String> servers, String own_ip) throws RemoteException
    {
        super();
        this.game = game;
        this.servers = servers;
        this.serverlist = new DistributedGameInterface[this.servers.size()];
        this.active = false;
        this.own_ip = own_ip;
    }

    @Override
    public String getIP() throws RemoteException
    {
        return own_ip;
    }

    @Override
    public RemotePlayer getPlayer(int playerID) throws RemoteException {
        if ( !current.equals(this) )
            return current.getPlayer(playerID);
        return game.activatePlayer();
    }

    @Override
    public float getLoadAvg() throws RemoteException {
        // returns the load average for the current server
        String line = "";
        try {
            try {
                this.file_reader = new FileReader("/proc/loadavg");
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(file_reader);
            line = reader.readLine();
            file_reader.close();
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
        System.err.println("Initializing connections...");
        DistributedGameInterface first = null;
        boolean not_connected;
        for ( int i = 0; i < servers.size(); i++ ) {

            String server = servers.poll();
            if (server.equals(own_ip)) {
                serverlist[i] = null;
                continue;
            }

            not_connected = true;
            while (not_connected)
            {
                try {
                    serverlist[i] = (DistributedGameInterface) Naming.lookup("rmi://" + server + ":1099/gameserver");
                    not_connected = false;
                    System.err.println(server + " successfully connected!");
                } catch (RemoteException | MalformedURLException | NotBoundException e) {
                    System.err.println(server + " is not yet ready...");
                    not_connected = true;
                }

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if ( i == 0 ) {
                System.err.println("Starting IP: " + server);
                first = serverlist[0];
            }

        }
        first.activate(); // TODO: se hacen partir mutuamente xd pls pls pls
        this.current = first;
        System.err.println("All systems connected and ready.");
    }

    @Override
    public void activate() throws RemoteException {
        if ( !this.active ) {
            System.err.println("Game starting (or resuming) here.");
            this.active = true;
            this.current = this;
            this.game.start();
        }
    }

    @Override
    public void prepareMigration(int new_n_players) throws RemoteException {

        if (!current.equals(this)){
            current.prepareMigration( new_n_players );
            return;
        }

        try {
            game.join();

            System.err.println("Clearing players...");
            game.players = new Player[new_n_players];

            System.err.println("Clearing platforms...");
            game.platforms.clear();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void migrateGameThread(boolean running, boolean started, int dead_players, int no_players,
                                  int target_no_players, int score, float level_modifier_1,
                                  float level_modifier_2, boolean together) throws RemoteException {
        System.err.println("Receiving game state...");
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
        System.err.println("Receiving Player " + ID + "...");
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
        System.err.println("Receiving platform...");
        Platform p = new Platform(
                x + width/2,
                y + Platform.THICKNESS/2,
                width
        );

        this.game.platforms.add(p);
    }

    @Override
    public int[][] getPlayers() throws RemoteException {
        if ( !current.equals(this) )
            return current.getPlayers();
        return game.play_array;
    }

    @Override
    public int[][] getPlatforms() throws RemoteException {
        if ( !current.equals(this) )
            return current.getPlatforms();
        return game.plat_array;
    }

    @Override
    public int[] getDimensions() throws RemoteException {
        if ( !current.equals(this) )
            return current.getDimensions();
        return game.getDimensions();
    }

    @Override
    public boolean getGameOver() throws RemoteException {
        if ( !current.equals(this) )
            return current.getGameOver();
        return game.gameover;
    }

    @Override
    public DistributedGameInterface renewRemote() throws RemoteException {
        return current;
    }

    @Override
    public RemotePlayer renewPlayer(int ID) throws RemoteException {
        if ( !current.equals(this) )
            return current.renewPlayer(ID);

        for ( Player player: game.players )
            if ( player.ID == ID )
                return player;

        return null;
    }

    public void migrate()
    {
        System.err.println("Starting migration!");

        PriorityQueue<DistributedGameInterface> queue = new PriorityQueue<>(new ServerLoadComparator());
        for ( DistributedGameInterface server : serverlist )
            if ( server != null )
                queue.add(server);

        current = queue.poll();
        try {
            System.err.println("Migrating to " + current.getIP());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        game.migrate = true;
        try {
            game.join();
            current.prepareMigration(game.target_no_players);

            current.migrateGameThread(game.running,
                    game.started,
                    game.dead_players,
                    game.no_players,
                    game.target_no_players,
                    game.score,
                    game.level_modifier_1,
                    game.level_modifier_2,
                    game.together);


            for(Player p: game.players )
                current.migratePlayer(p.ID, p.body.x, p.body.y,
                        p.velX, p.velY, p.active,
                        p.score, p.lives, p.jumping, p.restart);

            for (Platform p: game.platforms)
                current.migratePlatform(p.x, p.y, p.width);

            this.active = false;
            current.activate();

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
