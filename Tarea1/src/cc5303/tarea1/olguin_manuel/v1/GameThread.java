package cc5303.tarea1.olguin_manuel.v1;

import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 03-10-15.
 */
public class GameThread extends Thread {

    private static int WIDTH = 200;
    private static int HEIGHT = 600;

    private boolean running;
    private int current_level;

    public BoardState state;
    private PlatformGenerator platformGenerator;
    private Player[] players;
    private Platform[] platforms;

    public Player activatePlayer() {
        for (Player player : players) {
            if (!player.active) {
                player.active = true;
                return player;
            }
        }

        return null;
    }

    public void run() {
        while (running) {
            for (int i = 0; i < players.length; i++) {
                if (players[i].active)
                    players[i].update();

                try {
                    state.players[i] = players[i].getState();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

            for ( int i = 0; i < platforms.length; i++ )
                this.state.platforms[i] = this.platforms[i].getState();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean isRunning() {
        return running;
    }

    public GameThread() {
        this.players = new Player[4];
        for (int i = 0; i < this.players.length; i++)
            try {
                this.players[i] = new Player(0, HEIGHT - 30);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        try {
            this.state = new BoardState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.state.players = new int[players.length][4];
        this.state.dimensions = new int[2];
        this.state.dimensions[0] = WIDTH;
        this.state.dimensions[1] = HEIGHT;
        this.running = false;
        this.platformGenerator = new PlatformGenerator(WIDTH, HEIGHT);
        this.platforms = this.platformGenerator.generatePlatforms();
        this.state.platforms = new int[this.platforms.length][3];
    }

    @Override
    public void start() {
        running = true;
        super.start();
    }

}
