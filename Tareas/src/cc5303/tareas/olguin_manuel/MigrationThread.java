package cc5303.tareas.olguin_manuel;

import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 16-11-15.
 */
public class MigrationThread extends Thread {

    DistributedGameHandler gameHandler;

    public MigrationThread ( DistributedGameHandler gameHandler )
    {
        System.err.println("Starting MigrationThread.");
        this.gameHandler = gameHandler;
        try {
            this.gameHandler.initConnections();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true)
        {
            float load = 0;
            try {
                load = gameHandler.getLoadAvg();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (load > 1 && gameHandler.active)
                gameHandler.migrate();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
