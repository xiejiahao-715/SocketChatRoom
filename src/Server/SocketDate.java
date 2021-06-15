package Server;

import java.io.ObjectOutputStream;
import java.net.Socket;

/*
* @className: SocketData
* @description: 封装这socket对象及其输入流
 */
public class SocketDate {
    Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    ObjectOutputStream oos;
    public SocketDate(Socket socket,ObjectOutputStream oos){
        this.socket = socket;
        this.oos = oos;
    }
}
