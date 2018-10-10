import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerWorker extends Thread {
    private final Socket socket;
    public ArrayList<Client> clients;

    public ServerWorker(Socket socket, ArrayList<Client> clients){
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            handleClientSocket(clients);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException f) {
            f.printStackTrace();
        }
    }

    private void handleClientSocket(ArrayList<Client> clients) throws IOException, InterruptedException {
        InputStream input = this.socket.getInputStream();
        OutputStream output = this.socket.getOutputStream();

        //Server input from clients
        do {
            byte[] dataIn = new byte[1024];
            input.read(dataIn);
            String msgIn = new String(dataIn);
            msgIn = msgIn.trim();
            System.out.println("IN -->" + msgIn + "<--");
            if (msgIn.equals("QUIT") && clients.size() > 0) {
                for (int i = 0; i < clients.size(); i++) {
                    clients.remove(clients.get(i));
                    output = clients.get(i).getSocket().getOutputStream();
                    String msgToSend = clients.get(i).getUsername() + " has left the server\n\nLIST <<" +  clients.toString() + ">>\n\n";
                    byte[] dataToSend = msgToSend.getBytes();
                    output.write(dataToSend);
                }
                break;
            }

            //Output message to clients
            for (int i = 0; i < clients.size(); i++) {
                if (clients.get(i).getSocket() != this.socket && clients.get(i).getSocket().isBound()) {
                    output = clients.get(i).getSocket().getOutputStream();
                    String msgToSend = "DATA " + clients.get(i).getUsername() + ": " + msgIn;
                    byte[] dataToSend = msgToSend.getBytes();
                    output.write(dataToSend);
                }
            }
        } while (clients.size() > 0);
    }
}
