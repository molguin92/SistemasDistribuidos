package cl.uchile.dcc.cc5303.olguin_manuel;

public class Main {

    //Parametros del Juego
    static final public int ANCHO = 800;
    static final public int ALTO = 600;

    public static void main(String[] args){
        System.out.println("Iniciando Juego de SSDD...");

        MainThread m = new MainThread();
        m.start();
    }
}
