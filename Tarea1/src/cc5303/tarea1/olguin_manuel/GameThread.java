package cc5303.tarea1.olguin_manuel;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by arachnid92 on 03-10-15.
 */
public class GameThread extends Thread implements IPlayerFactory
{

    private boolean running;

    private ArrayList<Player> players;
    private ArrayList<GameObject> objects;

    private int current_level;

    @Override
    public Player createPlayer() throws RemoteException
    {
        if ( players.size() > 3 )
        {
            System.err.println( "Maximum number of players reached." );
            return null;
        }

        Player p = new Player(0, 0);
        players.add(p);
        return p;

    }

    public void run()
    {
        while ( running )
        {
            for ( Player p: players )
                p.update();
        }

    }

    public void startGame()
    {

        if ( players.isEmpty() )
        {
            System.err.println("No players.");
            return;
        }

        current_level = 1;
        running = true;

        this.start();


    }

    public GameThread ()
    {
        players = new ArrayList<Player>();
        objects = new ArrayList<GameObject>();
        running = false;
        current_level = 0;


    }

}
