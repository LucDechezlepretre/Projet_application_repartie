import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceCentral extends Remote {
    public String getRestaurants() throws RemoteException;
    public String reserverTable(int idRestaurant, String nom, String prenom, int nbConvives, String numTel) throws RemoteException;
    public String ajouterRestaurant(String nom, String adresse, double lat, double lon) throws RemoteException;
}
