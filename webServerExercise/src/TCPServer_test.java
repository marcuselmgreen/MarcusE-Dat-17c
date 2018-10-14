import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServer_test {
    public static void main(String[] args) {
        System.out.println("=============SERVER==============");
        ArrayList<Client> clients = new ArrayList<>();

        final int PORT_LISTEN = 5656;
        boolean accept = true;


        try {
            ServerSocket server = new ServerSocket(PORT_LISTEN);
            while (true) {
                System.out.println("Server starting...\n");


                Socket socket = server.accept();
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                //Client to array
                byte[] dataIn = new byte[1024];
                input.read(dataIn);
                String username = new String(dataIn);
                username = username.replaceAll("\u0000", "");
                Client client = new Client(username, socket);
                if (checkDuplicateUsername(clients, username)) {
                    clients.add(client);
                    //J_OK
                    String msgToSend = "J_OK\n\n";
                    output = client.getSocket().getOutputStream();
                    byte[] dataToSend = msgToSend.getBytes();
                    output.write(dataToSend);

                    //PrintList
                    printList(clients);
                    System.out.println("\nClient connected");
                    String clientIp = socket.getInetAddress().getHostAddress();
                    System.out.println("IP: " + clientIp);
                    System.out.println("PORT: " + socket.getPort());
                    ServerWorker worker = new ServerWorker(socket, clients, client);
                    worker.start();
                } else {
                    String msgToSend = "J_ER 0: Duplicate name";
                    output = socket.getOutputStream();
                    byte[] dataToSend = msgToSend.getBytes();
                    output.write(dataToSend);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void printList(ArrayList<Client> clients) throws IOException{
        for (Client client : clients) {
            String msgToSend = "\n\nLIST <<" + clients.toString()+ ">>\n\n";
            OutputStream output = client.getSocket().getOutputStream();
            byte[] dataToSend = msgToSend.getBytes();
            output.write(dataToSend);
        }
    }
    public static boolean checkDuplicateUsername(ArrayList<Client> clients, String username) {
        for (Client client : clients) {
            if (client.getUsername().equalsIgnoreCase(username)){
                return false;
            }
        }return true;
    }
}