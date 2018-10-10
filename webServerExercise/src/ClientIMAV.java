import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientIMAV extends Thread {
    private Socket socket;

    public ClientIMAV(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            handleOut();
        } catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException f){
            f.printStackTrace();
        }

    }

    private void handleOut() throws IOException, InterruptedException {
        OutputStream output = this.socket.getOutputStream();
        do {
            //I'm alive message
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    String msgToSend = "IMAV";
                    byte[] dataToSend = msgToSend.getBytes();
                    try {
                        output.write(dataToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 60, TimeUnit.SECONDS);
        } while (true);
    }
}
