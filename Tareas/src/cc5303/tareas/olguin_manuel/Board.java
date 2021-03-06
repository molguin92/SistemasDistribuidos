package cc5303.tareas.olguin_manuel;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Board extends Canvas {

    //Class in charge of getting information from the remote interface,
    //and drawing it on screen.

    public Image img;
    public Graphics buffer;
    public int playerID;
    public RemotePlayer player;
    private ScoreComparator scoreComparator;
    private PriorityQueue<int[]> pqueue;
    public boolean request_restart;

    DistributedGameInterface game;

    public Board(DistributedGameInterface game, RemotePlayer player) {
        this.player = player;
        try {
            this.playerID = player.getID();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.setFont(new Font("SCORE", Font.BOLD, 16));
        this.scoreComparator = new ScoreComparator();
        this.pqueue = new PriorityQueue<>(4, scoreComparator);
        this.request_restart = false;
        this.game = game;
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {

        try {

            // check for migration...
            // if the server has migrated, we need to renew our references to the remote objects!
            if (game.hasMigrated()) {
                game = game.renewRemote();
                player = game.renewPlayer(playerID);
            }

            if (player == null) {
                System.err.printf("Player is null... wtf?");
                System.exit(-1);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (buffer == null) {
            img = createImage(getWidth(), getHeight());
            buffer = img.getGraphics();
        }

        int score = 0;
        int lives = 0;

        buffer.setColor(Color.black);
        buffer.fillRect(0, 0, getWidth(), getHeight());
        int[][] players = new int[1][1];
        int[][] platforms = new int[1][1];

        try {
            players = game.getPlayers();
            platforms = game.getPlatforms();
        } catch (RemoteException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        for (int[] player : players) {
            if (player[6] == -1) {
                // Waiting for players.
                buffer.setColor(Color.CYAN);
                buffer.drawString("ESPERANDO... ", 25, 200);
                g.drawImage(img, 0, 0, null);
                return;
            }

            if (player[4] == this.playerID) {
                if (player[3] == 0) {
                    // game over... show scoreboard
                    System.out.println("SCORE: " + player[5]);
                    System.out.println("GAME OVER");

                    this.paint_gameover(players, g);
                    return;
                }
                this.request_restart = false;
                // our player is painted in red
                buffer.setColor(Color.RED);
                buffer.fillRect(player[0], player[1], player[2], player[2]);
                System.out.println("SCORE: " + player[5]);
                score = player[5];
                lives = player[6];
            } else {
                if (player[3] != 0) {
                    buffer.setColor(Color.BLUE);
                    buffer.fillRect(player[0], player[1], player[2], player[2]);
                }
            }
        }

        buffer.setColor(Color.WHITE);
        for (int[] platform : platforms) {
            buffer.fillRect(platform[0], platform[1], platform[2], platform[3]);
        }

        //finally, draw scores and lives over everything else
        buffer.setColor(Color.CYAN);
        buffer.drawString("SCORE: " + score, 25, 25);
        buffer.drawString("LIVES: " + lives, 25, 50);

        g.drawImage(img, 0, 0, null);
    }

    private void paint_gameover(int[][] players, Graphics g) {

        // paints the game over screen

        pqueue.addAll(Arrays.asList(players));
        buffer.setColor(Color.black);
        buffer.fillRect(0, 0, getWidth(), getHeight());
        buffer.setColor(Color.CYAN);
        int y = 100;
        buffer.drawString("SCOREBOARD: ", 25, y);
        while (!pqueue.isEmpty()) {
            y += 20;
            int[] p_state = pqueue.poll();

            if (p_state[4] == this.playerID)
                buffer.drawString("You: " + p_state[5] + "", 25, y);
            else
                buffer.drawString("Player " + p_state[4] + ": " + p_state[5] + "", 25, y);
        }

        try {
            if (game.getGameOver()) {
                y += 40;
                buffer.drawString("GAME OVER", 25, y);
                y += 40;
                buffer.drawString("PRESS R TO", 25, y);
                y += 20;
                buffer.drawString("PLAY AGAIN!", 25, y);
                y += 40;
                if (this.request_restart) {
                    buffer.drawString("Waiting for ", 25, y);
                    y += 20;
                    buffer.drawString("other players", 25, y);
                    y += 20;
                    buffer.drawString("...", 25, y);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        g.drawImage(img, 0, 0, null);
    }

    private class ScoreComparator implements Comparator<int[]> {
        @Override
        public int compare(int[] p_state1, int[] p_state2) {
            // comparing is done "in reverse" for the priority queue
            if (p_state1[5] < p_state2[5])
                return 1;
            else if (p_state1[5] == p_state2[5])
                return 0;
            else
                return -1;
        }
    }
}
