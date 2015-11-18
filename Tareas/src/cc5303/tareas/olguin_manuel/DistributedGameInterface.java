package cc5303.tareas.olguin_manuel;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 04-10-15.
 */
public interface DistributedGameInterface extends Remote
{
    RemotePlayer getPlayer(int playerID) throws RemoteException;
    float getLoadAvg() throws RemoteException;
    void initConnections() throws RemoteException;
    void activate(boolean first_run) throws RemoteException;

    void prepareMigration(int new_n_players, boolean together) throws RemoteException;

    void migrateGameThread(boolean running,
                           boolean started,
                           int dead_players,
                           int no_players,
                           int target_no_players,
                           int score,
                           float level_modifier_1,
                           float level_modifier_2,
                           boolean together) throws RemoteException;

    void migratePlayer( int ID,
                        int posX,
                        int posY,
                        float velX,
                        float velY,
                        boolean active,
                        int score,
                        int lives,
                        boolean jumping,
                        boolean restart ) throws RemoteException;

    void migratePlatform (int x, int y, int width) throws RemoteException;

    int[][] getPlayers() throws RemoteException;
    int[][] getPlatforms() throws RemoteException;
    int[] getDimensions() throws  RemoteException;
    boolean getGameOver() throws RemoteException;

    DistributedGameInterface renewRemote() throws RemoteException;
    RemotePlayer renewPlayer(int ID) throws RemoteException;
    String getIP () throws RemoteException;

    boolean hasMigrated() throws RemoteException;

    void leaving(int playerID) throws RemoteException;
}
