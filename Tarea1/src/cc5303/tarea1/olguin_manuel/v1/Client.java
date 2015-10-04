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

        RemoteGameInterface remote;
        Player player;
        BoardState state;
        JFrame frame;
        Board board;

        try {
            remote = (RemoteGameInterface) Naming.lookup(GameServer.urlServer);
            state = remote.getBoardState();

            player = remote.getPlayer();

            frame = new JFrame("Tarea1");
            frame.setVisible(true);
            frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

            board = new Board(state);

            while(true)
            {

                state = remote.getBoardState();
                player.jump();
                board.repaint();
            }



        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

}
