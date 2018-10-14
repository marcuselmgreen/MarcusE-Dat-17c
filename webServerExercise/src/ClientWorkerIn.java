import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientWorkerIn extends Thread implements Runnable {
    private InputStream input;


    public ClientWorkerIn(InputStream input){
        this.input = input;
    }

    @Override
    public void run() {
        try {
            handleIn();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleIn() throws IOException {
        String msgIn = "";
        do {
            byte[] dataIn = new byte[1024];
            input.read(dataIn);
            msgIn = new String(dataIn);
            msgIn = msgIn.trim();
            System.out.println(msgIn);
        }while (!msgIn.contains("Leaving..."));
    }
}
