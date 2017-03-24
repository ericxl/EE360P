import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Server {
    int serverId;
    String[] serverAddresses;

    Map<Integer, Socket> serverConnections = new HashMap<>();

    Map<String, Integer> inventory;
    Map<Integer, String> orders = new HashMap<>();
    Map<String, ArrayList<Integer>> user_orders = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (args.length == 1) {
            String configFilename = args[0];
            try {
                sc = new Scanner(new File(configFilename));
            }
            catch(Exception e) {e.printStackTrace();}
        } else {
            System.out.println("ERROR: Provide 1 arguments");
            System.out.println("\t(1) <serverCfgFilePath>: the file path to server configuration number for TCP connection");
            System.exit(-1);
        }
        int myID = sc.nextInt();
        int numServer = sc.nextInt();
        String inventoryPath = sc.next();
        String[] addresses = new String[numServer];

        System.out.println("[DEBUG] my id: " + myID);
        System.out.println("[DEBUG] numServer: " + numServer);
        System.out.println("[DEBUG] inventory path: " + inventoryPath);

        for (int i = 0; i < numServer; i++) {
            String str = sc.next();
            addresses[i] = str;
            System.out.println("address for server " + i + ": " + str);
        }

        Scanner scan = null;
        Map<String, Integer> inv = null;
        try {
            scan = new Scanner(new FileReader(new File("").getAbsolutePath() + "/"+
                    "hw4/q1/" +
                    inventoryPath));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        inv = new HashMap<>();
        while (scan.hasNextLine()) {
            String[] tokens = scan.nextLine()
                    .split(" ");
            if (tokens.length != 2) continue;
            Integer amount = Integer.parseInt(tokens[1]);
            inv.put(tokens[0], amount);
        }

        new Server(inv, myID, addresses);
    }

    public Server(Map<String, Integer> inv, int serverID, String[] addresses){
        inventory = new ConcurrentHashMap<>(inv);
        serverAddresses = addresses;
        this.serverId = serverID;


        ServerSocket serverSoc = null;
        try {
            int myPort = Integer.parseInt(serverAddresses[serverID - 1].split(":")[1]);
            serverSoc = new ServerSocket(myPort);

        }
        catch (IOException e){
            e.printStackTrace();
        }


        for(int i = 0; i < serverAddresses.length; i++){
            if(i+1 != serverID){
                String[] host = serverAddresses[i].split(":");

                String address = host[0];
                InetAddress ia = null;
                try{ ia = InetAddress.getByName(address);} catch(Exception e){}
                int port = Integer.parseInt(host[1]);

                while (true){
                    try {
                        Socket s = new Socket(ia, port);
                        serverConnections.put(new Integer(i+1), s);

                        System.out.println("serverid " + (i+1) + " is connected");
                        break;
                    }catch (IOException e){
                        //System.out.print("Server "+ Integer.toString(i+1) + " not up yet.\n");
                    }
                }

            }
        }

        while (true) {
            try {
                Socket clientSocket = serverSoc.accept();
                Thread t = new Thread(new TCPRunnable(clientSocket));
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class TCPRunnable implements Runnable {
        Socket s;
        DataInputStream reader;
        DataOutputStream writer;

        public TCPRunnable (Socket socket){
            s = socket;

            try {
                reader = new DataInputStream(socket.getInputStream());
                writer = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            /*
            while(true){
                try{
                    String msg = reader.readUTF();

                    Future<String> s = threadPool.submit(new ClientHandler(msg));
                    String output = s.get();
                    writer.writeUTF(output);
                    writer.flush();
                }
                catch (IOException e) {
                    try {
                        reader.close();
                    }
                    catch(Exception ex){}
                    try {
                        writer.close();
                    }
                    catch(Exception ex){}
                    try {
                        s.close();
                    }
                    catch(Exception ex){}
                    break;
                }
                catch (InterruptedException | ExecutionException e)
                {
                    e.printStackTrace();
                }

            }*/
        }
    }

}
