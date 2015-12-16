package cc5303.tareas.olguin_manuel;

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

    protected GameState state;

/*    protected boolean migrate;
    protected boolean running;
    protected boolean started;
    protected boolean gameover;
    protected boolean together;
    protected boolean paused;
    protected int dead_players;
    protected int no_players;
    protected int target_no_players;
    protected int score;
    protected float level_modifier_1;
    protected float level_modifier_2;

    protected Player[] players;
    protected ArrayList<Platform> platforms;
    protected int[][] plat_array;
    protected int[][] play_array;*/

    Random rand;

    int bkp_cnt;

    public RemotePlayer activatePlayer() {

        // Activates a player for use by a remote client.

        if (state.started && state.together)
            return null;

        for (Player player : state.players) {

            if (!player.active) {

                if (state.together && state.no_players == state.target_no_players)
                    return null;

                System.err.println("Activating Player " + player.ID);
                state.no_players++;
                player.active = true;
                if (!state.started)
                    player.body.setLocation(rand.nextInt(WIDTH - 20) + 10, HEIGHT - 50);
                else
                    player.body.setLocation(rand.nextInt(WIDTH - 20) + 10, HEIGHT/2);

                if ( state.no_players == state.target_no_players )
                    state.started = true;

                return player;
            }
        }

        return null;
    }

    public void run() {
        while (state.running) {

            bkp_cnt++;
            if(bkp_cnt >= 50)
            {
                bkp_cnt = 0;
                this.backUpToDisk();
            }

            if ( state.migrate )
                return;

            if ((!state.started && state.together) || state.paused) {

                for(Player p: state.players)
                    if(p.active && p.toggle_pause)
                    {
                        this.state.paused = false;
                        p.toggle_pause = false;
                    }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }

            state.play_array = new int[state.players.length][7];
            for ( int[] a: state.play_array )
                Arrays.fill(a, 0);

            // next, we update all the objects

            boolean shift = false;

            this.checkCollisions();

            for (int i = 0; i < state.players.length; i++) {

                if (state.players[i].active) {
                    state.players[i].update();
                    state.players[i].score = this.state.score + state.players[i].score_offset;
                    state.players[i].restart = false;
                    if (state.players[i].toggle_pause)
                    {
                        this.state.paused = true;
                        state.players[i].toggle_pause = false;
                    }
                }
                if (state.players[i].body.getMaxY() < 200)
                    shift = true;

                try {
                    state.play_array[i] = state.players[i].getState();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (shift)
                this.shiftDown();

            state.plat_array = new int[state.platforms.size()][4];
            for ( int i =  0; i < state.platforms.size(); i++ )
                state.plat_array[i] = state.platforms.get(i).getState();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (this.state.gameover)
                this.GameOver();
        }

    }

    public GameThread(GameState state) {
        rand = new Random(System.nanoTime());
        this.state = state;
        this.bkp_cnt = 0;
    }

    public GameThread(int n_players, boolean together) {

        this.bkp_cnt = 0;
        this.state = new GameState();

        rand = new Random(System.nanoTime());

        this.state.target_no_players = n_players;
        this.state.together = together;
        this.state.dead_players = 0;

        this.state.players = new Player[n_players];
        for (int i = 0; i < this.state.players.length; i++)
            // create inactive players
            try {
                this.state.players[i] = new Player(WIDTH / 2, HEIGHT / 2);
                this.state.players[i].ID = i + 1;
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        state.play_array = new int[1][7];
        for ( int[] a: state.play_array )
            Arrays.fill(a, -1);

        state.plat_array = new int[1][4];
        for ( int[] a: state.plat_array )
            Arrays.fill(a, 0);

        this.state.running = false;
        this.state.started = false;
        this.state.paused = false;
        this.state.platforms = new ArrayList<>();
        this.state.level_modifier_1 = 0.2f;
        this.state.level_modifier_2 = 0.5f;

        for (int y = -HEIGHT; y < HEIGHT; y += 100) {
            // generate random platforms
            this.generatePlatforms(y);
        }
        //add special ground platform
        this.state.platforms.add(new Platform(WIDTH / 2, HEIGHT - 2 * Platform.THICKNESS, 2 * WIDTH));

        this.state.no_players = 0;
        this.state.score = 0;
    }

    @Override
    public void start() {
        this.state.running = true;
        this.state.migrate = false;
        super.start();
    }

    private void addPlatformBetween( int x1, int x2, int y) {
        // adds a level of platforms at Y.
        // the distribution of the platforms is random.
        int pwidth;
        int cX;
        int dx = x2 - x1;

        int rand_b = Math.max((int) (state.level_modifier_1 * dx), 1);
        pwidth = rand.nextInt(rand_b) + (int) (state.level_modifier_2 * dx);
        cX = pwidth / 2 + rand.nextInt(dx - pwidth) + x1;
        this.state.platforms.add(new Platform(cX, y, pwidth));
    }

    public void generatePlatforms(int y ) {

        // initializes a random playing field.

        int n = rand.nextInt(3) + 1;
        int d;
        switch (n) {
            case 1:
                addPlatformBetween( 0, WIDTH, y);
                break;
            case 2:
                d = WIDTH / n;
                addPlatformBetween(0, d, y);
                addPlatformBetween(d, 2 * d, y);
                break;
            case 3:
                d = WIDTH / n;
                addPlatformBetween(0, d, y);
                addPlatformBetween(d, 2 * d, y);
                addPlatformBetween(2 * d, 3 * d, y);
                break;
            default:
                break;
        }
    }

    void checkCollisions() {

        for (Player player : state.players) {

            if (!player.active)
                continue;

            //between platforms and players
            for (Platform platform : state.platforms) {

                if (player.body.intersects(platform)) {
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
            for (Player player1 : state.players) {
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
                player.body.setLocation(state.platforms.get(5).x, state.platforms.get(5).y + Player.HW);
                player.velY = -1;
            }

            if (player.lives <= 0) {
                player.active = false;
                this.state.dead_players++;
                if (this.state.no_players == this.state.dead_players) {
                    System.out.println("no more players alive");
                    this.state.gameover = true;
                }
            }
        }
    }

    public void shiftDown() {

        // shifts the board downwards,
        // and cleans up platforms that are no longer in view
        state.score++;
        LinkedList<Platform> removal = new LinkedList<>();
        for (Platform platform : state.platforms) {
            platform.translate(0, 1);

            if (platform.getMinY() > HEIGHT)
                removal.add(platform);
        }

        for (Player player : state.players)
            player.body.translate(0, 1);

        if (state.score % 100 == 0) {
            this.state.level_modifier_1 = this.state.level_modifier_1 > 0 ? this.state.level_modifier_1 - 0.005f : 0;
            this.state.level_modifier_2 = this.state.level_modifier_2 > 0 ? this.state.level_modifier_2 - 0.005f : 0;
            this.generatePlatforms(-HEIGHT);
        }

        for (Platform platform : removal)
            state.platforms.remove(platform);
        removal = null;
    }


    public void GameOver()
    {
        while (this.state.gameover)
        {
            for (Player p: state.players)
            {
                this.state.gameover = p.restart && this.state.gameover;
                System.out.println("Player " + p.ID + " votes: " + p.restart);
            }
            this.state.gameover = !this.state.gameover;

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

        state.play_array = new int[1][7];
        for ( int[] a: state.play_array )
            Arrays.fill(a, -1);

        state.plat_array = new int[1][4];
        for ( int[] a: state.plat_array )
            Arrays.fill(a, 0);

        this.state.dead_players = 0;
        this.state.gameover = false;
        this.state.started = true;
        this.state.platforms = new ArrayList<>();
        this.state.level_modifier_1 = 0.2f;
        this.state.level_modifier_2 = 0.5f;

        for (int y = -HEIGHT; y < HEIGHT; y += 100) {
            // generate random platforms
            this.generatePlatforms(y);
        }
        //add special ground platform
        this.state.platforms.add(new Platform(WIDTH / 2, HEIGHT - 2 * Platform.THICKNESS, 2 * WIDTH));

        this.state.score = 0;

        for(int i = 0; i < state.no_players; i++) {
            Player p = state.players[i];
            System.out.println("Reactivating player " + p.ID);
            p.active = true;
            p.score = 0;
            p.body.setLocation(rand.nextInt(WIDTH - 20) + 10, HEIGHT - 50);
            p.velY = 0;
            p.lives = 4;
        }

    }

    public int[] getDimensions() {
        return new int[] {WIDTH, HEIGHT};
    }

    public void backUpToDisk()
    {
        BackupThread t = new BackupThread(this.state);
        t.run();
    }
}
