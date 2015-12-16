package cc5303.tareas.olguin_manuel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by arachnid92 on 16-12-15.
 */
public class BackupThread extends Thread{

    GameState state;

    public BackupThread(GameState state) {
        this.state = state.clone();
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./.state.bkp"));
            oos.writeObject(this.state);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
