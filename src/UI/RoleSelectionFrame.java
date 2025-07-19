package UI;

import java.awt.*;
import javax.swing.*;

public class RoleSelectionFrame {
    private JFrame f;//窗口
    
    public RoleSelectionFrame() {
        f = new JFrame("鲜花销售系统-角色选择");
        f.setSize(400, 400);
        f.setLayout(new GridLayout(2, 1));//将主面板设置为两行一列
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        //初始化组件
        initComponents();
        f.setVisible(true);
    }
    //初始化
    private void initComponents() {
        JPanel labelPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        Font font = new Font("宋体", Font.BOLD, 20);
        JLabel roles = new JLabel("请选择你的身份");
        roles.setFont(font);
        labelPanel.add(roles);
        
        JButton storeownerButton = new JButton("花店");
        storeownerButton.setFont(font);
        //花店按钮事件
        storeownerButton.addActionListener(e -> {
            f.dispose();
            new LoginRegisterFrame("花店登录-注册", "花店");
        });
        
        JButton customerButton = new JButton("顾客");
        customerButton.setFont(font);
        //顾客按钮事件
        customerButton.addActionListener(e -> {
            f.dispose();
            new LoginRegisterFrame("顾客登录-注册", "顾客");
        });
        
        buttonPanel.add(storeownerButton);
        buttonPanel.add(customerButton);
        
        f.add(labelPanel);
        f.add(buttonPanel);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoleSelectionFrame());
    }
}