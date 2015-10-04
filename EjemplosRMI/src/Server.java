import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class Server {

	public static String urlServer = "rmi://localhost:1099/bancoServer";
	
	public static void main(String[] args) {
		
		try {
            IBanco banco = new Banco(9001);
			Naming.rebind(urlServer, banco);
			System.out.println("Objeto Banco publicado en "+urlServer);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
