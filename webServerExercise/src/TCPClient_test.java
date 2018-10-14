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
        String msgIn = "";
        InputStream input = null;
        OutputStream output = null;
        boolean accept = true;
        String username;
        int server_port;
        String server_ip;
        Socket socket = null;
        String cmdCheck = "";
        do {
            do {
                System.out.println("=============CLIENT==============");

                //Check join command and username
                do {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("To join a server, type: JOIN <<user_name>>, <<server_ip>>:<<server_port>>");
                    String join = sc.nextLine();
                    cmdCheck = join.substring(0, 4);
                    String[] command = join.split("[,:]");
                    String temp_username = command[0];
                    username = temp_username.substring(5);
                    String temp_ipToConnect = command[1];
                    server_ip = temp_ipToConnect.substring(1);
                    server_port = Integer.parseInt(command[2]);
                } while (checkUsername(username) || commandCheck(cmdCheck));
                final int PORT_SERVER = server_port;
                final String IP_SERVER_STR = server_ip.equals("0") ? "127.0.0.1" : server_ip;

                InetAddress ip = null;
                try {
                    ip = InetAddress.getByName(IP_SERVER_STR);

                    System.out.println("\nConnecting...");
                    System.out.println("SERVER IP: " + IP_SERVER_STR);
                    System.out.println("SERVER PORT: " + PORT_SERVER);
                    System.out.println("USERNAME: " + username + "\n");

                    //Sends ip, port and username to server
                    socket = new Socket(ip, PORT_SERVER);
                    output = socket.getOutputStream();
                    input = socket.getInputStream();

                    byte[] username_out = username.getBytes();
                    output.write(username_out);
                    byte[] dataIn = new byte[1024];
                    input.read(dataIn);
                    msgIn = new String(dataIn);
                    msgIn = msgIn.trim();
                    System.out.println(msgIn);
                    if (msgIn.contains("J_OK")) {
                        accept = false;
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException f) {
                    f.printStackTrace();
                }
            } while (accept);
            try {
                ClientWorkerOut workerOut = new ClientWorkerOut(output);
                ClientWorkerIn workerIn = new ClientWorkerIn(input);

                /*OutputStream finalOutput = output;
                Thread IMAV = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String msgToSend = "IMAV";
                            byte[] dataToSend = msgToSend.getBytes();
                            finalOutput.write(dataToSend);
                            Thread.sleep(60000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException f){
                            f.printStackTrace();
                        }
                    }
                }
            });
*/
                //Thread that sends messages from client to server
                workerOut.start();
                //Thread that receives messages from server to client
                workerIn.start();

                //IMAV.start();


                workerIn.join();
                workerOut.join();
                //IMAV.join();

            } catch (InterruptedException g) {
                g.printStackTrace();
            }
        }while (socket.isBound());
    }
    public static boolean checkUsername(String username) {
        if (!username.matches("[a-zA-Z0-9\\-_]{1,12}$")){
            System.out.println("Username doesn't fit requirements, try again!\n");
            return true;
        }
        return false;
    }
    public  static  boolean commandCheck(String cmdCheck){
        if (!cmdCheck.contains("JOIN")) {
            System.out.println("Bad command, try again!");
            return true;
        }
        else if (cmdCheck.contains("QUIT")){
            System.exit(0);
        }
        return false;
    }
}