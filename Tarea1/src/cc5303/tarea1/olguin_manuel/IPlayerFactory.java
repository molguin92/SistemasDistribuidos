package cc5303.tarea1.olguin_manuel;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by arachnid92 on 03-10-15.
 */
public interface IPlayerFactory extends Remote
{

    Player createPlayer() throws RemoteException;
}
