package cc5303.tarea1.olguin_manuel.v1;

import java.awt.*;
import java.rmi.RemoteException;

/**
 * Created by sebablasko on 9/11/15.
 */
public class Board extends Canvas {

    public int width, height;
    public Image img;
    public Graphics buffer;
    public RemoteBoardState state;

    public Board(RemoteBoardState state)
    {
        this.state = state;
    }

    @Override
    public void update(Graphics g) { paint(g); }

    @Override
    public void paint(Graphics g){
        if(buffer==null){
            img = createImage(getWidth(),getHeight() );
            buffer = img.getGraphics();
        }

        buffer.setColor(Color.black);
        buffer.fillRect(0, 0, getWidth(), getHeight());;
        int[][] players = new int[4][3];
        int[][] platforms = new int [6][3];

        try {
            players = state.getPlayers();
            platforms = state.getPlatforms();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        buffer.setColor(Color.RED);
        buffer.fillRect(players[0][0] - 5, players[0][1] - 5, 10, 10);

        buffer.setColor(Color.WHITE);
        for ( int[] platform: platforms )
        {
            buffer.fillRect(platform[0] - (platform[2]/2), platform[1] - 2, platform[2], 4 );
        }

        g.drawImage(img, 0, 0, null);
    }
}
