package cc5303.tareas.olguin_manuel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by arachnid92 on 16-12-15.
 */
public class GameState implements Serializable {

    private static long serialVersionUID = 1L;

    protected boolean migrate;
    protected boolean running;
    protected boolean started;
    protected boolean gameover;
    protected boolean together;
    protected boolean paused;
    protected int dead_players;
    protected int no_players;
    protected int target_no_players;
    protected int score;
    protected float level_modifier_1;
    protected float level_modifier_2;

    protected Player[] players;
    protected ArrayList<Platform> platforms;
    protected int[][] plat_array;
    protected int[][] play_array;

    @Override
    public GameState clone()
    {
        try {
            GameState clone = (GameState) super.clone();

            clone.players = new Player[players.length];
            for(int i = 0; i < clone.players.length; i++)
                    clone.players[i] = players[i].clone();

            clone.platforms = new ArrayList<>(platforms.size());
            for(Platform p: platforms)
                clone.platforms.add((Platform) p.clone());

            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
