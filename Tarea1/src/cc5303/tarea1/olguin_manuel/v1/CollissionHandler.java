package cc5303.tarea1.olguin_manuel.v1;

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
                if ( player.posX >= (platform.posX - (platform.width/2)) && player.posX <= (platform.posX + (platform.width/2)))
                {

                    int dy = Math.abs( player.posY - platform.posY );
                    int threshold = (Platform.THICKNESS + Player.HW)/2;
                    if ( dy <= threshold )
                    {
                        if ( player.standing )
                            continue;

                        System.err.println ( "COLLISION" );
                        if ( player.posY <= platform.posY  )
                        {
                            // on top
                            player.posY = platform.posY - threshold - 5;
                            player.standing = true;
                            player.velY = 0;
                        }
                        else
                        {
                            // under
                            player.posY = platform.posY + threshold + 1;
                            player.velY = 1;
                        }
                    }
                }
            }
        }
    }
}
