package cc5303.tareas.olguin_manuel;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 04-10-15.
 */
public class Client
{

    public static void main ( String[] args )
    {

        //Main class of the client
        //Must be executed with the IP of ANY of the servers as a parameter!

        String IP = args[0];
        System.setProperty("java.rmi.server.hostname", IP);

        DistributedGameInterface remote;
        RemotePlayer player;
        final Board board;

        try {
            remote = (DistributedGameInterface) Naming.lookup("rmi://" + IP + ":1099/gameserver");
            remote = remote.renewRemote();
            player = remote.getPlayer(-1);
            final int[] dim = remote.getDimensions();
            if ( player == null )
            {
                System.err.println("Max amount of players reached!");
                System.exit(69);
            }

            board = new Board(remote, player, IP );

            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new ClientFrame(board, dim);
                }
            });

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }

    }

}
