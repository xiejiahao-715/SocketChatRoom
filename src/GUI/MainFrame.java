package GUI;

import Client.ClientThread;

import javax.swing.JFrame;

public class MainFrame extends JFrame{
    public int port;
    private MainFrame mainFrame;
    private LoginPanel loginPanel;
    private ChatPanel chatPanel;
    public SelectPanel getSelectPanel() {
        return selectPanel;
    }
    public void setSelectPanel(SelectPanel selectPanel) {
        this.selectPanel = selectPanel;
    }
    private SelectPanel selectPanel;
    private ClientThread client;// 一个客户端管理一个client
    private String[] allUserName;
    public MainFrame getMainFrame() {
        return mainFrame;
    }
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    public LoginPanel getLoginPanel() {
        return loginPanel;
    }
    public void setLoginPanel(LoginPanel loginPanel) {
        this.loginPanel = loginPanel;
    }
    public ChatPanel getChatPanel() {
        return chatPanel;
    }
    public void setChatPanel(ChatPanel chatPanel) {
        this.chatPanel = chatPanel;
    }
    public ClientThread getClient(){
        return client;
    }
    public void setClient(ClientThread client){
        this.client = client;
    }
    public String[] getAllUserName() {
        return allUserName;
    }
    public void setAllUserName(String[] allUserName) {
        for (int i = 0;i < allUserName.length;i++){
            allUserName[i] = allUserName[i].strip();
        }
        this.allUserName = allUserName;
        this.selectPanel.init();
    }
    public MainFrame(int port){
        super();
        mainFrame = this;
        this.port = port;
        loginPanel = new LoginPanel(mainFrame);
        chatPanel = new ChatPanel(mainFrame);
        selectPanel = new SelectPanel(mainFrame);
        this.add(loginPanel);
        this.add(chatPanel);
        this.add(selectPanel);
        // 默认添加第一个面板
        loginPanel.setEnabled(true);
        loginPanel.setVisible(true);
        // 设置主界面的样式
        this.setTitle("基于TCP的聊天室");
        this.setSize(600,400);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
