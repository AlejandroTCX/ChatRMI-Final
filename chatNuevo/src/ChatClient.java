import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.util.List;

public interface ChatClient extends Remote {
    void receiveMessage(String message) throws RemoteException;

    void updateClientList(List<ChatClient> clients) throws RemoteException;

    String getClientIP() throws RemoteException, UnknownHostException, java.net.UnknownHostException;
}