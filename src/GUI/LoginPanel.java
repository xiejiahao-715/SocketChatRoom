package GUI;

import Client.ClientThread;
import Message.Message;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LoginPanel extends JPanel implements ActionListener, KeyListener {
    private MainFrame mainFrame;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel label;
    private JTextField nameTextField;
    private Boolean isLogin;
    private Boolean loginProcess; // 登陆的进程状态 false表示未处于登陆的进程 true表示正在尝试连接服务器
    public LoginPanel(MainFrame mainFrame){
        super();
        this.isLogin=false;
        this.loginProcess = false;
        this.mainFrame = mainFrame;
        this.setBounds(0,0,600,400);
        this.setLayout(null);

        loginButton = new JButton("登陆");
        loginButton.setBounds(255,170,60,30);
        loginButton.addActionListener(this);
        loginButton.setActionCommand("login");
        this.add(loginButton);

        exitButton = new JButton("退出");
        exitButton.setBounds(255,210,60,30);
        exitButton.addActionListener(this);
        exitButton.setActionCommand("exit");
        this.add(exitButton);

        label = new JLabel("请输入用户名:");
        label.setBounds(160,140,90,20);
        this.add(label);

        nameTextField = new JTextField();
        nameTextField.setBounds(240,140,100,20);
        nameTextField.addKeyListener(this);
        this.add(nameTextField);


        // 默认面板是不可以使用的
        this.setEnabled(false);
        this.setVisible(false);
    }
    public void setIsLogin(Boolean bool){
        this.isLogin = bool;
    }
    public void setLoginProcess(Boolean bool){
        this.loginProcess = bool;
    }
    public Boolean getIsLogin(){
        return this.isLogin;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equals("login")){
            this.Login();
        }else if(command.equals("exit")){
            mainFrame.dispose();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            this.Login();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    private void Login(){
        String name = nameTextField.getText().strip();
        if(!name.equals("") && !this.loginProcess){
            this.loginProcess = true;
            System.out.println("登录:"+name);
            loginButton.setEnabled(false);
            mainFrame.setClient(new ClientThread("127.0.0.1",mainFrame.port,name,mainFrame));
            new Thread(mainFrame.getClient()).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long startTime = System.currentTimeMillis();
                    while (true){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        long endTime = System.currentTimeMillis();
                        if(!loginProcess){
                            System.out.println("请先启动服务器");
                            break;
                        } else if(endTime-startTime>5000){
                            System.out.println("登陆超时");
                            mainFrame.getLoginPanel().setEnabled(true);
                            loginProcess = false;
                            isLogin = false;
                            break;
                        }else if(isLogin){
                            // 表示登陆成功 向服务器告知该主机上登陆用户的用户名
                            mainFrame.setTitle("我的昵称:"+name);
                            nameTextField.setText("");
                            mainFrame.getLoginPanel().setEnabled(false);
                            mainFrame.getLoginPanel().setVisible(false);
                            mainFrame.getSelectPanel().setVisible(true);
                            mainFrame.getSelectPanel().setVisible(true);
                            loginProcess = false;
                            break;
                        }
                    }
                    loginButton.setEnabled(true);
                }
            }).start();
        }
    }
}
