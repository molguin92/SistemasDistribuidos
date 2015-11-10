package cc5303.tarea1.olguin_manuel.v1;

import sun.plugin.javascript.navig.Array;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Board extends Canvas {

    //Classs in charge of getting information from the remote interface,
    //and drawing it on screen.

    public Image img;
    public Graphics buffer;
    public RemoteBoardState state;
    public int playerID;
    private ScoreComparator scoreComparator;
    private PriorityQueue<int[]> pqueue;
    public boolean request_restart;

    public Board(RemoteBoardState state, int ID)
    {
        this.playerID = ID;
        this.state = state;
        this.setFont(new Font("SCORE", Font.BOLD, 16));
        this.scoreComparator = new ScoreComparator();
        this.pqueue = new PriorityQueue<>(4, scoreComparator);
        this.request_restart = false;
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
                if( player[3] == 0 )
                {
                    // game over... ahora hay que mostrar una scoreboard
                    System.out.println("SCORE: " + player[5]);
                    System.out.println("GAME OVER");

                    this.paint_gameover(players, g);
                    return;
                }
                this.request_restart = false;
                // el propio jugador tiene un color distinto
                buffer.setColor(Color.RED);
                buffer.fillRect(player[0], player[1], player[2], player[2]);
                System.out.println("SCORE: " + player[5]);
                score = player[5];
                lives = player[6];
            } else
            {
                if ( player[3] != 0 )
                {
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

    private void paint_gameover(int[][] players, Graphics g)
    {
        pqueue.addAll(Arrays.asList(players));
        buffer.setColor(Color.black);
        buffer.fillRect(0, 0, getWidth(), getHeight());
        buffer.setColor(Color.CYAN);
        int y = 100;
        buffer.drawString("SCOREBOARD: ", 25, y);
        while (!pqueue.isEmpty())
        {
            y += 20;
            int[] p_state = pqueue.poll();

            if ( p_state[4] == this.playerID )
                buffer.drawString("You: " + p_state[5] + "", 25, y);
            else
                buffer.drawString("Player " + p_state[4] + ": " + p_state[5] + "", 25, y);
        }

        try{
            if (state.getGameOver())
            {
                y += 40;
                buffer.drawString("GAME OVER", 25, y);
                y += 40;
                buffer.drawString("PRESS R TO", 25, y);
                y += 20;
                buffer.drawString("PLAY AGAIN!", 25, y);
                y += 40;
                if(this.request_restart)
                {
                    buffer.drawString("Waiting for ", 25, y);
                    y += 20;
                    buffer.drawString("other players", 25, y);
                    y += 20;
                    buffer.drawString("...", 25, y);
                }
            }
        } catch (RemoteException e){
            e.printStackTrace();
        }

        g.drawImage(img, 0, 0, null);
    }

    private class ScoreComparator implements Comparator<int[]> {
        @Override
        public int compare(int[] p_state1, int[] p_state2) {
            // comparing is done "in reverse" for the priority queue
            if(p_state1[5] < p_state2[5])
                return 1;
            else if (p_state1[5] == p_state2[5] )
                return 0;
            else
                return -1;
        }
    }
}
