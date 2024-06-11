import java.rmi.*;

import org.json.JSONObject;

public interface ServiceDonneesBloquees extends Remote{
    public String recupererDonneesBloquees() throws RemoteException;
}
