import java.lang.reflect.Array;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private int tcpPort, udpPort;
    Map<String, Integer> inventory;
    Map<Integer, String> orders = new HashMap<>();
    Map<String, ArrayList<Integer>> user_orders = new HashMap<>();
    int orderid = 1;

    public Server(Map<String, Integer> inv, int tcp, int udp) {
        this.tcpPort = tcp;
        this.udpPort = udp;
        this.inventory = inv;

        //start udp
        Thread udpThread = new Thread(new UDPRunnable(udpPort));
        udpThread.start();

        //start tcp
        try {
            ServerSocket socket = new ServerSocket(tcpPort);
            while (true) {
                try {
                    Socket clientSocket = socket.accept();
                    Thread t = new Thread(new TCPRunnable(clientSocket));
                    t.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    Server (Map<String, Integer> inv) {
        this.inventory = inv;
    }

    public static void main(String[] args) {
        int tcpPort;
        int udpPort;
        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(2) <udpPort>: the port number for UDP connection");
            System.out.println("\t(3) <file>: the file of inventory");

            System.exit(-1);
        }
        tcpPort = Integer.parseInt(args[0]);
        udpPort = Integer.parseInt(args[1]);
        String fileName = args[2];

        Map<String, Integer> inv = null;
        Scanner sc = null;
        try {
            sc = new Scanner(new File(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        inv = new HashMap<>();
        while (sc.hasNextLine()) {
            String[] tokens = sc.nextLine().split(" ");
            if (tokens.length != 2) continue;
            Integer amount = Integer.parseInt(tokens[1]);
            inv.put(tokens[0], amount);
        }

        new Server(inv, tcpPort, udpPort);
    }

    String debug(String msg){
        Future<String> s = threadPool.submit(new ClientHandler(msg));
        String returnValue;
        try {
            returnValue = s.get();
        }
        catch (Exception e){
            return null;
        }
        return returnValue;
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
            while(true){
                try{
                    String msg = reader.readUTF();
                    System.out.println(msg);

                    Future<String> s = threadPool.submit(new ClientHandler(msg));
                    String output = s.get();
                    writer.writeUTF(output);
                    writer.flush();
                } catch (Exception e) {

                }

            }
        }
    }

    class UDPRunnable implements Runnable {
        DatagramSocket socket;

        public UDPRunnable(int port) {
            try {
                socket = new DatagramSocket(port);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                byte[] buff = new byte[512];
                try {
                    DatagramPacket p = new DatagramPacket(buff, buff.length);
                    socket.receive(p);
                    String msg = new String(p.getData());
                    Future<String> s = threadPool.submit(new ClientHandler(msg));
                    String f = s.get();
                    byte[] output = f.getBytes();

                    DatagramPacket response = new DatagramPacket(
                            output,
                            output.length,
                            p.getAddress(),
                            p.getPort()
                    );
                    socket.send(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ClientHandler implements Callable<String> {
        String[] tokens;
        public ClientHandler(String msg) {
            tokens = msg.trim().split("\\s+");
        }

        @Override
        public synchronized String call() {
            if(tokens[0].equals("purchase")){
                String user = tokens[1];
                String item = tokens[2];
                int want = Integer.parseInt(tokens[3]);
                if(!inventory.keySet().contains(item)){
                    return "Not Available - We do not sell this product";
                }
                Integer left = inventory.get(item);
                if(want > left){
                    return "Not Available - Not enough items";
                }
                orders.put(new Integer(orderid), user + " " + item + " " + want);
                if(!user_orders.containsKey(user)){
                    user_orders.put(user, new ArrayList<Integer>());
                }
                user_orders.get(user).add(new Integer(orderid));

                int current = left - want;
                inventory.put(item, new Integer(current));

                String response = "You order has been placed, " + orderid + " " + user + " " + item + " " + want;
                orderid++;
                return response;
            }
            else if(tokens[0].equals("cancel")){
                int id = Integer.parseInt(tokens[1]);
                if(!orders.containsKey(new Integer(id))) {
                    return id + " not found, no such order";
                }
                String order = orders.get(new Integer(id));
                String[] orderToken = order.split(" ");

                String user = orderToken[0];
                String item = orderToken[1];
                int want = Integer.parseInt(orderToken[2]);

                //delete in orders
                orders.remove(new Integer(id));

                //delete in user_orders
                if(user_orders.containsKey(user)) {
                    ArrayList<Integer> o = user_orders.get(user);
                    if(o != null){
                        if(o.contains(new Integer(id))){
                            o.remove(new Integer(id));
                        }
                    }
                }

                //update inventory
                if(inventory.containsKey(item)){
                    Integer current = inventory.get(item);
                    int newCurrent = current.intValue() + want;
                    inventory.put(item, new Integer(newCurrent));
                }

                return "Order " + id +" is canceled";
            }
            else if(tokens[0].equals("search")){
                String user = tokens[1];

                if(!user_orders.containsKey(user)){
                    return "No order found for " + user;
                }
                ArrayList<Integer> o = user_orders.get(user);
                if(o.isEmpty()){
                    return "No order found for " + user;
                }

                String response = "";
                for (int i = 0; i < o.size(); i ++){
                    Integer oId = o.get(i);
                    String order = orders.get(oId);
                    String[] orderToken = order.split(" ");
                    String build = oId + ", " + orderToken[1] + ", " + orderToken[2];
                    response = response + build + "\n";
                }
                return response.substring(0,response.lastIndexOf('\n'));
            }
            else if(tokens[0].equals("list")){
                String response = "";
                for (String key: inventory.keySet()){
                    Integer quanlity = inventory.get(key);
                    String build = key + " " + quanlity;
                    response = response + build + "\n";
                }
                return response.substring(0,response.lastIndexOf('\n'));
            }
            else {
                return "ERROR: No such command";
            }
        }
    }
}
