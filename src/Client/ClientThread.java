package Client;

import GUI.MainFrame;
import Message.Message;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

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
                        if(!mainFrame.getLoginPanel().getIsLogin()){// 如果客户端不处于登陆状态
                            sendMessage(new Message(name,"login",0,null)); // 发送登陆请求
                        }
                        Message message = (Message) ois.readObject();
                        if (message.getType() == 0) {
                            if (message.getMessage().contains("PermitExit")) {
                                // 监听用户的退出
                                // 如果要退出的用户不是本客户端 则刷新本客户端用户在线的界面 否则直接退出
                                if(!message.getUsername().contains(name)) {
                                    String s = message.getMessage().substring(message.getMessage().indexOf('[') + 1, message.getMessage().lastIndexOf(']'));
                                    String[] temp = Arrays.stream(s.split(",")).filter(x -> !x.contains(message.getUsername())).toArray(String[]::new);
                                    mainFrame.setAllUserName(temp);
                                    System.out.println("用户 " + message.getUsername() + " 退出成功");
                                    mainFrame.getAllMessages().remove(message.getUsername());
                                }else {
                                    System.out.println("用户 " + message.getUsername() + " 退出成功");
                                    break;
                                }
                            } else if(message.getMessage().contains("PermitLogin")){
                                if(message.getUsername().equals(name)){
                                    mainFrame.getLoginPanel().setIsLogin(true);
                                    System.out.println("用户 "+name+" 登陆成功");
                                }
                                // 监听用户登陆
                                String s= message.getMessage().substring(message.getMessage().indexOf('[')+1,message.getMessage().lastIndexOf(']'));
                                mainFrame.setAllUserName(s.split(","));
                            }
                        } else if(message.getType() == 1){ // 群聊消息
                            mainFrame.getAllMessages().put(null,mainFrame.getAllMessages().get(null)+message.getUsername()+":"+message.getMessage() + "\r\n");
                            if(mainFrame.getChatPanel().getType() == 1){
                                mainFrame.getChatPanel().getChatMessages().append(message.getUsername()+":"+message.getMessage() + "\r\n");
                            }
                        } else if(message.getType() == 2){ // 私聊消息
                            if(mainFrame.getAllMessages().containsKey(message.getUsername())){
                                mainFrame.getAllMessages().put(message.getUsername(),mainFrame.getAllMessages().get(message.getAcceptName())+message.getUsername()+":"+message.getMessage() + "\r\n");
                            }else {
                                mainFrame.getAllMessages().put(message.getUsername(),message.getUsername()+":"+message.getMessage() + "\r\n");
                            }
                            if(mainFrame.getChatPanel().getType() == 2){
                                mainFrame.getChatPanel().getChatMessages().append(message.getUsername()+":"+message.getMessage() + "\r\n");
                            }
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


