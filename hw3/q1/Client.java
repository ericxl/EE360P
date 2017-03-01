package q3;

import java.util.Scanner;
import java.net.*;
import java.io.*;

public class Client {
  public static void main(String[] args) {
    String hostAddress;
    int tcpPort;
    int udpPort;
    DatagramSocket udpSocket;
    Socket tcpSocket;
    InetAddress addr;
    String deliveryMethod = "T";

    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <hostAddress>: the address of the server");
      System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(3) <udpPort>: the port number for UDP connection");
      System.exit(-1);
    }

    hostAddress = args[0];
    tcpPort = Integer.parseInt(args[1]);
    udpPort = Integer.parseInt(args[2]);
    try {
      addr = InetAddress.getByName(hostAddress);
      udpSocket = new DatagramSocket();
      tcpSocket = new Socket(addr, tcpPort);
      Scanner sc = new Scanner(System.in);
      while (sc.hasNextLine()) {
        String cmd = sc.nextLine();
        String[] tokens = cmd.split(" ");


        if (tokens[0].equals("setmode")) {
          if (tokens[1].equals("U")) {
            deliveryMethod = "U";
            System.out.println("Protocol is UDP\n");
          } else if (tokens[1].equals("T")) {
            System.out.println("Protocol is TCP\n");
          } else {
            System.out.println("ERROR: Input must be either 'T' or 'U'");
          }
        } else if (tokens[0].equals("purchase") && (tokens.length == 4)) {
          String purchase = tokens[0];
          String userName = tokens[1];
          String productName = tokens[2];
          String quantity = tokens[3];
          String dataToSend = purchase + " " + userName + " " + productName + " " + quantity;
          sendData(dataToSend, deliveryMethod, udpSocket, tcpSocket, addr, udpPort);
        } else if (tokens[0].equals("cancel")) {
          String cancel = tokens[0];
          String orderId = tokens[1];
          String dataToSend = cancel + " " + orderId;
          sendData(dataToSend, deliveryMethod, udpSocket, tcpSocket, addr, udpPort);
        } else if (tokens[0].equals("search")) {
          String search = tokens[0];
          String userName = tokens[1];
          String dataToSend = search + " " + userName;
          sendData(dataToSend, deliveryMethod, udpSocket, tcpSocket, addr, udpPort);
        } else if (tokens[0].equals("list")) {
          String list = tokens[0];
          String dataToSend = list;
          sendData(dataToSend, deliveryMethod, udpSocket, tcpSocket, addr, udpPort);
        } else {
          System.out.println("ERROR: No such command");
        }
      }
      udpSocket.close();
      tcpSocket.close();
    } catch (UnknownHostException e) {
    } catch (SocketException e) {
    } catch (IOException e) {
    }
  }

  private static void sendData(String dataToSend, String deliveryMethod, DatagramSocket udpSocket, Socket tcpSocket, InetAddress addr, int udpPort) throws IOException {
    if (deliveryMethod.equals("U")) {
      DatagramPacket packet = new DatagramPacket(dataToSend.getBytes(), dataToSend.length(), addr, udpPort);
      udpSocket.send(packet);
      byte[] receiveBuffer = new byte[200];
      DatagramPacket serverReply = new DatagramPacket(receiveBuffer, receiveBuffer.length);
      udpSocket.receive(serverReply);
      String response = new String(serverReply.getData(), "UTF-8");
      System.out.println(response);
    } else {
      PrintWriter output = new PrintWriter(tcpSocket.getOutputStream());
      Scanner serverReply = new Scanner(tcpSocket.getInputStream());
      output.println(dataToSend);
      output.flush();
      while (serverReply.hasNextLine()) {
        String response = serverReply.nextLine();
        if (response.equals("done")) {
          break;
        }
        System.out.println(response);
      }
    }
  }
}
