package cc5303.tarea1.olguin_manuel.v1;

import java.awt.*;

/**
 * Created by arachnid92 on 08-10-15.
 */
public class CollissionHandler
{

    Player[] players;
    Platform[] platforms;

    public CollissionHandler(Player[] players, Platform[] platforms)
    {
        this.platforms = platforms;
        this.players = players;
    }

    void checkCollisions()
    {

        for ( Player player: players )
        {
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
        }
    }
}
