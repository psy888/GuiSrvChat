package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerSocketThread implements Runnable {
    private static final int PORT = 8778;


    private HashMap<String, ClientThread> clients = new HashMap<>();
    private ServerSocket server;
    private Thread serverInfoThread;




    @Override
    public void run() {
        boolean isInterrupted = false;
        try {
            server = new ServerSocket(PORT);
            serverInfoThread = new Thread(new ServerSocketInfoThread(this));
            serverInfoThread.setDaemon(true);
            serverInfoThread.start();

            System.out.println("Server is started");
            while (!isInterrupted) {
                if(server.isClosed()) break;
                Socket client = server.accept();
                ClientThread clientThread = new ClientThread(client, this);
                Thread thread = new Thread(clientThread);
                thread.setDaemon(true);
                thread.start();
            }

        } catch (IOException e) {
            isInterrupted = true;
            System.out.println("Сервер остановлен");
//            e.printStackTrace();
        }


    }



    public boolean addClient(String name, ClientThread client) {
        if (clients.get(name) == null) {
            this.clients.put(name, client);
            Main.controller.setUserCount(clients.size());
            return true;
        }
        return false;
    }

    public synchronized void sendMsgToAll(String senderName, String msg) {
        msg = senderName + ": " + msg;
        //todo перебрать циклом всех клиентов и отправит сообщения
        for (Map.Entry<String, ClientThread> entry : clients.entrySet()) {
            try {
                entry.getValue().sendMsg(msg);
                System.out.println("Messages log: " + msg); //todo add date and time
            } catch (IOException ioe) {
                System.out.println("Ошибка отправки сообщения пользоваелю " + entry.getValue().getName());
            }
        }

    }

    public synchronized void sendPrivateMsg(String senderName, String recipientName, String msg) {
        msg = "private from " + senderName + " : " + msg;
        //todo найти получателя и отправить сообщение, если такого имени нет вернуть сообщение об ошибке
        try {
            clients.get(recipientName).sendMsg(msg);
        } catch (IOException e) {
            System.out.println("Ошибка отправки сообщения пользоваелю " + clients.get(recipientName).getName());
        }
    }

    public void removeUser(ClientThread client) {
        clients.remove(client.getName(), client);
        Main.controller.setUserCount(clients.size());
    }

    public void stopServer(){
        try {
            server.close();
            System.out.println("Server is stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, ClientThread> getClients() {
        return clients;
    }
}
