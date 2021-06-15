package Server;

import Message.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
* @className: ServerThread
* @description: 服务端的线程类
 */
public class ServerThread implements Runnable{
    private ServerSocket serverSocket;
    private ServerManager serverManager;
    private int port;
    private int backlog;
    public ServerThread(int port,int backlog){
        this.port = port;
        this.backlog = backlog;
    }
    public ServerThread(int port){
        this(port,10);
    }
    @Override
    public void run(){
        try {
            serverSocket = new ServerSocket(this.port, this.backlog);
            serverManager = new ServerManager(serverSocket, this.backlog);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(new ServerConnect(socket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if(serverSocket != null){
                    serverSocket.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    /*
     * @className: ServerConnect
     * @description: 当一个客户端与服务器连接成功后 创建该内部类实现通信
     */
    class ServerConnect implements Runnable{
        private Socket socket;
        private String name; // 连接服务器的用户名信息
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        public ServerConnect(Socket socket){
            this.socket = socket;
        }
        @Override
        public void run(){
            try {
                ois = new ObjectInputStream(socket.getInputStream()); // 读取客户端向服务器发送消息的流
                oos = new ObjectOutputStream(socket.getOutputStream()); // 向客户端发送数据的流
                while (true){
                    try {
                        Message message = (Message) ois.readObject();
                        // 输出服务器中转的消息
                        System.out.println(message);
                        if(message.getType() == 0){
                            // 服务端与客户端交互的特殊消息
                            if(message.getMessage().equals("login")){
                                // 登录的消息
                                this.name = message.getUsername();
                                serverManager.addSocket(message.getUsername(),new SocketDate(socket,oos));
                                // 登录成功的消息不仅自己找到，其他客户端也要知道
                                serverManager.sendMessage(name,new Message(name, "PermitLogin:"+serverManager.getAllUserName(),0,null));
                                serverManager.broadcastMessage(name,new Message(name, "PermitLogin:"+serverManager.getAllUserName(),0,null));
                                System.out.println("用户:"+this.name+" 已登陆成功"+" 当前用户数: "+serverManager.getSize()+" "+serverManager.getAllUserName());
                            }else if(message.getMessage().equals("exit")){
                                serverManager.sendMessage(name,new Message(name,"PermitExit",0,null));
                                System.out.println("用户:"+this.name+" 退出");
                            }
                        }else if(message.getType() == 1){
                            // 群聊消息
                            serverManager.broadcastMessage(message.getUsername(),message);
                        }else if(message.getType() == 2){
                            // 私聊消息
                            serverManager.sendMessage(message.getAcceptName(),message);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try{
                    if(ois!=null){
                        // socket编程io流的读和写是绑定在一起的，关闭其中任意一个，另外1个会自动关闭
                        ois.close();
                    }
                    if(oos!=null){
                        oos.close();
                    }
                    serverManager.removeSocket(name);// 移除当前客户端
                    System.out.println("当前主机数:"+serverManager.getSize()+" "+serverManager.getAllUserName());
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
