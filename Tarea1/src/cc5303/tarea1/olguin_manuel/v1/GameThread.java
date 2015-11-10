package cc5303.tarea1.olguin_manuel.v1;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by arachnid92 on 03-10-15.
 */
public class GameThread extends Thread {

    // Thread in charge of the logic of the game!

    private static int WIDTH = 200;
    private static int HEIGHT = 600;

    private boolean running;
    private boolean started;
    private int dead_players;
    private int no_players;
    private int target_no_players;
    private int score;
    private float level_modifier_1;
    private float level_modifier_2;

    private boolean together;

    public BoardState state;
    private Player[] players;
    private ArrayList<Platform> platforms;

    public RemotePlayer activatePlayer() {

        // Activates a player for use by a remote client.

        if (started && together)
            return null;

        for (Player player : players) {

            if (!player.active) {

                if (together && no_players == target_no_players)
                    return null;

                Random rng = new Random(System.currentTimeMillis());
                no_players++;
                player.ID = no_players;
                player.active = true;
                player.body.setLocation(rng.nextInt(WIDTH - 20) + 10, HEIGHT - 50);

                if ( no_players == target_no_players )
                    started = true;

                return player;
            }
        }

        return null;
    }

    public void run() {
        while (running) {

            if (!started && together) {

                // run together! wait for a minimum amount of players
                this.state.players = new int[1][7];
                Arrays.fill(this.state.players[0], -1);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }

            // next, we update all the objects

            boolean shift = false;

            this.checkCollisions();

            this.state.players = new int[players.length][0];
            for (int i = 0; i < players.length; i++) {

                players[i].restart = false;

                if (players[i].active) {
                    players[i].update();
                    players[i].score = this.score;
                }
                if (players[i].body.getMaxY() < 200)
                    shift = true;

                try {
                    this.state.players[i] = players[i].getState();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (shift)
                this.shiftDown();

            this.state.platforms = new int[this.platforms.size()][0];
            for (int i = 0; i < platforms.size(); i++)
                this.state.platforms[i] = this.platforms.get(i).getState();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (state.gameover)
                this.GameOver();
        }

    }

    public GameThread(int n_players, boolean together) {

        this.target_no_players = n_players;
        this.together = together;
        this.dead_players = 0;

        this.players = new Player[n_players];
        for (int i = 0; i < this.players.length; i++)
            // create inactive players
            try {
                this.players[i] = new Player(WIDTH / 2, HEIGHT / 2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        try {
            this.state = new BoardState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.state.players = new int[4][7];
        this.state.dimensions = new int[2];
        this.state.dimensions[0] = WIDTH;
        this.state.dimensions[1] = HEIGHT;
        this.running = false;
        this.started = false;
        this.platforms = new ArrayList<>();
        this.level_modifier_1 = 0.2f;
        this.level_modifier_2 = 0.5f;

        for (int y = -HEIGHT; y < HEIGHT; y += 100) {
            // generate random platforms
            this.generatePlatforms(y);
        }
        //add special ground platform
        this.platforms.add(new Platform(WIDTH / 2, HEIGHT - 2 * Platform.THICKNESS, 2 * WIDTH));

        this.state.platforms = new int[this.platforms.size()][3];
        this.no_players = 0;
        this.score = 0;
    }

    @Override
    public void start() {
        running = true;
        super.start();
    }

    private void addPlatformBetween(Random rand, int x1, int x2, int y) {
        // adds a level of platforms at Y.
        // the distribution of the platforms is random.
        System.err.printf("Adding platform between %d and %d\n", x1, x2);
        int pwidth;
        int cX;
        int dx = x2 - x1;

        int rand_b = Math.max((int) (level_modifier_1 * dx), 1);
        pwidth = rand.nextInt(rand_b) + (int) (level_modifier_2 * dx);
        System.err.printf("Platform width: %d\n", pwidth);
        cX = pwidth / 2 + rand.nextInt(dx - pwidth) + x1;
        System.err.printf("Platform center: %d\n", cX);
        this.platforms.add(new Platform(cX, y, pwidth));
    }

    public void generatePlatforms(int y) {

        // initializes a random playing field.

        Random rand = new Random(System.currentTimeMillis());
        int n = rand.nextInt(3) + 1;
        System.err.printf("Random: %d\n", n);
        int d;
        switch (n) {
            case 1:
                addPlatformBetween(rand, 0, WIDTH, y);
                break;
            case 2:
                d = WIDTH / n;
                addPlatformBetween(rand, 0, d, y);
                addPlatformBetween(rand, d, 2 * d, y);
                break;
            case 3:
                d = WIDTH / n;
                addPlatformBetween(rand, 0, d, y);
                addPlatformBetween(rand, d, 2 * d, y);
                addPlatformBetween(rand, 2 * d, 3 * d, y);
                break;
            default:
                break;
        }
    }

    void checkCollisions() {

        for (Player player : players) {

            if (!player.active)
                continue;

            //between platforms and players
            for (Platform platform : platforms) {

                if (player.body.intersects(platform)) {
                    System.out.println("Collision");
                    //arriba
                    if (player.body.getMaxY() >= platform.getMinY() && player.body.getMaxY() < platform.getMaxY()) {
                        player.body.setLocation(player.body.x, platform.y - Player.HW);
                        player.velY = 0;
                        player.jumping = false;
                    }
                    //abajo
                    else {
                        player.body.setLocation(player.body.x, platform.y + Player.HW);
                        player.velY = 0.5f;
                    }
                }
            }

            //between players and players
            for (Player player1 : players) {
                if (player == player1 || !player1.active)
                    continue;

                if (player.body.intersects(player1.body)) {
                    if (player.body.getMinY() < player1.body.getMinY()) {
                        player.accelerate(0, -5f);
                        if (player1.velY < 0)
                            player1.velY = 0;
                    } else {
                        player1.accelerate(0, -5f);
                        if (player.velY < 0)
                            player.velY = 0;
                    }
                }
            }

            //finally, check bounds
            if (player.body.x < 0) {
                player.body.x = 0;
                if (player.velX < 0)
                    player.velX = 0;
                player.accelerate(0.2f, 0);
            } else if (player.body.x > WIDTH - Player.HW) {
                player.body.x = WIDTH - Player.HW;
                if (player.velX > 0)
                    player.velX = 0;
                player.accelerate(-0.2f, 0);
            }

            if (player.body.getMinY() > HEIGHT) {
                System.out.println("PLAYER OUT");
                player.lives--;
                player.body.setLocation(platforms.get(5).x, platforms.get(5).y + Player.HW);
                player.velY = -1;
                if (player.lives <= 0) {
                    player.active = false;
                    this.dead_players++;
                    if (this.no_players == this.dead_players) {
                        System.out.println("no more players alive");
                        state.gameover = true;
                    }
                }
            }
        }
    }

    public void shiftDown() {

        // shifts the board downwards,
        // and cleans up platforms that are no longer in view
        score++;
        LinkedList<Platform> removal = new LinkedList<>();
        for (Platform platform : platforms) {
            platform.translate(0, 1);

            if (platform.getMinY() > HEIGHT)
                removal.add(platform);
        }

        for (Player player : players)
            player.body.translate(0, 1);

        if (score % 100 == 0) {
            this.level_modifier_1 = this.level_modifier_1 > 0 ? this.level_modifier_1 - 0.005f : 0;
            this.level_modifier_2 = this.level_modifier_2 > 0 ? this.level_modifier_2 - 0.005f : 0;
            this.generatePlatforms(-HEIGHT);
        }

        for (Platform platform : removal)
            platforms.remove(platform);
        removal = null;
    }


    public void GameOver()
    {
        while (state.gameover)
        {
            for (Player p: players)
            {
                state.gameover = p.restart && state.gameover;
            }
            state.gameover = !state.gameover;

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.resetGameState();
    }

    public void resetGameState()
    {
        System.out.println("Restarting game.");
        Random rng = new Random(System.currentTimeMillis());

        this.dead_players = 0;
        this.state.players = new int[4][7];
        this.state.dimensions = new int[2];
        this.state.dimensions[0] = WIDTH;
        this.state.dimensions[1] = HEIGHT;
        this.state.gameover = false;
        this.started = true;
        this.platforms = new ArrayList<>();
        this.level_modifier_1 = 0.2f;
        this.level_modifier_2 = 0.5f;

        for (int y = -HEIGHT; y < HEIGHT; y += 100) {
            // generate random platforms
            this.generatePlatforms(y);
        }
        //add special ground platform
        this.platforms.add(new Platform(WIDTH / 2, HEIGHT - 2 * Platform.THICKNESS, 2 * WIDTH));

        this.state.platforms = new int[this.platforms.size()][3];
        this.score = 0;

        for(int i = 0; i < no_players; i++) {
            Player p = players[i];
            System.out.println("Reactivating player " + p.ID);
            p.active = true;
            p.score = 0;
            p.body.setLocation(rng.nextInt(WIDTH - 20) + 10, HEIGHT - 50);
            p.velY = 0;
            p.lives = 4;
        }

    }

}
