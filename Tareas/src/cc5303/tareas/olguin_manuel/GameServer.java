package cc5303.tareas.olguin_manuel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arachnid92 on 01-10-15.
 */
public class GameServer {

    // Main class of the server.
    // Must be executed with the ip of the server as first parameter.

    // Optional parameter: -n 4 (wait for 4 and start!)

    static String urlServer = "rmi://localhost:1099/gameserver";
    static String serverfile = "./serverIPs";

    public static void main ( String[] args )
    {
        String IP = args[0];
        int n_players = 2;
        boolean together = false;
        boolean slave = true;
        System.setProperty("java.rmi.server.hostname", IP);

        if ( args[1].equalsIgnoreCase("-n") && args.length > 2 )
        {
            n_players = Integer.parseInt(args[2]);
            together = true;
        }

        BufferedReader br = null;
        List<String> serverlist = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(serverfile));
            String line;
            while ((line = br.readLine()) != null )
                if ( !line.equals(IP))
                    serverlist.add(line);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GameThread game = new GameThread(n_players, together);
        DistributedGameInterface rinter = null;
        try {
            rinter = new DistributedGameHandler(game, serverlist.toArray(new String[1]), IP); // TODO Fix
        } catch (RemoteException e) {
            e.printStackTrace();
        }

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

        MigrationThread mthread = new MigrationThread((DistributedGameHandler) rinter);
        mthread.start();

        try {
            game.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
