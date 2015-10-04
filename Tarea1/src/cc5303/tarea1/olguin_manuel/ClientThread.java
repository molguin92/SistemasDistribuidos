package cc5303.tarea1.olguin_manuel;

import cc5303.tarea1.olguin_manuel.v1.Player;

import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 04-10-15.
 */
public class ClientThread extends Thread
{

    RemotePlayerInterface remote;
    Player player;

    public ClientThread ( RemotePlayerInterface remote ) throws RemoteException {
        this.remote = remote;
        this.player = remote.createPlayer();
    }

    public void run()
    {

        boolean run = true;
        int count = 0;

        try {
            remote.startGame();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        while ( run )
        {

            if ( count % 6 == 0 ) //hacemos poll cada 6 loops
            {

            }

            count++;
        }

    }

}
