package cc5303.tarea1.olguin_manuel.v1;

import java.awt.*;
import java.rmi.RemoteException;

/**
 * Created by sebablasko on 9/11/15.
 */
public class Board extends Canvas {

    public Image img;
    public Graphics buffer;
    public RemoteBoardState state;
    public int playerID;

    public Board(RemoteBoardState state, int ID)
    {
        this.playerID = ID;
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
        buffer.fillRect(0, 0, getWidth(), getHeight());
        int[][] players = new int[4][5];
        int[][] platforms = new int[0][0];

        try {
            players = state.getPlayers();
            platforms = state.getPlatforms();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        for ( int[] player : players )
        {

            if ( player[3] == 0 ) //no activo
                continue;

            if ( player[4] == this.playerID )
            {
                buffer.setColor(Color.RED);
            } else { buffer.setColor(Color.BLUE); }

            buffer.fillRect(player[0], player[1], player[2], player[2]);
        }

        buffer.setColor(Color.WHITE);
        for (int[] platform : platforms) {
            buffer.fillRect(platform[0], platform[1], platform[2], platform[3]);
        }

        g.drawImage(img, 0, 0, null);
    }
}
