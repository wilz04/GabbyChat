class Ring extends Thread {
  
  private Client first;
  
  public Ring() {
    first = null;
    start();
  }
  
  public void add(Client newClient) {
    if (first == null) {
      first = newClient;
      first.setNext(first);
      first.start();
    } else {
      String ring = toString();
      Client tmp = first;
      while (tmp.getNext() != first) {
        tmp = tmp.getNext();
      }
      tmp.setNext(newClient);
      tmp.getNext().setNext(first);
      tmp.getNext().start();
      tmp.getNext().write("setUp|" + ring);
    }
  }
  
  public void echo(String msg) {
    first.echo(first, msg);
  }
  
  public void trim() {
    String oldRing = toString();
    
    Client tmp;
    if (first != null) {
      tmp = first;
      while (tmp.getNext() != first) {
        tmp = tmp.getNext();
      }
      tmp.setNext(null);
    }
    
    while (first != null) {
      if (first.isOver()) {
        first = first.getNext();
      } else {
        break;
      }
    }
    if (first != null) {
      tmp = first;
      while (tmp.getNext() != null) {
        if (tmp.getNext().isOver()) {
          tmp.setNext(tmp.getNext().getNext());
        }
        if (tmp.getNext() != null) {
          tmp = tmp.getNext();
        }
      }
      
      tmp = first;
      while (tmp.getNext() != null) {
        tmp = tmp.getNext();
      }
      tmp.setNext(first);
      
      String newRing = toString();
      if (!oldRing.equals(newRing)) {
        echo("setUp|" + newRing);
      }
    }
  }
  
  public String toString() {
    String ring = "";
    if (first == null) {
      ring = null;
    } else {
      Client tmp = first;
      do {
        ring += tmp.getAlias() + ">";
        tmp = tmp.getNext();
      } while (tmp != first);
      ring = ring.substring(0, ring.length() - 1);
    }
    return ring;
  }
  
  public void run() {
    try {
      do {
        trim();
        sleep(1000);
      } while (true);
    } catch (Exception error) {
      System.out.println("Ring.run: " + error);
    }
  }
  
}