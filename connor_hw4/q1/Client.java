import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
  public static void main (String[] args) {
    Scanner sc = new Scanner(System.in);
    int numServer = sc.nextInt();

    String hostAddress;
    int tcpPort;
    InetAddress addr;
    Socket tcpSocket;
    int serverLocation = 0;

    List<String> addresses = new ArrayList<>();

    for (int i = 0; i < numServer; i++) {
      String str = sc.next();
      addresses.add(str);
      // TODO: parse inputs to get the ips and ports of servers
    }

    while (sc.hasNextLine()) {
/*      String cmd = sc.nextLine();
      String[] tokens = cmd.split(" ");*/
      try {
        String split[] = addresses.get(serverLocation).split(":");
        hostAddress = split[0];
        tcpPort = Integer.parseInt(split[1]);
        addr = InetAddress.getByName(hostAddress);

        tcpSocket = new Socket(addr, tcpPort);
        String cmd = sc.nextLine();
        if(!cmd.equals("")) {
          processClient(cmd, tcpSocket, addr);
        }
        tcpSocket.close();
      }
        catch(UnknownHostException e){
        }
        catch(SocketException e){
        }
        catch(IOException e){
        }
      }
    }

  private static void processClient(String cmd, Socket tcpSocket, InetAddress addr)throws IOException{
    String[] tokens = cmd.split(" ");
    if (tokens[0].equals("purchase") && (tokens.length == 4)) {
      String purchase = tokens[0];
      String userName = tokens[1];
      String productName = tokens[2];
      String quantity = tokens[3];
      String dataToSend = purchase + " " + userName + " " + productName + " " + quantity;
      sendData(dataToSend, tcpSocket, addr);
    } else if (tokens[0].equals("cancel")) {
      String cancel = tokens[0];
      String orderId = tokens[1];
      String dataToSend = cancel + " " + orderId;
      sendData(dataToSend, tcpSocket, addr);
    } else if (tokens[0].equals("search")) {
      String search = tokens[0];
      String userName = tokens[1];
      String dataToSend = search + " " + userName;
      sendData(dataToSend, tcpSocket, addr);
    } else if (tokens[0].equals("list")) {
      String list = tokens[0];
      String dataToSend = list;
      sendData(dataToSend, tcpSocket, addr);
    } else {
      System.out.println("ERROR: No such command");
    }
  }
  private static void sendData(String dataToSend, Socket tcpSocket, InetAddress addr) throws IOException {
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
