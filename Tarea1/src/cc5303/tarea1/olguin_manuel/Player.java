package cc5303.tarea1.olguin_manuel;

import java.rmi.Remote;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by arachnid92 on 03-10-15.
 */
public class Player
{
    public int posX;
    public int posY;
    public int velX;
    public int velY;

    public boolean ready;

    public boolean standing;

    private Queue<Integer> ops;

    private static final int JUMP = 0;
    private static final int MLEFT = 1;
    private static final int MRIGHT = 2;

    public void accelerate( int X, int Y )
    {
        this.velX += X;
        this.velY += Y;
    }

    public void jump()
    {
        standing = false;
        ops.offer(JUMP);
    }

    public void moveRight()
    {
        ops.offer(MRIGHT);
    }

    public void moveLeft()
    {
        ops.offer(MLEFT);
    }

    public void update()
    {

        this.posX += this.velX;
        this.posY += this.velY;

        if ( !this.standing )
            this.velY--;

        if ( this.velX < 0 )
            this.velX++;
        else if ( this.velX > 0 )
            this.velX--;

        Integer op = ops.poll();

        if ( op != null )
            switch ( op )
            {
                case JUMP:
                    if ( standing ) // solo podemos saltar desde plataformas
                        this.accelerate(0, 10);
                    break;
                case MLEFT:
                    this.accelerate(-2, 0);
                    break;
                case MRIGHT:
                    this.accelerate(2, 0);
                    break;
                default:
                    System.err.println("Operacion no reconocida.");
                    break;
            }


    }

    public void setReady()
    {
        this.ready = true;
    }

    public Player ( int posX, int posY )
    {

        this.posX = posX;
        this.posY = posY;

        this.velX = 0;
        this.velY = 0;

        this.ops = new LinkedList<>();

        this.standing = true;

    }
}
