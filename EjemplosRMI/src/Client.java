import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {

	public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {

			IBanco banco = (IBanco) Naming.lookup(Server.urlServer);
            String comando = "", linea ="";
            printComands();

            while(!linea.equals("salir")){
                linea = br.readLine();
                comando = linea.split(" ")[0];
                switch(comando){
                    case "saldo":
                        System.out.println(banco.getSaldo());
                        break;
                    case "giro":
                        System.out.println("Ha realizado un giro rapido por: "+banco.fastWithdraw());
                        break;
                    case "deposito":
                        int deposito = Integer.parseInt(linea.split(" ")[1]);
                        banco.saveMoney(deposito);
                        System.out.println("Ha realizado un deposito por "+ deposito);
                        break;
                    case "salir":
                        System.out.println("Usted ha salido del banco");
                        break;                        
                    default:
                        printComands();
                        break;
                }
            }			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void printComands(){
		System.out.println("Ingrese un comando valido: saldo/deposito <int>/giro/salir");
	}

}
