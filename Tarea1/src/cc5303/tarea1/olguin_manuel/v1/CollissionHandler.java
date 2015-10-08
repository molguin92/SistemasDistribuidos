package cc5303.tarea1.olguin_manuel.v1;

import java.awt.*;

/**
 * Created by arachnid92 on 08-10-15.
 */
public class CollissionHandler
{

    Player[] players;
    Platform[] platforms;
    int width;
    int height;

    public CollissionHandler(Player[] players, Platform[] platforms, int width, int height)
    {
        this.platforms = platforms;
        this.players = players;
        this.width = width;
        this.height = height;
    }

    void checkCollisions()
    {

        for ( Player player: players )
        {
            //between platforms and players
            for ( Platform platform: platforms )
            {

                if ( player.body.intersects(platform))
                {
                    System.out.println("Collision");
                    //arriba
                    if ( player.body.getMaxY() >= platform.getMinY() && player.body.getMaxY() < platform.getCenterY() )
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
                if ( player == player1 )
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
            else if ( player.body.x > this.width - Player.HW )
            {
                player.body.x = this.width - Player.HW;
                if (player.velX > 0)
                    player.velX = 0;
                player.accelerate( -0.2f, 0 );
            }


        }
    }
}
