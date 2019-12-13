package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class Controller {
    Thread serverThread;
    ServerSocketThread serverSocketThread;
    @FXML
    Button start;
    @FXML
    Button stop;
    @FXML
    Text userCount;
    @FXML
    Text serverState;

    @FXML
    public void startServer(){
        start.setDisable(true);
        stop.setDisable(false);
        serverSocketThread = new ServerSocketThread();
        serverThread = new Thread(serverSocketThread);
        serverThread.setDaemon(true);
        serverThread.start();
    }

    @FXML
    public void stopServer(){
        start.setDisable(false);
        stop.setDisable(true);
        serverSocketThread.stopServer();
        serverThread.interrupt();
        System.out.println("is alive : " + serverThread.isAlive());
    }

    public void setUserCount(int cnt) {
        this.userCount.setText("" + cnt);
    }
}
