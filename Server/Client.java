import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.*;

class Client extends Thread {
  
  private String alias;
  private boolean submit;
  private Client next;
  private boolean over;
  
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  
  public Client(Socket socket) {
    alias = socket.getInetAddress().toString();
    submit = false;
    next = null;
    over = false;
    try {
      this.socket = socket;
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    } catch (Exception error) {
      System.out.println("Client: " + error);
    }
  }
  
  public boolean login(String alias, String pwd) {/*
   try {
   Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
   Connection cx = DriverManager.getConnection("jdbc:odbc:GabbyBase", "sa", "");
   PreparedStatement query = cx.prepareStatement("Select * From Users Where Alias = '" + alias + "' And Password = '" + pwd + "';");
   ResultSet rs = query.executeQuery();
   if (rs.next()) {*/
    this.alias = alias;
    if (!contains(this)) {
      submit = true;
    }/*
     }
     cx.close();
     } catch (Exception error) {
     System.out.println("Client.login: " + error);
     }*/
    return submit;
  }
  
  public String getAlias() {
    return alias;
  }
  
  public void setNext(Client next) {
    this.next = next;
  }
  
  public Client getNext() {
    return next;
  }
  
  public boolean isOver() {
    return over;
  }
  
  public void write(String msg) {
    out.print(msg + "\u0000");
    out.flush();
  }
  
  public void write(Client sender, String recv, String msg) {
    if (recv.equals(alias)) {
      write(msg);
    } else {
      if (next != sender) {
        next.write(sender, recv, msg);
      }
    }
  }
  
  public void echo(Client sender, String msg) {
    if (next != sender) {
      next.echo(sender, msg);
    }
    write(msg);
  }
  
  public boolean contains(Client client) {
    if (next != client) {
      if (next.getAlias().equals(client.getAlias())) {
        return true;
      } else {
        return next.contains(client);
      }
    } else {
      return false;
    }
  }
  
  public boolean exit() {
    try {
      socket.close();
      return true;
    } catch (Exception error) {
      System.out.println("Client.exit: " + error);
      return false;
    }
  }
  
  public void run() {
    try {
      String msg;
      StringTokenizer sentence;
      String cmd;
      String recv;
      do {
        msg = in.readLine();
        if (!msg.equals("\u0000")) {
          sentence = new StringTokenizer(msg, "|\u0000");
          cmd = sentence.nextToken();
          if (cmd.equals("login")) {
            if (!submit) {
              if (login(sentence.nextToken(), sentence.nextToken())) {
                write("login|1");
                echo(this, "add|" + alias);
              } else {
                write("login|0");
                write("alert|Usuario no valido!");
              }
            }
          } else if (cmd.equals("getOut")) {
            recv = sentence.nextToken();
            write(this, recv, "getOut|" + alias + "|" + recv);
          } else if (cmd.equals("write")) {
            recv = sentence.nextToken();
            msg = sentence.nextToken();
            if (recv.equals("all")) {
              echo(this, "write|all|" + alias + ":\n\t" + msg + "\n");
            } else {
              write("write|" + recv + "|" + alias + ":\n\t" + msg + "\n");
              write(this, recv, "write|" + alias + "|" + alias + ":\n\t" + msg + "\n");
            }
          } else if (cmd.equals("exit")) {
            break;
          }
        }
      } while (!msg.equals("\u0000"));
    } catch (Exception error) {
      System.out.println("Client.run: " + error);
    }
    exit();
    over = true;
  }
  
}