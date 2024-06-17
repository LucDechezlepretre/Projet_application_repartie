import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LancerClient {
     public static void main(String[] args) throws RemoteException, NotBoundException{
        Registry reg;
        if(args.length == 0){
            reg = LocateRegistry.getRegistry("localhost");
        } else if(args.length == 1){
            reg = LocateRegistry.getRegistry(args[0]);
        } else {
            reg = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
        }
        
        ServiceBD s = (ServiceBD) reg.lookup("RestaurantService");
        
        try{
            String jsonRestaurants = s.getRestaurants();
            System.out.println(jsonRestaurants);

            String jsonReserverTable = s.reserverTable(1, "Richardin", "Killian", 4, "0623518114");
            System.out.println(jsonReserverTable);

            String jsonAjoutRestaurant = s.ajouterRestaurant("Chez le Luc", "1 rue de Killian", 0, 0); 
            System.out.println(jsonAjoutRestaurant);
        } catch(RemoteException e){
            System.out.println("Erreur lors de l'utilisation des méthode du service central");
            e.printStackTrace();
        }
    }    
}