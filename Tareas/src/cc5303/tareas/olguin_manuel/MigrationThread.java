package cc5303.tareas.olguin_manuel;

import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 16-11-15.
 */

// monitors the system load and decides when to migrate

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
            double load = 0d;
            try {
                load = gameHandler.getLoadAvg();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (load > 0.15d && gameHandler.active)
                gameHandler.migrate();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
