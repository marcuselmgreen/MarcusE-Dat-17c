import java.net.Socket;

public class Client {
    private String username;
    private final Socket socket;

    public Client(String username, Socket socket) {
        this.username = username;
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public String toString() {
        return getUsername();
    }
}
