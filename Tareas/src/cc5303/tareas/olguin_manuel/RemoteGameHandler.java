package cc5303.tareas.olguin_manuel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by arachnid92 on 04-10-15.
 */
public class RemoteGameHandler extends UnicastRemoteObject implements RemoteGameInterface
{

    GameThread game;
    private static final long serialVersionUID = 1L;

    public RemoteGameHandler ( GameThread game ) throws RemoteException
    {
        super();
        this.game = game;
    }

    @Override
    public RemotePlayer getPlayer() throws RemoteException {
        return game.activatePlayer();
    }

    @Override
    public RemoteBoardState getBoardState() throws RemoteException {
        return game.state;
    }
}
