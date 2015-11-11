package cc5303.tareas.olguin_manuel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by arachnid92 on 04-10-15.
 */
public class BoardState extends UnicastRemoteObject implements RemoteBoardState
{
    public int[][] players;
    public int[][] platforms;
    public int[] dimensions;
    public boolean gameover;

    protected BoardState() throws RemoteException {
        super();
        this.players = new int[4][];
        this.platforms = new int[0][];
        this.dimensions = new int[0];
        this.gameover = false;
    }

    @Override
    public int[][] getPlayers() throws RemoteException{
        return players;
    }

    @Override
    public int[][] getPlatforms() throws RemoteException{
        return platforms;
    }

    @Override
    public int[] getDimensions() throws RemoteException{
        return dimensions;
    }

    @Override
    public boolean getGameOver() throws RemoteException{
        return gameover;
    }

}