package cc5303.tarea1.olguin_manuel.v1;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by arachnid92 on 03-10-15.
 */
public class GameThread extends Thread
{
    private boolean running;
    private int current_level;

    public BoardState state;

    private Player[] players;

    public Player activatePlayer()
    {
        for (Player player : players) {
            if (!player.active) {
                player.active = true;
                return player;
            }
        }

        return null;
    }

    public void run()
    {
        while ( running )
        {
            for ( Player player: players )
            {
                if ( player.active )
                    player.update();
            }

            for ( int i = 0; i < players.length; i++)
                state.players[i] = players[i].getState();
        }

    }

    public boolean isRunning()
    {
        return running;
    }

    public GameThread ()
    {
        this.players = new Player[4];
        for ( int i = 0; i < this.players.length; i++ )
            this.players[i] = new Player(0,0);
        this.state = new BoardState();
        this.state.players = new int[players.length][4];
        this.state.dimensions = new int[2];
        this.state.dimensions[0] = 300;
        this.state.dimensions[1] = 600;
    }

}
