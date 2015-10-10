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
        this.setFont(new Font("SCORE", Font.BOLD, 16));
    }

    @Override
    public void update(Graphics g) { paint(g); }

    @Override
    public void paint(Graphics g){
        if(buffer==null){
            img = createImage(getWidth(),getHeight() );
            buffer = img.getGraphics();
        }

        int score = 0;
        int lives = 0;

        buffer.setColor(Color.black);
        buffer.fillRect(0, 0, getWidth(), getHeight());
        int[][] players = new int[4][7];
        int[][] platforms = new int[0][0];

        try {
            players = state.getPlayers();
            platforms = state.getPlatforms();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        for ( int[] player : players )
        {

            if ( player[6] == -1 )
            {
                //ESPERANDO A MAS JUGADORES;
                buffer.setColor(Color.CYAN);
                buffer.drawString("ESPERANDO... ", 25, 200);
                g.drawImage(img, 0, 0, null);
                return;
            }

            if ( player[4] == this.playerID )
            {
                buffer.setColor(Color.RED);
                buffer.fillRect(player[0], player[1], player[2], player[2]);
                System.out.println("SCORE: " + player[5]);
                score = player[5];
                lives = player[6];

                if( player[3] == 0 )
                {
                    System.out.println("SCORE: " + player[5]);
                    System.out.println("GAME OVER");
                    System.exit(0);
                }

            } else
            {
                if ( player[3] != 0 )
                {//no activo
                    buffer.setColor(Color.BLUE);
                    buffer.fillRect(player[0], player[1], player[2], player[2]);
                }
            }
        }

        buffer.setColor(Color.WHITE);
        for (int[] platform : platforms) {
            buffer.fillRect(platform[0], platform[1], platform[2], platform[3]);
        }

        //score y vidas se dibujan al final para quedar SOBRE lo demas
        buffer.setColor(Color.CYAN);
        buffer.drawString("SCORE: " + score, 25, 25);
        buffer.drawString("LIVES: " + lives, 25, 50);

        g.drawImage(img, 0, 0, null);
    }
}
