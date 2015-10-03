package cc5303.tarea1.olguin_manuel;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

/**
 * Created by arachnid92 on 01-10-15.
 */
public class GameServer {

    static String urlServer = "rmi://localhost:5303/gameserver";

    public static void main ( String[] args )
    {
        OptionParser parser = new OptionParser();
        parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(9000);
        parser.accepts("debug");
        parser.accepts("help");

        OptionSet params = parser.parse(args);

        GameThread game = new GameThread();
        IPlayerFactory fact = game;

        try {
            Naming.rebind(urlServer, fact);
            System.out.println("Listening on "+urlServer);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
