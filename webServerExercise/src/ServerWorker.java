import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerWorker extends Thread {
    private final Socket socket;
    public ArrayList<Client> clients;
    public Client client;
    static OutputStream output;

    public ServerWorker(Socket socket, ArrayList<Client> clients, Client client){
        this.socket = socket;
        this.clients = clients;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            handleClientSocket(clients, client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket(ArrayList<Client> clients, Client client) throws IOException {
        InputStream input = this.socket.getInputStream();
        //OutputStream output = this.socket.getOutputStream();
        String msgIn = "";

        //Server input from clients
        do {
            if (!msgIn.contains("QUIT")) {

                byte[] dataIn = new byte[1024];
                input.read(dataIn);
                msgIn = new String(dataIn);
                msgIn = msgIn.trim();
                System.out.println("DATA " + this.client.getUsername() + ": " + msgIn);

                //Output message to clients
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i).getSocket() != this.socket && clients.get(i).getSocket().isBound()) {

                        output = clients.get(i).getSocket().getOutputStream();
                        String msgToSend = "DATA " + this.client.getUsername() + ": " + msgIn;
                        byte[] dataToSend = msgToSend.getBytes();
                        output.write(dataToSend);
                    }
                }
            } else {
                //Client leaving
                if (clientLeaving(msgIn, socket)) {
                    Socket socket = new Socket();
                    break;
                }
                for (int i = 0; i < clients.size(); i++) {
                    output = clients.get(i).getSocket().getOutputStream();
                    String msgToSend = "LIST <<" + clients.toString() + ">>\n\n";
                    byte[] dataToSend = msgToSend.getBytes();
                    output.write(dataToSend);
                }
            }
        } while (true);
    }
    public boolean clientLeaving (String msgIn, Socket socket){
        try {
            if (msgIn.equals("QUIT")) {
                for (int i = 0; i<clients.size(); i++) {
                    if (clients.get(i).getSocket() == this.socket) {
                        output = this.socket.getOutputStream();
                        String msgToSend = "\n\nLeaving...\n";
                        byte[] dataToSend = msgToSend.getBytes();
                        output.write(dataToSend);
                        clients.remove(clients.get(i));
                        this.socket.close();
                        return false;
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return true;
    }
}
