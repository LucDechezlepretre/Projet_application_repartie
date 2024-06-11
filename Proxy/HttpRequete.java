import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class HttpRequete {

    public final static int NUMPORT = 80;

    public static void main(String[] args) throws IOException {
        //InetSocketAddress AP = new InetSocketAddress(InetAddress.getByName("localhost"), 80);
        InetSocketAddress AP = new InetSocketAddress(8000);
        HttpServer serveur = HttpServer.create(AP, 0); // Adresse, nb requete à la fois
        serveur.createContext("/", new MyHandler("100.64.80.76"));
        serveur.setExecutor(null);
        serveur.start();
        System.out.println("Server is running 🏃‍");
    }
}