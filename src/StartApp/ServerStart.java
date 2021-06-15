package StartApp;

import Server.ServerThread;

/*
* @className: Main.ServerStart
* @description: 启动服务器的类
 */
public class ServerStart {
    public static void main(String[] args) {
        new Thread(new ServerThread(9000)).start();
    }
}
