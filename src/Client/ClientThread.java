package Client;

import GUI.MainFrame;
import Message.Message;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;

/*
 * @className: ClientThread
 * @description: 客户端的线程类，代表着一个用户
 */
public class ClientThread implements Runnable {
    private Socket socket;
    private String host;
    private int port;
    private String name;
    private Boolean status;// 客户端在线状态
    private MainFrame mainFrame;
    private ObjectOutputStream oos; // 向服务端发送消息的流
    private ObjectInputStream ois; // 接受服务端向客户端发送消息的流


    public ClientThread(String host, int port, String name, MainFrame mainFrame) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.status = false;
        this.mainFrame = mainFrame;
        this.oos = null;
        this.ois = null;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            // 创建流对象的顺序非常重要
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            mainFrame.getLoginPanel().setIsLogin(true);
            // 创建监听线程保持连接
            new Thread(new ClientListen()).start();
        } catch (IOException e) {
            mainFrame.getLoginPanel().setIsLogin(false);
            mainFrame.getLoginPanel().setLoginProcess(false);
            e.printStackTrace();
        }
    }

    // 客户端向服务端发送信息(前提条件是服务器和客户端连接正常)
    public void sendMessage(Message message) {
        if (status && message.isLegal()) {
            try {
                this.oos.writeUnshared(message);
                this.oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getName() {
        return name;
    }

    // 关闭客户端与服务器的连接
    public void CloseClient() {
        if (status) {
            sendMessage(new Message(name, "exit", 0, null));
        }
    }

    /*
     * @className: ClientListen
     * @description: 监听服务器向客户端发送的信息
     */
    class ClientListen implements Runnable {
        @Override
        public void run() {
            try {
                status = true;
                while (status) {
                    try {
                        Message message = (Message) ois.readObject();
                        if (message.getType() == 0) {
                            if (message.getMessage().equals("PermitExit")) {
                                break;
                            } else if(message.getMessage().contains("PermitLogin")){
                                // 监听用户登陆
                                String s= message.getMessage().substring(message.getMessage().indexOf('[')+1,message.getMessage().lastIndexOf(']'));
                                mainFrame.setAllUserName(s.split(","));
                            }
                        } else {
                            mainFrame.getChatPanel().getChatMessages().append(message.getUsername()+":"+message.getMessage() + "\r\n");
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(oos != null){
                        oos.close();
                    }
                    if(ois != null){
                        ois.close();
                    }
                    socket.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
                status = false;
                mainFrame.getLoginPanel().setIsLogin(false);
                mainFrame.getLoginPanel().setLoginProcess(false);
            }
        }

    }
}


