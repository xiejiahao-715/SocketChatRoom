package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/*
 * @description: 选着聊天方式
 */
public class SelectPanel extends JPanel implements ActionListener {
    private MainFrame mainFrame;
    private ArrayList<JButton> buttons;
    private JButton refreshButton;
    private JButton chatsButton;
    public SelectPanel(MainFrame mainFrame){
        super();
        this.mainFrame = mainFrame;
        this.buttons = new ArrayList<JButton>();
        this.setBounds(0,0,600,400);
        this.setEnabled(false);
        this.setVisible(false);
        this.refreshButton = new JButton("刷新");
        this.refreshButton.addActionListener(this);
        this.refreshButton.setActionCommand(" 刷新");
        this.add(refreshButton);

        this.chatsButton = new JButton("群聊");
        this.chatsButton.addActionListener(this);
        this.chatsButton.setActionCommand(" 群聊");
        this.add(chatsButton);
    }
    public void init(){
        this.setVisible(false);
        // 移除所有的组件
        for(JButton button: buttons){
            this.remove(button);
        }
        buttons.clear();
        for(int i = 0;i<mainFrame.getAllUserName().length;i++){
            if(!mainFrame.getAllUserName()[i].equals(mainFrame.getClient().getName())) {
                JButton button = new JButton(mainFrame.getAllUserName()[i]);
                button.addActionListener(this);
                this.add(button);
                this.buttons.add(button);
            }
        }
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(" 群聊")){
            mainFrame.getSelectPanel().setEnabled(false);
            mainFrame.getSelectPanel().setVisible(false);
            mainFrame.getChatPanel().getTitle().setText("群聊");
            mainFrame.getChatPanel().setType(1);
            mainFrame.getChatPanel().setAcceptName(null);
            mainFrame.getChatPanel().setVisible(true);
            mainFrame.getChatPanel().setVisible(true);
        }else if(e.getActionCommand().equals(" 刷新")){
            init();
        }else {
            // 单聊
            mainFrame.getSelectPanel().setEnabled(false);
            mainFrame.getSelectPanel().setVisible(false);
            mainFrame.getChatPanel().getTitle().setText("私聊:"+e.getActionCommand());
            mainFrame.getChatPanel().setType(2);
            mainFrame.getChatPanel().setAcceptName(e.getActionCommand());
            mainFrame.getChatPanel().setVisible(true);
            mainFrame.getChatPanel().setVisible(true);
        }
    }
}
