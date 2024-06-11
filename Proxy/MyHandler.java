import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.json.JSONObject;

public class MyHandler implements HttpHandler {
    private String ipServiceDonneeBloquee;
    private static String donneesBloquees = "/donneesbloquees";
    private static String restaurant = "/restaurant";
    public MyHandler(String ipSDB){
        this.ipServiceDonneeBloquee = ipSDB;
    }
    /**
     * 1) Type requete.GET
     * 2) extraire le nom du fichier diemandé
     * 3) chercher le fichier demandé
     * 4) le charger
     */
    public void handle(HttpExchange exchange) throws IOException {
        String typeRequete = exchange.getRequestMethod();
        Registry reg;
        if (typeRequete.equalsIgnoreCase("GET")) {
            //System.out.println(exchange.getRequestURI().toString());
            if(exchange.getRequestURI().toString().equals(donneesBloquees)){
                reg = LocateRegistry.getRegistry(this.ipServiceDonneeBloquee);
                try{
                    ServiceDonneesBloquees s = (ServiceDonneesBloquees) reg.lookup("DonneeBloquee");
                    try{
                        JSONObject json = new JSONObject(s.recupererDonneesBloquees());
                        byte[] fileContent = json.toString().getBytes();
                        exchange.sendResponseHeaders(200, fileContent.length);
                        OutputStream responseBody = exchange.getResponseBody();
                        responseBody.write(fileContent);
                        responseBody.close();
                        //System.out.println(json);
                    } catch(RemoteException e){
                        System.out.println("Erreur lors de l'enregistrement du service sur le serveur central");
                        e.printStackTrace();
                    }
                }catch(NotBoundException e){
                    System.out.println("Aie aie aie !");      
                }
            }
            else if(exchange.getRequestURI().equals(restaurant)){
                
            }
                
        }
    }
}
