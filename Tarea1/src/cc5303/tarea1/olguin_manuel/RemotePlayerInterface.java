package cc5303.tarea1.olguin_manuel;

import cc5303.tarea1.olguin_manuel.v1.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 03-10-15.
 */
public interface RemotePlayerInterface extends Remote
{

    Player createPlayer() throws RemoteException;
    void startGame() throws RemoteException;
    boolean isRunning() throws RemoteException;

}
