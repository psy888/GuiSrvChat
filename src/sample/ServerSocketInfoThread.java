package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketInfoThread implements Runnable {
    private ServerSocketThread mainServerThread;
    private ServerSocket serverInfo;

    public ServerSocketInfoThread(ServerSocketThread mainServerThread) {
        this.mainServerThread = mainServerThread;
    }

    private static final int PORT2 = 8779;



    @Override
    public void run() {
        try {
            serverInfo = new ServerSocket(PORT2);

            do {
                Socket clientInfoSocket = serverInfo.accept();
                new InfoThread(mainServerThread.getClients(), clientInfoSocket).start();
            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
