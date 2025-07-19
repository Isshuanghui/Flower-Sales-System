package UI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.*;

import BI.BIFactory;
import BI.CustomerService;
import BI.FlowerStoreService;


public  class LoginRegisterFrame extends JFrame implements ActionListener {
    protected JFrame frame;
	//创建选项卡式界面，允许用户在不同界面之间切换
    protected JTabbedPane tabbedPane;
    protected JTextField username1;//用户名
    protected JPasswordField pwd1;//密码
    protected JTextField username2;//用户名
    protected JPasswordField pwd2;//密码
    protected JTextField name;
    protected JTextField phone;
    protected JTextField address;
    protected JLabel message1;//提示
    protected JLabel message2;//提示
    protected final String roleName;
    private final CustomerService customerService;
    private final FlowerStoreService storeService;

    public LoginRegisterFrame(String title,String roleName) {   	
        frame = new JFrame(title);
        this.roleName = roleName;
        customerService=roleName.equals("顾客")?BIFactory.getCustomerService():null;
        storeService=roleName.equals("花店")?BIFactory.getFlowerStoreService():null;
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        initComponents();
        frame.setVisible(true);
    }

    // 通用 UI 初始化
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        // 添加登录面板
        tabbedPane.addTab("登录", createLoginPanel());
        // 添加注册面板
        tabbedPane.addTab("注册", createRegisterPanel());
        frame.add(tabbedPane);
    }

    // 通用登录面板（可根据需求调整）
    private JPanel createLoginPanel() {
		//将面板划分为五行两列，
        JPanel panel = new JPanel(new GridLayout(5, 2, 20, 20));
    	//设置面板边框
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel(roleName + "名:"));
        username1=new JTextField();
        username1.setPreferredSize(new Dimension(150, 20));
        panel.add(username1);
        panel.add(new JLabel("密  码:"));
        pwd1 = new JPasswordField();
        pwd1.setPreferredSize(new Dimension(150, 20)); // 正确设置密码框尺寸
        panel.add(pwd1);
        JButton login= new JButton("登录");
        login.addActionListener(this);
        panel.add(login);
        JButton cancel=new JButton("取消");
        cancel.addActionListener(this);
        panel.add(cancel);
        message1 = new JLabel("");
        message1.setForeground(Color.RED);
        panel.add(message1);
        panel.add(new JLabel("")); // 占位
        return panel;
    }

    // 通用注册面板（可抽象为方法，子类按需扩展）
    protected JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        //设置面板边框
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel(roleName + "名:"));
        username2=new JTextField();
        panel.add(username2);
        panel.add(new JLabel("密    码:"));
        pwd2=new JPasswordField();
        panel.add(pwd2);
        panel.add(new JLabel("姓   名:"));
        name=new JTextField();
        panel.add(name);
        panel.add(new JLabel("手机号:"));
        phone=new JTextField();
        panel.add(phone);
        panel.add(new JLabel("地   址:"));
        address=new JTextField();
        panel.add(address);
        JButton register=new JButton("注册");
        register.addActionListener(this);
        panel.add(register);
        JButton cancel=new JButton("取消");
        cancel.addActionListener(this);
        panel.add(cancel);
        message2 = new JLabel("");
        message2.setForeground(Color.RED);
        panel.add(message2);
        panel.add(new JLabel("")); // 占位
        return panel;
    }

    // 通用事件处理
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "登录":
                handleLogin();
                break;
            case "注册":
                handleRegister();
                break;
            case "取消":
                clearFields();
                break;
        }
    }
    // 通用登录逻辑（调用抽象方法，由子类实现具体服务）
    private void handleLogin() {
        String usernameText = username1.getText().trim();
        String passwordText = new String(pwd1.getPassword());       
        
        // 前端验证
        if (usernameText.isEmpty()) {
            message1.setText(roleName + "名不能为空");
            return;
        }        
        if (passwordText.isEmpty()) {
            message1.setText("密码不能为空");
            return;
        }   
        
        // 调用业务逻辑层的登录方法
        try {          
            boolean success = false;  
            if(roleName.equals("花店")) {
            	 success = storeService.ALogin(usernameText, passwordText);
            }else if(roleName.equals("顾客")) {
            	success = customerService.Login(usernameText, passwordText);
            }
            if (success) {
                message1.setForeground(Color.GREEN);
                message1.setText("登录成功,正在跳转页面...");               
                // 延迟跳转，给用户看到成功消息
                Timer timer = new Timer(1500, ae -> {
                    if (roleName.equals("花店")) {
                        new StoreOwnerMainFrame(usernameText);
                        frame.dispose(); // 关闭登录窗口
                    } else {
                        new CustomerMainFrame(usernameText);
                        frame.dispose(); // 关闭登录窗口
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                message1.setForeground(Color.RED);
                message1.setText("用户名或密码错误");
            }
        } catch (IllegalArgumentException e) {
            message1.setForeground(Color.RED);
            message1.setText("输入错误: " + e.getMessage());
        } catch (Exception e) {
            message1.setForeground(Color.RED);
            message1.setText("登录失败: " + e.getMessage());
            e.printStackTrace(); // 打印完整异常栈，便于调试
        }
    }

    // 通用注册逻辑（调用抽象方法，由子类实现具体服务）
    private void handleRegister() {
        String usernameText = username2.getText().trim();
        String passwordText = new String(pwd2.getPassword());
        String nameText = name.getText().trim();
        String phoneText = phone.getText().trim();
        String addressText = address.getText().trim();       
        // 前端验证
        if (usernameText.isEmpty()) {
        	message2.setText(roleName + "名不能为空");
            return;
        }        
        if (passwordText.isEmpty()) {
            message2.setText("密码不能为空");
            return;
        }        
        // 密码长度验证
        if (passwordText.length() != 6 || !Pattern.matches("\\d{6}", passwordText)) {
            message2.setText("密码必须为6位数字");
            return;
        }        
        if (nameText.isEmpty()) {
            message2.setText("姓名不能为空");
            return;
        }        
        // 手机号验证
        if (!Pattern.matches("\\d{11}", phoneText)) {
            message2.setText("手机号必须为11位数字");
            return;
        }        
        if (addressText.isEmpty()) {
            message2.setText("地址不能为空");
            return;
        } 
        // 调用业务逻辑层的注册方法
        try {         
            boolean success = false;
            if(roleName.equals("花店")) {
            	success=storeService.ARegister(
                        usernameText, passwordText, nameText, phoneText, addressText
                        ); 
            }else if(roleName.equals("顾客")) {
            	success=customerService.Register(
                        usernameText, passwordText, nameText, phoneText, addressText
                        ); 
            }
            if (success) {
                message2.setForeground(Color.GREEN);
                message2.setText("注册成功，请登录");                
                // 注册成功后切换到登录选项卡
                tabbedPane.setSelectedIndex(0);
                clearFields();
            } else {
                message2.setForeground(Color.RED);
                message2.setText("注册失败，请重试");
            }
        } catch (IllegalArgumentException e) {
            message2.setForeground(Color.RED);
            message2.setText("输入错误: " + e.getMessage());
        } catch (IllegalStateException e) {
            message2.setForeground(Color.RED);
            message2.setText("注册失败: " + e.getMessage());
        } catch (Exception e) {
            message2.setForeground(Color.RED);
            message2.setText("注册失败: 未知错误");
            e.printStackTrace();
        }
    }

    // 通用清空方法
    private void clearFields() {
    	// 根据当前激活的选项卡清空对应面板的文本框
        int selectedIndex = tabbedPane.getSelectedIndex();     
        if (selectedIndex == 0) { // 登录面板
            username1.setText("");
            pwd1.setText("");
        } else if (selectedIndex == 1) { // 注册面板
        	username2.setText("");
            pwd2.setText("");
            name.setText("");
            phone.setText("");
            address.setText("");
        }
    }

}
