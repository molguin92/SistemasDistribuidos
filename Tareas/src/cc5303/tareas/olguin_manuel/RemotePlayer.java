package cc5303.tareas.olguin_manuel;

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
    int getID() throws RemoteException;
    void stop() throws RemoteException;
    void voteRestart() throws RemoteException;
}
