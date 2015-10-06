package cc5303.tarea1.olguin_manuel.v1;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 05-10-15.
 */
public interface RemotePlayer extends Remote
{
    void jump() throws RemoteException;
    void moveLeft() throws RemoteException;
    void moveRight() throws RemoteException;
    int[] getState() throws RemoteException;
}
