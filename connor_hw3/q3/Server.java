package q3;

import java.io.*;
import java.net.*;

public class Server {
  public static void main(String[] args) throws IOException {
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
    String inventoryFile = args[2];


    OnlineStore store = new OnlineStore(inventoryFile);

    UDPServer udpServer = new UDPServer(udpPort, store);
    udpServer.start();

    TCPServer tcpServer = new TCPServer(tcpPort, store);
    tcpServer.start();

  }

  private static class UDPServer extends Thread {
    private static final int BUFFER_SIZE = 5000;
    private DatagramSocket socket;
    private OnlineStore store;
    private byte[] buffer;

    UDPServer(int port, OnlineStore store) throws SocketException {
      this.store = store;
      this.buffer = new byte[BUFFER_SIZE];
      this.socket = new DatagramSocket(port);
    }

    @Override
    public void run() {
      DatagramPacket packet;
      while (true) {
        packet = new DatagramPacket(buffer, buffer.length);
        try {
          socket.receive(packet);
          Thread thread = new Thread(new UDPThread(store, socket, packet));
          thread.start();
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }


  private static class TCPServer extends Thread{
    private ServerSocket socket;
    private OnlineStore store;

    TCPServer(int port, OnlineStore store) throws IOException {
      this.store = store;
      socket = new ServerSocket(port);
    }

    @Override
    public void run(){
      while(true){
        try {
          Socket stream = socket.accept();
          Thread thread = new Thread(new TCPThread(store, stream));
          thread.start();
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    }
  }

  private static class UDPThread implements Runnable{
    private final OnlineStore store;
    private final DatagramSocket socket;
    private final DatagramPacket packet;
    UDPThread(OnlineStore store, DatagramSocket socket, DatagramPacket packet){
      this.store = store;
      this.socket = socket;
      this.packet = packet;
    }

    @Override
    public void run(){
      String message = "Error";

      try {
        message = new String(packet.getData(), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
        return;
      }
      System.out.println(message);
      String reply = applyChanges(store, message);
      if(reply == null){
        System.out.println("Error");
        return;
      }
      try {
        byte[] returnBytes = reply.getBytes("UTF-8");
        DatagramPacket returnPacket = new DatagramPacket(returnBytes, returnBytes.length, packet.getAddress(), packet.getPort());
        socket.send(returnPacket);
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
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
            System.out.println("Error");
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
