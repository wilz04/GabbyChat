import java.net.*;
import java.util.*;

class Main {
  
  public Main() {
    Ring ring = new Ring();
    Client tmp;
    Socket client;
    try {
      ServerSocket server = new ServerSocket(3000);
      System.out.println("GabbyChat Server started!");
      do {
        client = server.accept();
        tmp = new Client(client);
        ring.add(tmp);
      } while (true);
    } catch (Exception error) {
      System.out.println("Main: " + error);
    }
  }
  
  public static void main(String args[]) {
    new Main();
  }
  
}