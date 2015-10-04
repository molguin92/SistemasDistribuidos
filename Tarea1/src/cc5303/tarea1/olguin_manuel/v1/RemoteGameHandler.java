package cc5303.tarea1.olguin_manuel.v1;

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
        this.game = game;
    }

    @Override
    public Player getPlayer() throws RemoteException {
        return game.activatePlayer();
    }

    @Override
    public BoardState getBoardState() throws RemoteException {
        return game.state;
    }
}
