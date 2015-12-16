package cc5303.tareas.olguin_manuel;

import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Queue;

/**
 * Created by arachnid92 on 04-10-15.
 */
public class DistributedGameHandler extends UnicastRemoteObject implements DistributedGameInterface
{

    // This is where the magic happens
    // This class handles the remote connections and migrates the server when needed

    private static final long serialVersionUID = 1L;

    GameThread game;
    DistributedGameInterface[] serverlist;
    DistributedGameInterface current; // <- important! always point to the server currently running the game
    private Queue<String> servers;
    public boolean active;
    public String own_ip;
    public int[] renew_counter;
    private boolean together;
    private int n_players;

    private boolean migrated;

    private OperatingSystemMXBean os;

    public DistributedGameHandler(Queue<String> servers, String own_ip, int n_players, boolean together) throws RemoteException
    {
        super();
        this.servers = servers;
        this.serverlist = new DistributedGameInterface[this.servers.size()];
        this.active = false;
        this.own_ip = own_ip;
        this.migrated = true;

        this.together = together;
        this.n_players = n_players;
        this.renew_counter = new int[n_players];

        this.os = ManagementFactory.getOperatingSystemMXBean();
        DisconnectionThread t = new DisconnectionThread();
        t.start();
    }

    @Override
    public String getIP() throws RemoteException
    {
        return own_ip;
    }

    public boolean hasMigrated() throws RemoteException {
        return this.migrated;
    }

    @Override
    public void leaving(int playerID) throws RemoteException {

        // indicates that player playerID is leaving the game
        // open a new slot and migrate

        System.err.println("Player " + playerID + " is leaving. Opening player slot.");
        Player p = game.players[playerID - 1];
        game.no_players--;
        game.together = false;
        p.active = false;
        p.score = 0;
        p.score_offset = -1 * game.score;
        p.lives = 4;
        p.restart = true;
        this.migrate();
    }

    @Override
    public void togglePause() throws RemoteException {
        this.game.togglePause();
    }

    @Override
    public boolean isPaused() throws RemoteException {
        return game.paused;
    }

    @Override
    public RemotePlayer getPlayer(int playerID) throws RemoteException {

        // a client wants a player
        // activate one and hand it to the caller

        if(playerID > -1)
            return current.renewPlayer(playerID - 1);

        if ( !current.equals(this) )
            return current.getPlayer(playerID);

        Player p = (Player) game.activatePlayer();
        this.renew_counter[p.ID - 1] = 10;
        return p;
    }

    @Override
    public double getLoadAvg() throws RemoteException {
        // returns the load average for the current server
        System.err.println("Polling load...");
        double load = os.getSystemLoadAverage();
        int proc = os.getAvailableProcessors();
        double pcnt = load/proc;
        System.err.println("Current load: " + pcnt*100 + "%");
        return pcnt;
    }

    @Override
    public void initConnections() throws RemoteException {
        // initiates the connections to the other servers,
        // to set up the distributed system.
        System.err.println("Initializing connections...");
        DistributedGameInterface first = null;
        boolean not_connected;
        int n_servers = servers.size();
        for ( int i = 0; i < n_servers; i++ ) {

            String server = servers.poll();
            if (server.equals(own_ip)) {
                serverlist[i] = null;
                continue;
            }

            not_connected = true;
            while (not_connected)
            {
                // keep trying while some servers are not connected
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
        if (first != null ) {
            first.activate(true); // start the game
            this.current = first;
        }
        System.err.println("All systems connected and ready.");
    }

    @Override
    public void activate(boolean first_run) throws RemoteException {

        // starts the game logic

        if ( !this.active ) {
            System.err.println("Game starting (or resuming) here.");
            this.active = true;
            this.current = this;

            if (first_run)
                this.game = new GameThread(this.n_players, this.together);

            this.game.start();
            this.migrated = false;
        }
    }

    @Override
    public void prepareMigration(int new_n_players, boolean together) throws RemoteException {

        // pre-migration preparation

        System.err.println("Starting migration.");

        this.game = new GameThread(new_n_players, together);

        System.err.println("Clearing players...");
        game.players = new Player[new_n_players];

        System.err.println("Clearing platforms...");
        game.platforms.clear();

    }


    @Override
    public void migrateGameThread(boolean running, boolean started, int dead_players, int no_players,
                                  int target_no_players, int score, float level_modifier_1,
                                  float level_modifier_2, boolean together, boolean paused) throws RemoteException {

        // migrates the game thread

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
        this.game.paused = paused;
    }

    @Override
    public void migratePlayer(int ID, int posX, int posY, float velX, float velY, boolean active, int score, int lives,
                              boolean jumping, boolean restart, int score_offset) throws RemoteException {

        // migrates a player

        System.err.println("Receiving Player " + ID + "...");
        int i = ID - 1;
        this.game.players[i] = new Player(posX, posY);
        this.game.players[i].ID = ID;
        this.game.players[i].velX = velX;
        this.game.players[i].velY = velY;
        this.game.players[i].active = active;
        this.game.players[i].score = score;
        this.game.players[i].score_offset = score_offset;
        this.game.players[i].lives = lives;
        this.game.players[i].jumping = jumping;
        this.game.players[i].restart = restart;
    }

    @Override
    public void migratePlatform(int x, int y, int width) throws RemoteException {

        // migrates a platform

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

        System.err.println("Renewing player reference for ID " + ID);

        if ( !current.equals(this) )
            return current.renewPlayer(ID);


        for ( Player player: game.players )
            if ( player.ID == ID ) {
                 if(!player.active)
                     player.active = true;
                return player;
            }

        return null;
    }

    public void migrate()
    {

        // migrates the server

        System.err.println("Starting migration!");

        double min_load = 100;
        double load;
        DistributedGameInterface target = serverlist[0];
        for ( DistributedGameInterface server : serverlist )
        {

            // need to get the loads of the other servers, to compare!

            try {
                if ( !(server != null && server.getIP().compareTo(current.getIP()) != 0) )
                    continue;

                System.err.println("Polling load from " + server.getIP());
                if ((load = server.getLoadAvg()) < min_load ) {
                    min_load = load;
                    target = server;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        try {
            System.err.println("Migrating to " + target.getIP());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        boolean was_paused = game.paused;
        game.paused = false;

        game.migrate = true;
        try {
            game.join();
            System.err.println("Game paused.");
            target.prepareMigration(game.target_no_players, game.together );

            target.migrateGameThread(game.running,
                    game.started,
                    game.dead_players,
                    game.no_players,
                    game.target_no_players,
                    game.score,
                    game.level_modifier_1,
                    game.level_modifier_2,
                    game.together,
                    game.paused);


            for(Player p: game.players )
                target.migratePlayer(p.ID, p.body.x, p.body.y,
                        p.velX, p.velY, p.active,
                        p.score, p.lives, p.jumping, p.restart, p.score_offset);

            for (Platform p: game.platforms)
                target.migratePlatform(p.x, p.y, p.width);

            this.active = false;
            this.migrated = true;
            this.current = target;
            current.activate(false);

            if(was_paused)
                current.togglePause();

        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        }


    }

    private class DisconnectionThread extends Thread
    {
        @Override
        public void run() {
            for(;;)
            {
                if(game != null) {
                    for (int i = 0; i < renew_counter.length; i++) {
                        renew_counter[i]--;
                        if (renew_counter[i] <= 0) {
                            game.players[i].active = false;
                        }
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
