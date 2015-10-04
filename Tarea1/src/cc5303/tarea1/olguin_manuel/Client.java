package cc5303.tarea1.olguin_manuel;

import cc5303.tarea1.olguin_manuel.v1.GameServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 03-10-15.
 */
public class Client
{

    public static void main ( String[] args )
    {

        RemotePlayerInterface remote;
        ClientThread thread;

        try
        {
            remote = (RemotePlayerInterface) Naming.lookup(GameServer.urlServer);
            thread = new ClientThread(remote);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }

}
