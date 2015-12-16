package cc5303.tareas.olguin_manuel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by arachnid92 on 01-10-15.
 */
public class GameServer {

    // Main class of the server.
    // Must be executed with the ip of the server as first parameter.
    // Second parameter is a path to the serverlist file

    // Optional parameter: -n 4 (wait for 4 and start!)

    static String urlServer = "rmi://localhost:1099/gameserver";
    //static String serverfile = "./serverIPs";
    static String[] serverips = {"192.168.1.183","192.168.1.109"};

    public static void main ( String[] args )
    {
        String IP = args[0];
        String path = args[1];
        Queue<String> servers = new LinkedBlockingQueue<>();
        int n_players = 2;
        boolean together = false;

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String server;
            while ( (server = br.readLine()) != null )
                servers.add(server);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.setProperty("java.rmi.server.hostname", IP);

        if ( args.length > 3 && args[2].equalsIgnoreCase("-n") )
        {
            n_players = Integer.parseInt(args[3]);
            together = true;
        }

        DistributedGameInterface rinter = null;
        try {
            rinter = new DistributedGameHandler(servers , IP, n_players, together ); // TODO Fix
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

    }

}
