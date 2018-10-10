import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class TCPClient_test {

    public static void main(String[] args) {
        clientStart();
    }


    public static void clientStart() {
        System.out.println("=============CLIENT==============");

        Scanner sc = new Scanner(System.in);
        System.out.println("To join a server, type: JOIN <<user_name>>, <<server_ip>>:<<server_port>>");
        String join = sc.nextLine();
        String[] command = join.split("[,:]");
        String temp_username = command[0];
        String username = temp_username.substring(5);
        checkUsername(username);
        String temp_ipToConnect = command[1];
        String server_ip = temp_ipToConnect.substring(1);
        int server_port = Integer.parseInt(command[2]);
        final int PORT_SERVER = server_port;
        final String IP_SERVER_STR = server_ip.equals("0") ? "127.0.0.1" : server_ip;
        String USERNAME = username;


        try {
            InetAddress ip = InetAddress.getByName(IP_SERVER_STR);

            System.out.println("\nConnecting...");
            System.out.println("SERVER IP: " + IP_SERVER_STR);
            System.out.println("SERVER PORT: " + PORT_SERVER);
            System.out.println("USERNAME: " + username + "\n");

            //Sends ip, port and username to server
            Socket socket = new Socket(ip, PORT_SERVER);
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            byte[] username_out = USERNAME.getBytes();
            output.write(username_out);

            ClientWorkerOut workerOut = new ClientWorkerOut(socket);
            ClientWorkerIn workerIn = new ClientWorkerIn(socket, input);
            ClientIMAV imav = new ClientIMAV(socket);

            //Thread that sends messages from client to server
            workerOut.start();
            //Thread that receives messages from server to client
            workerIn.start();
            //Thread that sends "i'm alive messages to the server every minute"
            //imav.start();

            workerIn.join();
            workerOut.join();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException f) {
            f.printStackTrace();
        } catch (InterruptedException g) {
            g.printStackTrace();
        }
    }
    public static void checkUsername(String username){
        if (username.length() > 12 || !username.matches("[a-zA-Z_0-9]*")){
            System.out.println("Username doesn't fit requirements, try again!\n");
            System.exit(0);
        }
    }
}