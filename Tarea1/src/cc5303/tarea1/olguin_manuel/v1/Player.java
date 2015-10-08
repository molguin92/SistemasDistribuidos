package cc5303.tarea1.olguin_manuel.v1;

import org.w3c.dom.css.Rect;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by arachnid92 on 03-10-15.
 */
public class Player extends UnicastRemoteObject implements RemotePlayer
{

    public static int HW = 10;

    public int ID;
    public Rectangle body;

    public float velX;
    public float velY;

    public boolean ready;
    public boolean jumping;
    public boolean active;

    private static final int JUMP = 0;
    private static final int MLEFT = 1;
    private static final int MRIGHT = 2;

    public int score;

    public void accelerate( float X, float Y )
    {
        this.velX += X;
        this.velY += Y;
    }

    @Override
    public void jump() throws RemoteException
    {
        this.ready = true;
        if (!this.jumping)
        {
            System.err.println("Jumping");
            this.jumping = true;
            this.body.translate(0, -2);
            this.velY = 0;
            this.accelerate(0, -5f);
        }
    }

    @Override
    public void moveLeft() throws RemoteException
    {
        System.err.println("Left");
        this.velX = -1;
    }
    @Override
    public void moveRight() throws RemoteException
    {
        System.err.println("Right");
        this.velX = 1;
    }

    @Override
    public void stop() throws RemoteException
    {
        this.velX = 0;
    }

    public void update()
    {
        this.body.translate((int)this.velX, (int)this.velY);
        this.velY += 0.1;
    }

    @Override
    public int[] getState () throws RemoteException
    {
        int active = this.active ? 1 : 0;

        return new int[]{this.body.x, this.body.y, HW, active, this.ID, this.score};
    }

    @Override
    public int getID () throws RemoteException
    {
        return this.ID;
    }

    public void setReady()
    {
        this.ready = true;
    }

    public Player ( int posX, int posY ) throws RemoteException
    {
        super();

        this.velX = 0;
        this.velY = 0;

        this.jumping = false;
        this.active = false;
        this.ready = false;

        this.body = new Rectangle( posX, posY, Player.HW, Player.HW  );
    }
}
