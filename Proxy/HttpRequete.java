import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpRequete {

    public final static int NUMPORT = 80;

    public static void main(String[] args) throws IOException {
        //InetSocketAddress AP = new InetSocketAddress(InetAddress.getByName("localhost"), 8000);
        InetSocketAddress AP = new InetSocketAddress(8000);
        HttpServer serveur = HttpServer.create(AP, 0); // Adresse, nb requete à la fois
        serveur.createContext("/", new MyHandler(args[0], args[1]));
        serveur.setExecutor(null);
        serveur.start();
        System.out.println("Server is running 🏃‍");
    }
}