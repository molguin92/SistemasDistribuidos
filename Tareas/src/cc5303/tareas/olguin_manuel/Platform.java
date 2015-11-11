package cc5303.tareas.olguin_manuel;

import org.w3c.dom.css.Rect;

import java.awt.*;

/**
 * Created by arachnid92 on 06-10-15.
 */
public class Platform extends Rectangle
{

    public static int THICKNESS = 10;
    public int width;

    public Platform ( int centerX, int centerY, int width )
    {
        super(centerX - width/2, centerY - THICKNESS/2, width, THICKNESS);
        this.width = width;
    }

    public int[] getState ()
    {
        return new int[] {this.x, this.y, width, THICKNESS };
    }

}
