package cc5303.tareas.olguin_manuel;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by arachnid92 on 03-10-15.
 */
public class Player extends UnicastRemoteObject implements RemotePlayer
{

    // the logical representation of the player in the game

    public static int HW = 10;

    public int ID;
    public Rectangle body;

    public boolean restart;
    public boolean toggle_pause;

    public float velX;
    public float velY;

    public boolean jumping;
    public boolean active;

    public int score;
    public int score_offset;
    public int lives;

    public void accelerate( float X, float Y )
    {
        this.velX += X;
        this.velY += Y;
    }

    @Override
    public void jump() throws RemoteException
    {
        if (!this.jumping)
        {
            this.jumping = true;
            this.body.translate(0, -2);
            this.velY = 0;
            this.accelerate(0, -5f);
        }
    }

    @Override
    public void moveLeft() throws RemoteException
    {
        this.velX = -1;
    }
    @Override
    public void moveRight() throws RemoteException
    {
        this.velX = 1;
    }

    @Override
    public void stop() throws RemoteException
    {
        this.velX = 0;
    }

    @Override
    public void voteRestart() throws RemoteException {
        this.restart = true;
    }

    @Override
    public void togglePause() throws RemoteException {
        System.err.println("tryna pause this bitch motherfucker");
        this.toggle_pause = true;
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

        return new int[]{this.body.x, this.body.y, HW, active, this.ID, this.score, this.lives};
    }

    @Override
    public int getID () throws RemoteException
    {
        return this.ID;
    }

    public Player ( int posX, int posY ) throws RemoteException
    {
        super();

        this.velX = 0;
        this.velY = 0;

        this.jumping = false;
        this.active = false;
        this.restart = false;
        this.toggle_pause = false;
        this.score = 0;
        this.score_offset = 0;
        this.lives = 4;

        this.body = new Rectangle( posX, posY, Player.HW, Player.HW  );
    }
}
