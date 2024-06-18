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

    public void handle(HttpExchange exchange) throws IOException {
        String typeRequete = exchange.getRequestMethod();
        Registry reg;
        System.out.println(typeRequete);

        if (typeRequete.equalsIgnoreCase("GET")) {
            if (exchange.getRequestURI().toString().equals(donneesBloquees)) {
                reg = LocateRegistry.getRegistry(this.ipServiceDonneeBloquee);
                try {
                    ServiceDonneesBloquees s = (ServiceDonneesBloquees) reg.lookup("DonneeBloquee");
                    JSONObject json = new JSONObject(s.recupererDonneesBloquees());
                    byte[] fileContent = json.toString().getBytes();
                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                    exchange.sendResponseHeaders(200, fileContent.length);
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(fileContent);
                    responseBody.close();
                } catch (RemoteException | NotBoundException e) {
                    e.printStackTrace();
                }
            } else if (exchange.getRequestURI().toString().equals(restaurant)) {
                reg = LocateRegistry.getRegistry(this.ipServiceRestaurant);
                try {
                    ServiceBD s = (ServiceBD) reg.lookup("RestaurantService");
                    JSONObject json = new JSONObject(s.getRestaurants());
                    byte[] fileContent = json.toString().getBytes();
                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                    exchange.sendResponseHeaders(200, fileContent.length);
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(fileContent);
                    responseBody.close();
                } catch (RemoteException | NotBoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (typeRequete.equalsIgnoreCase("POST")) {
            if (exchange.getRequestURI().toString().equals(restaurant)) {
                reg = LocateRegistry.getRegistry(this.ipServiceRestaurant);
                try {
                    ServiceBD s = (ServiceBD) reg.lookup("RestaurantService");
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder requestBody = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        requestBody.append(line);
                    }
                    br.close();
                    JSONObject jsonRequest = new JSONObject(requestBody.toString());
                    String nom = jsonRequest.getString("nom");
                    String adresse = jsonRequest.getString("adresse");
                    double latitude = jsonRequest.getDouble("latitude");
                    double longitude = jsonRequest.getDouble("longitude");
                    String response = s.ajouterRestaurant(nom, adresse, latitude, longitude);
                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (RemoteException | NotBoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
