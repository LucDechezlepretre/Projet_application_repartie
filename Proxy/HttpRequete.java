import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpRequete {

    public final static int NUMPORT = 80;

    public static void main(String[] args) throws IOException {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
                
                // Add your request handler with the CORS filter
                server.createContext("/restaurant", new MyHandler("127.0.0.1", "127.0.0.1")).getFilters().add(new CorsFilter());
                server.createContext("/donneesbloquees", new MyHandler("127.0.0.1", "127.0.0.1")).getFilters().add(new CorsFilter());
    
                server.setExecutor(null); // creates a default executor
                server.start();
                System.out.println("Server started on port 8000");
            } catch (IOException e) {
                e.printStackTrace();
            }
        
        System.out.println("Server is running 🏃‍");
    }
}