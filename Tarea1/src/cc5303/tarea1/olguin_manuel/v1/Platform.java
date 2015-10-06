package cc5303.tarea1.olguin_manuel.v1;

/**
 * Created by arachnid92 on 06-10-15.
 */
public class Platform
{

    private int posX;
    private int posY;
    private int width;

    public Platform ( int centerX, int centerY, int width )
    {
        this.posX = centerX;
        this.posY = centerY;
        this.width = width;
    }

    public int[] getState ()
    {
        return new int[] {posX, posY, width};
    }

}
