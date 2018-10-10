import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientWorkerIn extends Thread implements Runnable {
    private final Socket socket;


    public ClientWorkerIn(Socket socket, InputStream input){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            handleIn();
        } catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException f){
            f.printStackTrace();
        }
    }

    private void handleIn() throws IOException, InterruptedException {
        InputStream input = this.socket.getInputStream();
        String msgIn = "";
        while (true) {//this.socket.isBound();
            byte[] dataIn = new byte[1024];
            input.read(dataIn);
            msgIn = new String(dataIn);
            msgIn = msgIn.trim();
            System.out.println(msgIn);
        }
    }
}
