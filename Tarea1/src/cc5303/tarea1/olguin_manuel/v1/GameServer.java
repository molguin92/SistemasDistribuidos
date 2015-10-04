package cc5303.tarea1.olguin_manuel.v1;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 01-10-15.
 */
public class GameServer {

    static String urlServer = "rmi://localhost:1099/gameserver";

    public static void main ( String[] args )
    {

        GameThread game = new GameThread();
        RemoteGameInterface rinter = null;
        try {
            rinter = new RemoteGameHandler(game);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        game.start();

        try {
            Naming.rebind(urlServer, rinter);
            System.out.println("Listening on "+urlServer);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
