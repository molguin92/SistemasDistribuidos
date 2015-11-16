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
        //Must be executed with the IP of the server as a parameter!

        String IP = args[0];
        System.setProperty("java.rmi.server.hostname", IP);

        DistributedGameInterface remote;
        RemotePlayer player;
        Board board;

        try {
            remote = (DistributedGameInterface) Naming.lookup("rmi://" + IP + ":1099/gameserver");
            remote = remote.renewRemote();
            player = remote.getPlayer(-1);
            int[] dim = remote.getDimensions();
            if ( player == null )
            {
                System.err.println("Max amount of players reached!");
                System.exit(69);
            }

            board = new Board(remote, player.getID());

            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new ClientFrame(player, board, dim);
                }
            });

        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

}
