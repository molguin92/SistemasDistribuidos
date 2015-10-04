package cc5303.tarea1.olguin_manuel.v1;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 04-10-15.
 */
public interface RemoteGameInterface extends Remote
{
    Player getPlayer() throws RemoteException;
    BoardState getBoardState() throws RemoteException;
}
