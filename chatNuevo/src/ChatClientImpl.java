import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ChatClientImpl extends UnicastRemoteObject implements ChatClient {
    public ChatClientImpl() throws RemoteException {
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        System.out.println("Mensaje recibido: " + message);
        // Puedes agregar aquí la lógica para mostrar el mensaje en la interfaz gráfica
    }

    @Override
    public void updateClientList(List<ChatClient> clients) throws RemoteException {

    }

    @Override
    public String getClientIP() throws RemoteException {
        return null;
    }
}
