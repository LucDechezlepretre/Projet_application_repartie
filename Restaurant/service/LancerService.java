import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class LancerService {
    public static void main(String[] args) {
        ServiceRestaurant serviceRestaurant = new ServiceRestaurant("config.ini");

        try {
            ServiceBD serviceCentral = (ServiceBD) UnicastRemoteObject.exportObject(serviceRestaurant, 0);

            try {
                Registry reg = LocateRegistry.createRegistry(1099);

                try {
                    reg.rebind("RestaurantService", serviceCentral);
                    System.out.println("Le service de restauration est lancé");
                } catch (RemoteException e) {
                    System.out.println("Erreur dans l'enregistrement du service, méthode rebind");
                    e.printStackTrace();
                }
            } catch (RemoteException e) {
                System.out.println("Erreur dans l'enregistrement du service, methode de création de l'annuaire annuaire (annuaire déjà créé)");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Erreur dans l'enregistrement du service, création du service central");
            e.printStackTrace();
        }
    }
}