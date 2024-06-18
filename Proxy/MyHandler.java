import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.json.JSONObject;

public class MyHandler implements HttpHandler {
    private String ipServiceDonneeBloquee;
    private String ipServiceRestaurant;
    private static String donneesBloquees = "/donneesbloquees";
    private static String restaurant = "/restaurant";

    public MyHandler(String ipSDB, String ipSR) {
        this.ipServiceDonneeBloquee = ipSDB;
        this.ipServiceRestaurant = ipSR;
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
            if (exchange.getRequestURI().toString().equals(donneesBloquees)) {
                reg = LocateRegistry.getRegistry(this.ipServiceDonneeBloquee);
                try {
                    ServiceDonneesBloquees s = (ServiceDonneesBloquees) reg.lookup("DonneeBloquee");
                    try {
                        JSONObject json = new JSONObject(s.recupererDonneesBloquees());
                        System.out.println("JSON récupéré");
                        byte[] fileContent = json.toString().getBytes();
                        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                        exchange.sendResponseHeaders(200, fileContent.length);
                        OutputStream responseBody = exchange.getResponseBody();
                        responseBody.write(fileContent);
                        responseBody.close();
                        // System.out.println(json);
                    } catch (RemoteException e) {
                        System.out.println("Erreur lors de l'enregistrement du service sur le serveur central");
                        e.printStackTrace();
                    }
                } catch (NotBoundException e) {
                    System.out.println("Aie aie aie !");
                }
            } else if (exchange.getRequestURI().toString().equals(restaurant)) {
                System.out.println("ServiceRestaurant est détecté LETS GOOOOOOOOOOOOOOOOOOO");
                reg = LocateRegistry.getRegistry(this.ipServiceRestaurant);
                try {
                    ServiceBD s = (ServiceBD) reg.lookup("RestaurantService");
                    System.out.println("RESTAURANT SERVICE");
                    try {
                        JSONObject json = new JSONObject(s.getRestaurants());
                        System.out.println("JSON RECUPERE");
                        byte[] fileContent = json.toString().getBytes();
                        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                        exchange.sendResponseHeaders(200, fileContent.length);
                        OutputStream responseBody = exchange.getResponseBody();

                        responseBody.write(fileContent);
                        responseBody.close();
                    } catch (RemoteException e) {
                        System.out.println("Erreur lors de l'enregistrement du service sur le serveur central");
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (NotBoundException e) {
                    e.printStackTrace();
                    System.out.println("Aie aie aie !");
                } catch (RemoteException e) {
                    System.out.println("Erreur lors de la récupération du service");
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (typeRequete.equalsIgnoreCase("POST")) {
            if (exchange.getRequestURI().toString().equals(restaurant)) {
                reg = LocateRegistry.getRegistry(this.ipServiceDonneeBloquee);
                try {
                    ServiceBD s = (ServiceBD) reg.lookup("RestaurantService");
                    try {
                        // Récupérer le corps de la requête
                        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        StringBuilder requestBody = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            requestBody.append(line);
                        }
                        br.close();

                        // Convertir le corps de la requête en objet JSON
                        JSONObject jsonRequest = new JSONObject(requestBody.toString());

                        // Traiter les données reçues
                        String nom = jsonRequest.getString("nom");
                        String adresse = jsonRequest.getString("adresse");
                        double latitude = jsonRequest.getDouble("latitude");
                        double longitude = jsonRequest.getDouble("longitude");

                        // Appel à la méthode pour ajouter un restaurant
                        String response = s.ajouterRestaurant(nom, adresse, latitude, longitude);

                        // Envoyer la réponse au client
                        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } catch (RemoteException e) {
                        System.out.println("Erreur lors de l'enregistrement du service sur le serveur central");
                        e.printStackTrace();
                    }
                } catch (NotBoundException e) {
                    System.out.println("Aie aie aie !");
                }
            }
        }
    }
}
