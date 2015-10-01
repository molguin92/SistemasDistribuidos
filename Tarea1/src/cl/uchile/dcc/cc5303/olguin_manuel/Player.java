package cl.uchile.dcc.cc5303.olguin_manuel;

import java.awt.*;

/**
 * Created by sebablasko on 9/11/15.
 */
public class Player {

    int posX, posY, w = 7, h = 10;
    double speed = 0.4;
    public boolean standUp = false;

    public Player(int x, int y){
        this.posX = x;
        this.posY = y;
    }

    public void jump(){
        if(this.standUp)
            this.speed = -0.9;
        this.standUp = false;
    }

    public void moveRight() {
        this.posX += 2;
    }

    public void moveLeft() {
        this.posX -= 2;
    }

    public void update(int dx){
        this.posY += this.speed*dx;
        this.speed += this.speed < 0.8 ? 0.02: 0;
        this.standUp = (this.speed == 0);
    }

    public void draw(Graphics g){
        g.fillRect(this.posX, this.posY, this.w, this.h);
    }

    @Override
    public String toString(){
        return "player: position ("+this.posX+","+this.posY+")";
    }

    public boolean collide(Bench b){
        return Math.abs(bottom() - b.top()) < 5 && right() <= b.right() && left() >= b.left();
    }

    public boolean hit(Bench b){
        return Math.abs(top() - b.bottom()) < 5 && right() <= b.right() && left() >= b.left();
    }

    public int top() {
        return this.posY;
    }

    public int left() {
        return this.posX;
    }

    public int bottom() {
        return this.posY + this.h;
    }

    public int right() {
        return this.posX + this.w;
    }
}
