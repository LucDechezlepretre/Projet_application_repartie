import java.rmi.*;

public interface ServiceDonneesBloquees extends Remote{
    public String recupererDonneesBloquees() throws RemoteException;
}
