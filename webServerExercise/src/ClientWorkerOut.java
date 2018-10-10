import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientWorkerOut extends Thread {
    private Socket socket;

    public ClientWorkerOut(Socket socket){
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
        Scanner sc = new Scanner(System.in);
        String msgToSend = "";
        do {
            msgToSend = sc.nextLine();

            byte[] dataToSend = msgToSend.getBytes();
            output.write(dataToSend);

            if (msgToSend.equals("QUIT")) {
                this.socket.close();
                break;
            }
        } while (true);
        socket = new Socket();
    }
}
