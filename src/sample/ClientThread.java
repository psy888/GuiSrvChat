package sample;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {
    public static final String COMMAND_EXIT = "-exit";
    public static final String COMMAND_PRIVATE = "-p";
    public static final String COMMAND_SERVER_INFO = "-server_info";
    private final static String LOGIN = "-login";
    private final static String ADDED = "ok";


    private Socket client;
    private ServerSocketThread server;
    private String userName = "";
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public ClientThread(Socket client, ServerSocketThread server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {

        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            //send greetings
//            bufferedWriter.write("Вы подключились к чату\r");
//            bufferedWriter.flush();
            //todo отправить правила пользования чатом
            //request userName

            boolean isAdded = false;
            do {
//                bufferedWriter.write("Введите ваше имя\r");
//                bufferedWriter.flush();
                userName = bufferedReader.readLine();
                System.out.println("userName = " + userName);;
                userName = userName.substring(LOGIN.length()+1).trim();
                System.out.println("userName = " + userName);;
                if (!(isAdded = addUser(userName))) {
                    bufferedWriter.write("Имя занято\r");
                    bufferedWriter.flush();
                    userName = "";
                }
//                else {
//                    bufferedWriter.write(ADDED+"\r");
//                }

            } while (!isAdded);
            bufferedWriter.write("для отправки сообщения всем введите сообщение и нажмите ввод\n" +
                    "для отправки приватного сообщения введите " + COMMAND_PRIVATE + " ИМЯ_ПОЛЬЗОВАТЕЛЯ:\n" +
                    "для выхода введите " + COMMAND_EXIT + "\r");
            bufferedWriter.flush();


            // Start Chatting
            String msg = "";
            do {
                msg = bufferedReader.readLine();
                if (msg.equalsIgnoreCase(COMMAND_EXIT)) {
                    server.sendMsgToAll("!CHAT", "Клиент " + userName + " отключился");
//                    System.out.println("Клиент " + userName + " отключился");
                    break;
                } else if (msg.contains(COMMAND_PRIVATE)) {
                    String recipientName = msg.substring(msg.indexOf(COMMAND_PRIVATE) + 3, msg.indexOf(":"));
                    System.out.println("recipientName = " + recipientName);
                    msg = msg.substring(msg.indexOf(":") + 1);
                    server.sendPrivateMsg(userName, recipientName, msg);
                } else {
                    server.sendMsgToAll(userName, msg);
                }
            } while (true);

            server.removeUser(this);

        } catch (IOException ioe) {
            server.removeUser(this);

            ioe.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                bufferedWriter.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }

    private boolean addUser(String userName) {
        return server.addClient(userName, this);
    }

    public String getName() {
        return userName;
    }

    public void sendMsg(String msg) throws IOException {
        bufferedWriter.write(msg + "\r");
        bufferedWriter.flush();
    }
}
