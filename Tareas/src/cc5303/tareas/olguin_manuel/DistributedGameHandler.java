package cc5303.tareas.olguin_manuel;

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

        this.os = ManagementFactory.getOperatingSystemMXBean();
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
        Player p = game.state.players[playerID - 1];
        game.state.no_players--;
        game.state.together = false;
        p.active = false;
        p.score = 0;
        p.score_offset = -1 * game.state.score;
        p.lives = 4;
        p.restart = true;
        this.migrate();
    }

    @Override
    public RemotePlayer getPlayer(int playerID) throws RemoteException {

        // a client wants a player
        // activate one and hand it to the caller

        if ( !current.equals(this) )
            return current.getPlayer(playerID);
        return game.activatePlayer();
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
        game.state.players = new Player[new_n_players];

        System.err.println("Clearing platforms...");
        game.state.platforms.clear();

    }


    @Override
    public void migrateGameThread(boolean running, boolean started, int dead_players, int no_players,
                                  int target_no_players, int score, float level_modifier_1,
                                  float level_modifier_2, boolean together, boolean paused) throws RemoteException {

        // migrates the game thread

        System.err.println("Receiving game state...");
        this.game.state.running = running;
        this.game.state.started = started;
        this.game.state.dead_players = dead_players;
        this.game.state.no_players = no_players;
        this.game.state.target_no_players = target_no_players;
        this.game.state.score = score;
        this.game.state.level_modifier_1 = level_modifier_1;
        this.game.state.level_modifier_2 = level_modifier_2;
        this.game.state.together = together;
        this.game.state.paused = paused;
    }

    @Override
    public void migratePlayer(int ID, int posX, int posY, float velX, float velY, boolean active, int score, int lives,
                              boolean jumping, boolean restart, int score_offset) throws RemoteException {

        // migrates a player

        System.err.println("Receiving Player " + ID + "...");
        int i = ID - 1;
        this.game.state.players[i] = new Player(posX, posY);
        this.game.state.players[i].ID = ID;
        this.game.state.players[i].velX = velX;
        this.game.state.players[i].velY = velY;
        this.game.state.players[i].active = active;
        this.game.state.players[i].score = score;
        this.game.state.players[i].score_offset = score_offset;
        this.game.state.players[i].lives = lives;
        this.game.state.players[i].jumping = jumping;
        this.game.state.players[i].restart = restart;
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

        this.game.state.platforms.add(p);
    }

    @Override
    public int[][] getPlayers() throws RemoteException {
        if ( !current.equals(this) )
            return current.getPlayers();
        return game.state.play_array;
    }

    @Override
    public int[][] getPlatforms() throws RemoteException {
        if ( !current.equals(this) )
            return current.getPlatforms();
        return game.state.plat_array;
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
        return game.state.gameover;
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

        for ( Player player: game.state.players )
            if ( player.ID == ID )
                return player;

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

            if ( server == null )
                continue;

            try {
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
            System.err.println("Migrating to " + current.getIP());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        game.state.migrate = true;
        try {
            game.join();
            System.err.println("Game paused.");
            target.prepareMigration(game.state.target_no_players, game.state.together );

            target.migrateGameThread(game.state.running,
                    game.state.started,
                    game.state.dead_players,
                    game.state.no_players,
                    game.state.target_no_players,
                    game.state.score,
                    game.state.level_modifier_1,
                    game.state.level_modifier_2,
                    game.state.together,
                    game.state.paused);


            for(Player p: game.state.players )
                target.migratePlayer(p.ID, p.body.x, p.body.y,
                        p.velX, p.velY, p.active,
                        p.score, p.lives, p.jumping, p.restart, p.score_offset);

            for (Platform p: game.state.platforms)
                target.migratePlatform(p.x, p.y, p.width);

            this.active = false;
            this.migrated = true;
            this.current = target;
            current.activate(false);

        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        }


    }

}
