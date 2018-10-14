import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class ClientWorkerOut extends Thread {
    private OutputStream output;

    public ClientWorkerOut(OutputStream output){
        this.output = output;
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
        Scanner sc = new Scanner(System.in);
        String msgToSend = "";
        do {
            //out
            msgToSend = sc.nextLine();

            byte[] dataToSend = msgToSend.getBytes();
            output.write(dataToSend);

        } while (!msgToSend.contains("QUIT"));
    }
}
