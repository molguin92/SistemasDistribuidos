package cc5303.tarea1.olguin_manuel.v1;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

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

        RemoteGameInterface remote;
        RemotePlayer player;
        RemoteBoardState state;
        Board board;

        try {
            remote = (RemoteGameInterface) Naming.lookup("rmi://" + IP + ":1099/gameserver");
            state = remote.getBoardState();
            player = remote.getPlayer();
            if ( player == null )
            {
                System.err.println("Max amount of players reached!");
                System.exit(69);
            }

            board = new Board(state, player.getID());

            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        new ClientFrame(player, board, state.getDimensions());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

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
