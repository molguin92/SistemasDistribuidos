package cc5303.tarea1.olguin_manuel.v1;

import java.awt.*;

/**
 * Created by sebablasko on 9/11/15.
 */
public class Board extends Canvas {

    public int width, height;
    public Image img;
    public Graphics buffer;
    public BoardState state;

    public Board(BoardState state){
        this.width = state.dimensions[0];
        this.height = state.dimensions[1];
        this.state = state;
    }

    @Override
    public void update(Graphics g) { paint(g); }

    @Override
    public void paint(Graphics g){
        if(buffer==null){
            img = createImage(getWidth(),getHeight() );
            buffer = img.getGraphics();
        }

        buffer.setColor(Color.black);
        buffer.fillRect(0, 0, getWidth(), getHeight());;

        for(int[] player: state.players)
        {
            //paint each player

            int x = player[0] - 3;
            int y = player[1] - 6;

            buffer.setColor(Color.RED);
            buffer.fillRect(x,y,6, 12);
        }

        buffer.setColor(Color.white);

        g.drawImage(img, 0, 0, null);
    }

}
