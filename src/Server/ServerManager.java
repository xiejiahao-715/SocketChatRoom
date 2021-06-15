package Server;

import Message.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/*
* @className: ServerManager
* @description: 用于管理服务器的对象
 */
public class ServerManager {
    private ServerSocket server; // 服务器对象
    private HashMap<String,SocketDate> allSocket; // 所有连接到服务器上的socket对象
    private int size; // 当前连接到服务器的主机数
    private int maxSize; // 允许连接的最大主机数 默认为10
    private HashMap<String,Socket> allOOS;// 所有的写入流对象
    public ServerManager(ServerSocket serverSocket,int maxSize){
        this.server = serverSocket;
        this.allSocket = new HashMap<String,SocketDate>();
        this.maxSize = maxSize;
        this.size = 0;
    }
    public ServerManager(ServerSocket serverSocket){
        this(serverSocket,10);
    }
    // 添加新的socket对象 其中uid 用于唯一标识一个用户
    public void addSocket(String username,SocketDate socketDate){
        this.allSocket.put(username,socketDate);
        size++;
    }
    public int getSize(){
        return this.size;
    }
    // 客户端与服务器断开连接 从管理对象中移除该对象
    public void removeSocket(String name){
        if(name!=null && !name.equals("")){
            try {
                allSocket.get(name).getSocket().close();
                this.allSocket.remove(name);
                size--;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /*
     * @description: 服务端向指定的客户端发送信息
     * @param socket: 指定的接受数据的客户端
     * @param s: 被广播的信息
     */
    public void sendMessage(String name,Message message){
        try {
            allSocket.get(name).getOos().writeUnshared(message);
            allSocket.get(name).getOos().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    * @description: 将指定客户端发送来的信息广播给其他连接到该服务器的客户端
    * @param socket: 需要广播信息的客户端
    * @param s: 被广播的信息
    */
    public void broadcastMessage(String name,Message message){
        for(String username: allSocket.keySet()){
            if(!name.equals(username)) {
                sendMessage(username,message);
            }
        }
    }
    public String getAllUserName(){
        return allSocket.keySet().toString();
    }
}
