package cc5303.tarea1.olguin_manuel.v1;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 01-10-15.
 */
public class GameServer {

    // Main class of the server.
    // Must be executed with the ip of the server as first parameter.

    // Optional parameter: -n 4 (wait for 4 and start!)

    static String urlServer = "rmi://localhost:1099/gameserver";

    public static void main ( String[] args )
    {
        String IP = args[0];
        int n_players = 2;
        boolean together = false;
        System.setProperty("java.rmi.server.hostname", IP);

        if ( args[1].equalsIgnoreCase("-n") && args.length > 2 )
        {
            n_players = Integer.parseInt(args[2]);
            together = true;
        }

        GameThread game = new GameThread(n_players, together);
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

        try {
            game.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
