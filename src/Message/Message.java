package Message;

import java.io.Serializable;

/*
* @className: 消息类
* @description: 为TCP协议传输的流数据定义指定的格式
 */
public class Message implements Serializable {
    private String username;// 用户名
    private String message;
    private String acceptName; // 收件人的姓名 当type = 1 是可以为null ,当type = 2是不能为null
    //type表示发送数据的类型，1表示发送的是群发消息 2表示发送的是私聊消息
    // 0 表示特殊的消息，表示客户端与服务端进行特殊的交互，比如退出功能的实现，登陆时交给服务端必要的信息，如用户名
    private int type;

    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public Message(String username,String message,int type,String acceptName){
        this.username = username;
        this.message = message;
        this.type = type;
        this.acceptName = acceptName;
    }
    public Message(String username,String message){
        this(username,message,1,null);
    }
    public Message(String username,String message,int type){
        this(username,message,type,null);
    }
    @Override
    public String toString(){
        return "用户名:"+username+" 消息:"+message+" 类型:"+type+" 收件人:"+acceptName;
    }

    // 判断所给该类型的对象是否合法
    public Boolean isLegal(){
        // 保证用户名不能为空
        if(username == null || username.strip().equals("")){
            return false;
        }else if(type == 1 && message == null){
            return false;
        }else if(type == 2 && (acceptName == null || acceptName.strip().equals(""))){
            return false;
        }else if(type == 3 && message == null){
            return false;
        }
        return true;
    }
}
