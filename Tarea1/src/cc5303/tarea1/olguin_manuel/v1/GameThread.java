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

    private static int WIDTH = 200;
    private static int HEIGHT = 600;

    private boolean running;
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
        for (Player player : players) {

            if (!player.active)
            {

                if ( together && no_players == target_no_players )
                    return null;

                Random rng = new Random(System.currentTimeMillis());
                no_players++;
                player.ID = no_players;
                player.active = true;
                player.body.setLocation( rng.nextInt(WIDTH - 20) + 10, HEIGHT - 50 );

                return player;
            }
        }

        return null;
    }

    public void run() {
        while (running) {

            if ( no_players < target_no_players && together ) {
                this.state.players = new int[1][7];
                Arrays.fill(this.state.players[0], -1);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }

            boolean shift = false;

            this.checkCollisions();

            this.state.players = new int[players.length][0];
            for (int i = 0; i < players.length; i++) {
                if (players[i].active)
                {
                    players[i].update();
                    players[i].score = this.score;
                }
                if (players[i].body.getMaxY() < 200 )
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
            for ( int i = 0; i < platforms.size(); i++ )
                this.state.platforms[i] = this.platforms.get(i).getState();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public GameThread( int n_players, boolean together  ) {

        this.target_no_players = n_players;
        this.together = together;

        this.players = new Player[n_players];
        for (int i = 0; i < this.players.length; i++)
            try {
                this.players[i] = new Player(WIDTH/2, HEIGHT/2);
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
        this.platforms = new ArrayList<>();
        this.level_modifier_1 = 0.2f;
        this.level_modifier_2 = 0.5f;

        for ( int y = -HEIGHT; y < HEIGHT; y += 100 )
        {
            this.generatePlatforms(y);
        }
        this.platforms.add(new Platform(WIDTH/2, HEIGHT - 2*Platform.THICKNESS, 2*WIDTH));

        this.state.platforms = new int[this.platforms.size()][3];
        this.no_players = 0;
        this.score = 0;
    }

    @Override
    public void start() {
        running = true;
        super.start();
    }

    private void addPlatformBetween ( Random rand,int x1, int x2, int y)
    {
        System.err.printf("Adding platform between %d and %d\n", x1, x2);
        int pwidth;
        int cX;
        int dx = x2 - x1;

        int rand_b = Math.max((int)(level_modifier_1 * dx), 1);
        pwidth = rand.nextInt(rand_b) + (int)(level_modifier_2 * dx);
        System.err.printf("Platform width: %d\n", pwidth);
        cX = pwidth/2 + rand.nextInt(dx - pwidth) + x1;
        System.err.printf("Platform center: %d\n", cX);
        this.platforms.add(new Platform(cX, y, pwidth));
    }

    public void generatePlatforms(int y)
    {
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

    void checkCollisions()
    {

        for ( Player player: players )
        {

            if (!player.active)
                continue;

            //between platforms and players
            for ( Platform platform: platforms )
            {

                if ( player.body.intersects(platform))
                {
                    System.out.println("Collision");
                    //arriba
                    if ( player.body.getMaxY() >= platform.getMinY() && player.body.getMaxY() < platform.getMaxY() )
                    {
                        player.body.setLocation(player.body.x, platform.y - Player.HW);
                        player.velY = 0;
                        player.jumping = false;
                    }
                    //abajo
                    else
                    {
                        player.body.setLocation(player.body.x, platform.y + Player.HW);
                        player.velY = 0.5f;
                    }
                }
            }

            //between players and players
            for ( Player player1: players)
            {
                if ( player == player1 || !player1.active )
                    continue;

                if ( player.body.intersects(player1.body))
                {
                    if ( player.body.getMinY() < player1.body.getMinY() )
                    {
                        player.accelerate(0, -5f);
                        if ( player1.velY < 0 )
                            player1.velY = 0;
                    }
                    else
                    {
                        player1.accelerate(0, -5f);
                        if ( player.velY < 0 )
                            player.velY = 0;
                    }
                }
            }

            //finally, check bounds
            if ( player.body.x < 0 )
            {
                player.body.x = 0;
                if (player.velX < 0)
                    player.velX = 0;
                player.accelerate( 0.2f, 0 );
            }
            else if ( player.body.x > WIDTH - Player.HW )
            {
                player.body.x = WIDTH - Player.HW;
                if (player.velX > 0)
                    player.velX = 0;
                player.accelerate( -0.2f, 0 );
            }

            if ( player.body.getMinY() > HEIGHT )
            {
                System.out.println ( "PLAYER OUT" );
                player.lives--;
                player.body.setLocation(platforms.get(5).x, platforms.get(5).y + Player.HW);
                player.velY = -1;
                if ( player.lives <= 0)
                    player.active = false;
            }
        }
    }

    public void shiftDown ()
    {
        score++;
        LinkedList<Platform> removal = new LinkedList<>();
        for (Platform platform: platforms)
        {
            platform.translate(0, 1);

            if(platform.getMinY() > HEIGHT)
                removal.add(platform);
        }

        for ( Player player: players )
            player.body.translate(0, 1);

        if ( score % 100 == 0 )
        {
            this.level_modifier_1 = this.level_modifier_1 > 0 ? this.level_modifier_1 - 0.005f : 0;
            this.level_modifier_2 = this.level_modifier_2 > 0 ? this.level_modifier_2 - 0.005f : 0;
            this.generatePlatforms(-HEIGHT);
        }

        for ( Platform platform: removal )
            platforms.remove(platform);
        removal = null;
    }
}
