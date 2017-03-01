import java.util.*;
import java.io.*;
import java.net.*;
public class Server {
    private static Map<String, Integer> inventory = new HashMap<>();

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
        inventory = parseInventory(fileName);

        DatagramSocket udpSocket;
        ServerSocket tcpSocket;
        try{
            udpSocket = new DatagramSocket(udpPort);
        }
        catch(SocketException e){
            e.printStackTrace();
        }

        try {
            tcpSocket = new ServerSocket(tcpPort);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private static Map<String, Integer> parseInventory(String filename) {
        Map <String, Integer> result = null;
        Scanner sc = null;
        try {
            sc = new Scanner(new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        result = new HashMap<>();
        while(sc.hasNextLine()){
            String[] tokens = sc.nextLine().split(" ");
            if(tokens.length != 2) continue;
            Integer amount = Integer.parseInt(tokens[1]);
            result.put(tokens[0], amount);
        }
        return result;
    }
}
