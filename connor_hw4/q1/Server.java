import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Server {

  private static int myID;
  private static Clock clock;
  private static OnlineStore store;
  private static int numServer;
  private static String inventoryFile;
  private static List<String> ipAddresses;
  private static List<Integer> portNumbers;
  private static Boolean csFlag;


  public static void main(String[] args) throws IOException{
    if(args.length != 1){
      System.out.println("No .cfg file provided");
      System.exit(-1);
    }

    String configFile = args[0];
    Scanner sc = new Scanner(new FileReader(configFile));
    ipAddresses = new ArrayList<>();
    portNumbers = new ArrayList<>();
    myID = sc.nextInt();
    numServer = sc.nextInt();
    inventoryFile = sc.next();
    clock = new Clock(); //clock for lamport algo
    csFlag = false; //critical section flag
    Comparator<TimeStamp> queue = new TimeComparator();


    System.out.println("[DEBUG] my id: " + myID);
    System.out.println("[DEBUG] numServer: " + numServer);
    System.out.println("[DEBUG] inventory path: " + inventoryFile);

    store = new OnlineStore(inventoryFile);

    for (int i = 0; i < numServer; i++) {
      String serverAddresses = sc.next();
      String addresses[] = serverAddresses.split(":");
      ipAddresses.add(addresses[0]);
      portNumbers.add(Integer.parseInt(addresses[1]));
      System.out.println("address for server " + i + ": " + serverAddresses);
    }


    TCPServer tcpServer = new TCPServer();
    tcpServer.start();

    while(true){
      //TCPServer tcpServer = new TCPServer();
    }
    // TODO: start server socket to communicate with clients and other servers

    // TODO: parse the inventory file

    // TODO: handle request from client
  }

  private static class TCPServer extends Thread {
    List<Thread> tcpServers;
    private ServerSocket socket;

    TCPServer() throws IOException {
      for(int port : portNumbers){
        socket = new ServerSocket(port);
      }
    }

    @Override
    public void run() {
      tcpServers = new ArrayList<Thread>();
      while (true) {
        try {
          Socket stream = socket.accept();
          Thread thread = new Thread(new TCPThread(store, stream));
          tcpServers.add(thread);
          thread.start();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static class TCPThread implements Runnable{
    private final OnlineStore store;
    private final Socket socket;

    TCPThread(OnlineStore store, Socket socket){
      this.store = store;
      this.socket = socket;
    }
    @Override
    public void run(){
      try{
        BufferedReader streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter streamOut = new PrintWriter(socket.getOutputStream());
        while(true){
          String message = streamIn.readLine();
          String reply = applyChanges(store, message);
          if (reply == null){
            continue;
          }
          reply += "\ndone";
          streamOut.println(reply);
          streamOut.flush();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static String applyChanges(OnlineStore store, String toDo){
    if(toDo == null) return null;
    String[] tokens = toDo.split(" ");
    String command = tokens[0].trim();
    if(command.equals("purchase")){
      return store.purchase(tokens[1].trim(),
              tokens[2].trim(), Integer.parseInt(tokens[3].trim()));
    }
    else if(command.equals("cancel")){
      return store.cancel(Integer.parseInt(tokens[1].trim()));
    }
    else if(command.equals("search")){
      return store.search(tokens[1].trim());
    }
    else if (command.equals("list")){
      return store.list();
    }
    return null;
  }
}