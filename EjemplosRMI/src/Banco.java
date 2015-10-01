import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;

public class Banco extends UnicastRemoteObject implements IBanco{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int saldo;
	private int numeroTX;
	
	private int fastMount = 1000;
	
	public Banco()throws RemoteException{
		this.saldo = 10000;
		this.numeroTX = 0;
	}
	
	public String getSaldo() throws RemoteException{
		++this.numeroTX;
		notifyOperationNumber("Obtener saldo");
        return "La cuenta del banco asciende a: $"+this.saldo;
	}
	
	public void saveMoney(int money)throws RemoteException{
		++this.numeroTX;
		this.saldo += money;
		notifyOperationNumber("Depositar dinero ("+money+")");
	}
	
	public int fastWithdraw()throws RemoteException{
		++this.numeroTX;
		this.saldo-= this.fastMount;
		notifyOperationNumber("Giro rapido por "+this.numeroTX);
		return fastMount;
	}
	private void notifyOperationNumber(String nombreOperacion) {
		System.out.println("Operacion #"+this.numeroTX+": "+nombreOperacion+" / Cuenta: "+this.saldo);
	}
}
