package cc5303.tarea1.olguin_manuel.v1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by arachnid92 on 03-10-15.
 */
public class Player extends UnicastRemoteObject implements RemotePlayer
{
    public int posX;
    public int posY;
    public float velX;
    public float velY;

    public boolean ready;
    public boolean standing;
    public boolean active;

    private Queue<Integer> ops;

    private static final int JUMP = 0;
    private static final int MLEFT = 1;
    private static final int MRIGHT = 2;

    public void accelerate( float X, float Y )
    {
        this.velX += X;
        this.velY += Y;
    }

    @Override
    public void jump() throws RemoteException
    {
        if (standing)
        {
            System.err.println("Jumping");
            standing = false;
            this.accelerate(0, -8f);
        }
    }

    @Override
    public void moveLeft() throws RemoteException
    {
        System.err.println("Left");
        if ( this.velX > -6)
            this.accelerate(-2, 0);
    }
    @Override
    public void moveRight() throws RemoteException
    {
        System.err.println("Right");
        if ( this.velX < 6)
            this.accelerate(2, 0);
    }

    public void update()
    {

        this.posX += (int)this.velX;
        this.posY += (int)this.velY;

        if ( !this.standing ) {
            this.velY += 0.1;
        } else {
            if ( this.velX < 0 )
                this.velX += 0.5;
            else if ( this.velX > 0 )
                this.velX -= 0.5;
        }
    }

    @Override
    public int[] getState () throws RemoteException
    {
        return new int[]{posX, posY, (int)velX, (int)velY};
    }

    public void setReady()
    {
        this.ready = true;
    }

    public Player ( int posX, int posY ) throws RemoteException
    {
        super();

        this.posX = posX;
        this.posY = posY;

        this.velX = 0;
        this.velY = 0;

        this.ops = new LinkedList<>();

        this.standing = true;
        this.active = false;

    }
}
