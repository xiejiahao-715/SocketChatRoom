package GUI;

import Message.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ChatPanel extends JPanel implements ActionListener, KeyListener {
    private MainFrame mainFrame;
    private JTextArea chatMessages;// 聊天框里面显示的内容
    private JTextArea message;// 用户需要发送的内容
    private JButton sendButton;
    private JButton exitButton;
    private JLabel title;
    private JButton backtrackButton; // 返回到选着群聊或单聊的button
    private int type; // 为1表示群聊，为2表示单聊
    private String acceptName;// 如果是私聊，代表私聊人的名字

    public JTextArea getChatMessages() {
        return chatMessages;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    public void setType(int type){
        this.type =type;
    }

    public JLabel getTitle() {
        return title;
    }

    public ChatPanel(MainFrame mainFrame){
        this(mainFrame,1);
    }
    public ChatPanel(MainFrame mainFrame,int type){
        super();
        this.mainFrame = mainFrame;
        this.setBounds(0,0,600,400);
        this.setLayout(null);
        this.type = type;
        this.acceptName = null;

        title = new JLabel();
        title.setVerticalAlignment(SwingConstants.TOP);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        Font fontTitle=new Font("宋体", Font.PLAIN,18);
        title.setFont(fontTitle);
        title.setBounds(0,0,580,20);;
        this.add(title);

        backtrackButton = new JButton("返回");
        backtrackButton.setBounds(5,0,60,20);
        backtrackButton.addActionListener(this);
        backtrackButton.setActionCommand("backtrack");
        this.add(backtrackButton);

        Font fontText = new Font("宋体", Font.PLAIN,15);
        chatMessages = new JTextArea();
        chatMessages.setFont(fontText);
        chatMessages.setBounds(0,20,580,180);
        chatMessages.setLineWrap(true);
        chatMessages.setEditable(false);
        JScrollPane scrollPane1=new JScrollPane();//创建滚动条面板
        scrollPane1.setBounds(0,20,587,180);
        //分别设置水平和垂直滚动条自动出现  （默认是这种）
        scrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setViewportView(chatMessages);
        this.add(scrollPane1);

        message = new JTextArea();
        message.setFont(fontText);
        message.setBounds(0,199,580,130);
        message.setLineWrap(true);
        message.addKeyListener(this);
        JScrollPane scrollPane2=new JScrollPane();//创建滚动条面板
        scrollPane2.setBounds(0,199,587,130);
        //分别设置水平和垂直滚动条自动出现  （默认是这种）
        scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane2.setViewportView(message);
        this.add(scrollPane2);

        sendButton = new JButton("发送");
        sendButton.setBounds(520,330,60,30);
        sendButton.addActionListener(this);
        sendButton.setActionCommand("send");
        this.add(sendButton);

        exitButton = new JButton("退出登陆");
        exitButton.setBounds(420,330,90,30);
        exitButton.addActionListener(this);
        exitButton.setActionCommand("exit");
        this.add(exitButton);

        // 默认面板是不可以使用的
        this.setEnabled(false);
        this.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "send":
                this.send();
                break;
            case "exit":
                mainFrame.getLoginPanel().setIsLogin(false);
                mainFrame.getClient().CloseClient();
                chatMessages.setText("");
                message.setText("");
                mainFrame.setTitle("基于TCP的聊天室");
                mainFrame.getChatPanel().setEnabled(false);
                mainFrame.getChatPanel().setVisible(false);
                mainFrame.getLoginPanel().setEnabled(true);
                mainFrame.getLoginPanel().setVisible(true);
                break;
            case "backtrack":
                mainFrame.getChatPanel().setEnabled(false);
                mainFrame.getChatPanel().setVisible(false);
                mainFrame.getSelectPanel().setEnabled(true);
                mainFrame.getSelectPanel().setVisible(true);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == 10){
            this.send();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public void send(){
        String info = message.getText().strip();
        if(!info.equals("")){
            Message sendMessage = new Message(mainFrame.getClient().getName(),info,type,acceptName);
            mainFrame.getClient().sendMessage(sendMessage);
            chatMessages.append(mainFrame.getClient().getName() +":"+ info + "\r\n");
            message.setText("");
            message.requestFocusInWindow();
        }
    }
}
