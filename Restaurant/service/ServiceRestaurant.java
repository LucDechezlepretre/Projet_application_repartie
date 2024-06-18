import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

public class ServiceRestaurant implements ServiceBD {

    Connection connection;

    public ServiceRestaurant(String fichierINI){
        try {
            ArrayList<String> InfoDB = new ArrayList<>();

            BufferedReader fichin = new BufferedReader(new FileReader(fichierINI));
            String ligne = fichin.readLine();
            while (ligne != null) {
                String[] tab = ligne.split("=");
                InfoDB.add(tab[1].trim());
                ligne = fichin.readLine();
            }
            
            fichin.close();

            // Connection vers la base de données
            Class.forName("oracle.jdbc.driver.OracleDriver");

            connection = DriverManager.getConnection(InfoDB.get(0), InfoDB.get(1), InfoDB.get(2));
        } catch (SQLException e) {
            System.out.println("Database connection failed");
            e.printStackTrace();
        } catch (FileNotFoundException e){
            System.out.println("Ficier ini non trouvé");
            e.printStackTrace();            
        } catch (IOException e){
            System.out.println("Impossible de lire le fichier ");
            e.printStackTrace();  
        } catch (ClassNotFoundException e){
            System.out.println("la classe des driver n'a pas été ");
            e.printStackTrace();
        }
    }

    @Override
    public String getRestaurants() throws RemoteException {
        ArrayList<Restaurant> restaurants = new ArrayList<>();
    
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM restaurants");
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                System.out.println(result.getString(2));
                restaurants.add(new Restaurant(result.getInt(1), result.getString(2), result.getString(3), 
                    result.getDouble(4), result.getDouble(5))); 
            }
            
            String res = "{\"restaurants\" : [";
            for(Restaurant r : restaurants){
                res += new JSONObject(r).toString();
                res += ",";
            }
            res += "]}";
            return res;
        } catch(SQLException e) {
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            return "{\"code\":500,\"status\":false,\"message\":\"" +  e.getMessage()+ "\",\"serverTime\":\"" + currentDate.toString() + "\"}";
        }
    }

    public String ajouterRestaurant(String nom, String adresse, double lat, double lon) throws RemoteException{
        try {
            String requete = """
                    INSERT INTO restaurants (nom, adresse, latitude, longitude) VALUES (?, ?, ?, ?)
                    """;
            PreparedStatement stmt = connection.prepareStatement(requete);
            stmt.setString(1, nom);
            stmt.setString(2, adresse);
            stmt.setDouble(3, lat);
            stmt.setDouble(4, lon);
            stmt.executeUpdate();
            
            return "{\"code\":200,\"status\":true,\"data\":{\"message\":\"Votre restaurant a bien été enregistré sur la carte.\"}}";
        } catch(SQLException e) {
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            return "{\"code\":500,\"status\":false,\"message\":\"" +  e.getMessage()+ "\",\"serverTime\":\"" + currentDate.toString() + "\"}";
        }
    }

    //réserver une table dans un restaurant en renseignant nom, prénom, nombre de convives, et coordonnées téléphoniques de la réservation.
    @Override
    public String reserverTable(int idRestaurant, String nom, String prenom, int nbConvives, String numTel) throws RemoteException {
        int indexTable = trouverTableDisponible(idRestaurant, nbConvives);

        if(indexTable == -1){
            return "{\"code\":500,\"status\":false,\"message\":\"Table non disponible à cette horaire.\"}";
        }

        try {
            String requete = """
                    INSERT INTO reservations (restaurant_id, table_id, prenom, nom, nbConvives, numTel, reservation_time)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
            PreparedStatement stmt = connection.prepareStatement(requete);
            stmt.setInt(1, idRestaurant);
            stmt.setInt(2, indexTable);
            stmt.setString(3, prenom);
            stmt.setString(4, nom);
            stmt.setInt(5, nbConvives);
            stmt.setString(6, numTel);
            stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();
            
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            
            return "{\"code\":200,\"status\":true,\"data\":{\"message\":\"Votre table a bien été réservée.\"},\"serverTime\":\"" + currentDate.toString() + "\"}";
        } catch (SQLException e) {
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            return "{\"code\":500,\"status\":false,\"message\":\"" +  e.getMessage()+ "\",\"serverTime\":\"" + currentDate.toString() + "\"}";
        }
    }

    private int trouverTableDisponible(int idRestaurant, int nbConvives) {
        try {
            String requete = """
                    SELECT t.id 
                    FROM tables t INNER JOIN restaurants r ON t.restaurant_id = r.id 
                    LEFT JOIN reservations r2 ON t.id = r2.table_id AND r2.reservation_time = TO_TIMESTAMP(?, 'DD-MM-YYYY HH24:MI:SS') 
                    WHERE t.restaurant_id = ? AND t.nbPlaces >= ? AND r2.id IS NULL
                    """;
            PreparedStatement stmt = connection.prepareStatement(requete);

            stmt.setString(1, "10-10-2024 23:00:00");
            stmt.setInt(2, idRestaurant);
            stmt.setInt(3, nbConvives);
            ResultSet result = stmt.executeQuery();

            if(result.next()){
                return result.getInt(1);
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }
    
}