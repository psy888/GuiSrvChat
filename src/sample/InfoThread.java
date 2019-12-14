package sample;

import java.net.Socket;
import java.util.HashMap;

public class InfoThread extends Thread{

    private HashMap<String, ClientThread> clients;
    private Socket infoClient;

    public InfoThread (HashMap<String, ClientThread> clients, Socket infoClient){
        this.clients = clients;
        this.infoClient = infoClient;
    }

    @Override
    public void run() {


    }
}
