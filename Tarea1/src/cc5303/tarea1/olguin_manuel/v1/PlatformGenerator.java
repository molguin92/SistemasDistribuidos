package cc5303.tarea1.olguin_manuel.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by arachnid92 on 06-10-15.
 */
public class PlatformGenerator
{
    List<Platform> platforms;
    int height;
    int width;

    public PlatformGenerator (int width, int height)
    {
        this.platforms = new ArrayList<>();
        this.height = height;
        this.width = width;
    }

    private void addPlatformBetween ( Random rand,int x1, int x2, int y)
    {
        System.err.printf("Adding platform between %d and %d\n", x1, x2);
        int pwidth;
        int cX;
        int dx = x2 - x1;

        pwidth = rand.nextInt((int)(0.2 * dx)) + (int)(0.5 * dx);
        System.err.printf("Platform width: %d\n", pwidth);
        cX = pwidth/2 + rand.nextInt(dx - pwidth) + x1;
        System.err.printf("Platform center: %d\n", cX);
        this.platforms.add(new Platform(cX, y, pwidth));
    }

    public Platform[] generatePlatforms()
    {
        Random rand = new Random(System.currentTimeMillis());

        for ( int y = 100; y < this.height; y += 100 )
        {

            int n = rand.nextInt(3) + 1;
            System.err.printf("Random: %d\n", n);
            int d;
            switch (n)
            {
                case 1:
                    addPlatformBetween(rand, 0, this.width, y);
                    break;
                case 2:
                    d = width/n;
                    addPlatformBetween(rand, 0, d, y );
                    addPlatformBetween(rand, d, 2*d, y);
                    break;
                case 3:
                    d = width/n;
                    addPlatformBetween(rand, 0, d, y );
                    addPlatformBetween(rand, d, 2*d, y);
                    addPlatformBetween(rand, 2*d, 3*d, y);
                    break;
                default:
                    break;
            }
        }

        return this.platforms.toArray(new Platform[this.platforms.size()]);
    }

}
