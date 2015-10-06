package cc5303.tarea1.olguin_manuel.v1;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 05-10-15.
 */
public interface RemoteBoardState extends Remote
{
    int[][] getPlayers() throws RemoteException;
    int[][] getPlatforms() throws RemoteException;
    int[] getDimensions() throws  RemoteException;
}
