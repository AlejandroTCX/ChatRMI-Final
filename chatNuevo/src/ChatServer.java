import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatServer {
    public static void main(String[] args) {
        try {
            ChatService chatService = new ChatServiceImpl();
            Registry registry = LocateRegistry.createRegistry(6001);
            registry.rebind("ChatService", chatService);
            System.out.println("Servidor RMI iniciado");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}