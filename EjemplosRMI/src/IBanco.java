import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBanco extends Remote{

	public String getSaldo() throws RemoteException;
	public void saveMoney(int money) throws RemoteException;
	public int fastWithdraw() throws RemoteException;
}
