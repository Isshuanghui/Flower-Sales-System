package UI;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import BI.BIFactory;
import BI.CustomerService;
import BI.FlowerStoreService;
import DAO.DAOFactory;
import DAO.FlowerDAO;
import DAO.FlowerStoreDAO;
import DAO.OrderDAO;
import Entity.Flower;
import Entity.FlowerStore;
import Entity.Order;

public class StoreOwnerMainFrame  implements ActionListener {
    private JFrame frame;
    private final FlowerStoreService storeService;	// 花店业务逻辑服务类，处理核心业务
    private final CustomerService customerservice;
    private final String storename;      			//标识当前花店
    private JTable flowerTable;					 	//用于展示花店鲜花的UI组件。
    private JTabbedPane tabbedPane;      			//选项卡面板组件
    private DefaultTableModel tableModel;			//为表格提供数据支持，管理表格中的数据。
    private DefaultTableModel orderTableModel;		//管理订单表格
    private DefaultTableModel salesReportTableModel;//销售报表表格
    private DefaultTableModel salesTableModel;		//销售表格的数据模型

    Font Title = new Font("Microsoft YaHei", Font.BOLD, 24);
    Font Label = new Font("Microsoft YaHei", Font.BOLD, 18);
    
    public StoreOwnerMainFrame(String storename) {
        this.storename = storename;
        this.storeService = BIFactory.getFlowerStoreService();
        this.customerservice=BIFactory.getCustomerService();
        
        frame = new JFrame("花店管理 - " + storename);
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); 		//居中       
        initComponents();						//初始化界面组件
        frame.setVisible(true);        			//显示界面
    }
    
    private void initComponents() {
        // 主面板设置，使用BorderLayout布局
    	//内容面板是一个中间容器，位于 JFrame 的最底层，所有可见组件都必须添加到它上面。
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);								// 设置主内容面板
        
        // 创建顶部导航栏并添加到主面板北部
        JPanel navbar = createNavbar();
        mainPanel.add(navbar, BorderLayout.NORTH);
        // 创建选项卡式界面
        JTabbedPane tabbedPane = new JTabbedPane();       
        // 1. 库存管理（显示所有鲜花）
        JPanel stockPanel = createStockPanel();
        tabbedPane.addTab("查看库存",null, stockPanel,"查看鲜花库存");
        
        // 2. 销售鲜花
        JPanel salesPanel = createSalesPanel();
        tabbedPane.addTab("销售鲜花",null, salesPanel, "线下售卖鲜花");
        
        // 3. 查看订单
        JPanel ordersPanel = createOrdersPanel();
        tabbedPane.addTab("订单管理",null, ordersPanel,"查看订单");
        
        // 4. 查看销售情况
        JPanel reportsPanel = createSalesReportPanel();
        tabbedPane.addTab("销售报表",null, reportsPanel,"查看销售情况");
        
        // 5. 培育新品种
        JPanel newFlowerPanel = createNewFlowerPanel();
        tabbedPane.addTab("培育新品种",null, newFlowerPanel,"添加新品种");
        
        // 6. 入库/出库
        JPanel inventoryPanel = createInventoryPanel(); // 修改这里
        tabbedPane.addTab("库存操作", null, inventoryPanel, "入库/出库操作");
        
        frame.add(tabbedPane, BorderLayout.CENTER);
    }
    //创建顶部导航栏
    private JPanel createNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel();
        //中间标签
        JLabel label=new JLabel("首页");
        label.setFont(Title);
        centerPanel.add(label);
        navbar.add(centerPanel, BorderLayout.CENTER);
        //退出登录按钮
        JPanel rightPanel = new JPanel();
        JButton logoutButton = new JButton("退出登录");
        logoutButton.addActionListener(e -> {
            new RoleSelectionFrame();
            frame.dispose();
        });
        rightPanel.add(logoutButton);
        navbar.add(rightPanel, BorderLayout.EAST);        
        return navbar;
    }
    //查看库存
    private JPanel createStockPanel() {
    	JPanel panel = new JPanel(new BorderLayout(20, 20));
        // 标题
        JLabel titleLabel = new JLabel("鲜花库存", JLabel.LEFT);
        titleLabel.setFont(Title);
        panel.add(titleLabel, BorderLayout.NORTH);
        // 表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        // 表格列定义
        String[] columnNames = {"ID", "花名", "类型", "进价", "售价", "库存", "花语"};
        //初始化表格模型，设置不可编辑，0 表示初始行数为 0。
        tableModel = new DefaultTableModel(columnNames, 0) {
        	//重写 isCellEditable() 方法，返回 false 禁用所有单元格的编辑功能。
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };
        flowerTable = new JTable(tableModel);        //使用前面创建的表格模型初始化 JTable
        flowerTable.setRowHeight(40); // 设置行高为40像素
        
        // 添加滚动条
        JScrollPane scrollPane = new JScrollPane(flowerTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);       //在表格面板添加
        // 刷新按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("刷新库存");
        refreshButton.addActionListener(e -> refreshStockTable()); // 刷新库存数据
        buttonPanel.add(refreshButton);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);        
        panel.add(tablePanel, BorderLayout.CENTER);
        // 初始化库存数据
        refreshStockTable();        
        return panel;
    }
    //初始化库存数据
    private void refreshStockTable() {
        tableModel.setRowCount(0); // 清空表格
        try {
            List<Flower> flowers = storeService.getFStockByFSname(storename);
            int flowerid=1;
            for (Flower flower : flowers) {
                Object[] row = {
                    flowerid++,
                    flower.getName(),
                    flower.getType(),
                    flower.getPurchasePrice(),
                    flower.getSalePrice(),
                    flower.getStock(),
                    flower.getMeaning()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "获取库存数据失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    //销售鲜花(线下批发)
    private JPanel createSalesPanel() {
    	JPanel panel = new JPanel(new BorderLayout(20, 20));
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();//GridBagConstraints：定义组件在网格中的位置、大小和对齐方式的约束条件
        gbc.insets = new Insets(20, 20, 20, 20);		  //insets：设置组件四周的边距（上、左、下、右各 10 像素），避免组件紧贴在一起。
        gbc.fill = GridBagConstraints.HORIZONTAL;		  //设置填充模式为水平填充
        
        // 鲜花选择下拉框
        gbc.gridx = 0;gbc.gridy = 0;//组件位于网格第一列第一行       
        JLabel flowerLabel = new JLabel("选择鲜花:");
        flowerLabel.setFont(Label);
        formPanel.add(flowerLabel, gbc);// 将标签添加到表单面板，并应用当前约束
        
        gbc.gridx = 1;gbc.gridy = 0;//组件位于网格第二列第一行         
        JComboBox<Flower> flowerComboBox = new JComboBox<>(); // 创建泛型下拉框，存储 Flower 对象
        formPanel.add(flowerComboBox, gbc);
        
        // 数量输入框
        gbc.gridx = 0;gbc.gridy = 1;        
        JLabel quantityLabel = new JLabel("销售数量:");
        quantityLabel.setFont(Label);
        formPanel.add(quantityLabel, gbc);
        
        gbc.gridx = 1;gbc.gridy = 1;        
        JTextField num = new JTextField(20);
        formPanel.add(num, gbc);
        
        // 价格和利润显示
        gbc.gridx = 0;gbc.gridy = 2;        
        JLabel purchasePriceLabel = new JLabel("入库价:");
        purchasePriceLabel.setFont(Label);
        formPanel.add(purchasePriceLabel, gbc);
        
        gbc.gridx = 1;gbc.gridy = 2;        
        JTextField inprice = new JTextField(20);
        formPanel.add(inprice, gbc);
        
        gbc.gridx = 0;gbc.gridy = 3;        
        JLabel salePriceLabel = new JLabel("出库价:");
        salePriceLabel.setFont(Label);
        formPanel.add(salePriceLabel, gbc);
        
        gbc.gridx = 1;gbc.gridy = 3;        
        JTextField outprice = new JTextField(20);
        formPanel.add(outprice, gbc);
        
        gbc.gridx = 0;gbc.gridy = 4;       
        JLabel priceLabel = new JLabel("总价格:");
        priceLabel.setFont(Label);
        formPanel.add(priceLabel, gbc);
        
        gbc.gridx = 1;gbc.gridy = 4;        
        JTextField allprice = new JTextField(20);
        formPanel.add(allprice, gbc); 
        
        gbc.gridx = 0;gbc.gridy = 5;       
        JLabel profitLabel = new JLabel("总利润:");
        profitLabel.setFont(Label);
        formPanel.add(profitLabel, gbc);
        
        gbc.gridx = 1;gbc.gridy = 5;        
        JTextField allprofit = new JTextField(20);
        formPanel.add(allprofit, gbc);
        
        // 客户信息
        gbc.gridx = 0;gbc.gridy = 6;        
        JLabel customerLabel = new JLabel("客户姓名:");
        customerLabel.setFont(Label);
        formPanel.add(customerLabel, gbc);
        
        gbc.gridx = 1;gbc.gridy = 6;        
        JTextField name = new JTextField(20);
        formPanel.add(name, gbc);
        
        // 联系方式
        gbc.gridx = 0;gbc.gridy = 7;        
        JLabel phoneLabel = new JLabel("联系方式:");
        phoneLabel.setFont(Label);
        formPanel.add(phoneLabel, gbc);
        
        gbc.gridx = 1;gbc.gridy = 7;        
        JTextField phone = new JTextField(20);
        formPanel.add(phone, gbc);
        
        // 按钮面板
        gbc.gridx = 0;gbc.gridy = 8;        
        gbc.gridwidth = 2;//当前组件将占据 2 列的宽度。
        gbc.anchor = GridBagConstraints.CENTER;//当组件的显示区域大于其自身大小时，设置组件在该区域内的对齐方式。
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        
        JButton confirm = new JButton("确认销售");
        buttonPanel.add(confirm);
        
        JButton cancel = new JButton("重置");
        buttonPanel.add(cancel);  
        
        formPanel.add(buttonPanel, gbc);   
        
        panel.add(formPanel, BorderLayout.NORTH);
     	
     // 初始化鲜花下拉框
        try {
            List<Flower> flowers = storeService.getFStockByFSname(storename);
            for (Flower flower : flowers) {
                flowerComboBox.addItem(flower);
            }
         // 设置自定义渲染器，只显示花名
            flowerComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                             int index, boolean isSelected, 
                                                             boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Flower) {
                        setText(((Flower) value).getName()); // 只显示花名
                    }
                    return this;
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "获取鲜花列表失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
        // 监听鲜花选择事件
        flowerComboBox.addActionListener(e -> {
        	//获取用户选中的鲜花对象
            Flower selectedFlower = (Flower) flowerComboBox.getSelectedItem();
            if (selectedFlower != null) {
                inprice.setText(selectedFlower.getPurchasePrice().toString());
                outprice.setText(selectedFlower.getSalePrice().toString());
                //调用计算总价的方法
                calculateAmount(num,outprice,allprice);
                //调用计算利润的方法，更新利润显示(后面有)
                calculateProfit(num, inprice, outprice,allprofit);
            }
        });
        
        // 监听数量变化事件
        num.getDocument().addDocumentListener(new DocumentListener() {
        	 // 当文本被插入时触发
            @Override
            public void insertUpdate(DocumentEvent e) {
            	calculateAmount(num,outprice,allprice);
            	calculateProfit(num, inprice, outprice,allprofit);
            }
            // 当文本被删除时触发
            @Override
            public void removeUpdate(DocumentEvent e) {
            	calculateAmount(num,outprice,allprice);
            	calculateProfit(num, inprice, outprice,allprofit);
            }
            // 当文本被替换时触发
            @Override
            public void changedUpdate(DocumentEvent e) {
            	calculateAmount(num,outprice,allprice);
            	calculateProfit(num, inprice, outprice,allprofit);
            }
        });
        
        // 重置按钮事件
        cancel.addActionListener(e -> {           
            inprice.setText("");
            outprice.setText("");
            num.setText("");
            allprice.setText("");
            allprofit.setText("");
            name.setText("");
            phone.setText("");
        });
       // 销售按钮事件
        confirm.addActionListener(e -> {
            Flower selectedFlower = (Flower) flowerComboBox.getSelectedItem();
            if (selectedFlower == null) {
                JOptionPane.showMessageDialog(frame, "请选择要销售的鲜花", 
                        "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String quantityText = num.getText().trim();
            if (quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请输入销售数量", 
                        "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String customerName = name.getText().trim();
            if (customerName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请输入客户姓名", 
                        "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String customerphone = phone.getText().trim();
            if (customerphone.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请输入联系方式", 
                        "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            try {
                int quantity = Integer.parseInt(quantityText);
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(frame, "销售数量必须大于0", 
                            "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (quantity > selectedFlower.getStock()) {
                    JOptionPane.showMessageDialog(frame, "库存不足，当前库存: " + selectedFlower.getStock(), 
                            "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 执行销售逻辑（先出库再记录销售）
                boolean success = performSale(selectedFlower, quantity, customerName, phone, salesTableModel);
                if (success) {
                    JOptionPane.showMessageDialog(frame, "销售成功!", 
                            "成功", JOptionPane.INFORMATION_MESSAGE);
                    // 刷新库存表格
                    refreshStockTable();
                    // 刷新鲜花下拉框
                    flowerComboBox.removeAllItems();
                    List<Flower> flowers = storeService.getFStockByFSname(storename);
                    for (Flower flower : flowers) {
                        flowerComboBox.addItem(flower);
                    }
                    // 重置表单
                    num.setText("");
                    allprice.setText("");
                    allprofit.setText("");
                    name.setText("");
                    phone.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "请输入有效的销售数量", 
                        "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "销售失败: " + ex.getMessage(), 
                        "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        return panel;
    }
    //销售
	private boolean performSale(Flower selectedFlower, int quantity, String customerName, JTextField phone,
			DefaultTableModel salesTableModel) {
		try {
	        boolean saleSuccess = storeService.sellFlower(
	        		
	                storename,
	                selectedFlower.getId(),
	                quantity,
	                selectedFlower.getSalePrice(),
	                customerName,
	                phone.getText()
	        );
	        
	        if (!saleSuccess) {
	            JOptionPane.showMessageDialog(frame, "销售失败，请重试", 
	                    "错误", JOptionPane.ERROR_MESSAGE);
	            return false;
	        }        
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	//计算总利润
	private void calculateProfit(JTextField num, JTextField inprice, JTextField outprice,JTextField allprofit) {
	    try {
	        int quantity = Integer.parseInt(num.getText().trim());
	        BigDecimal inPrice = new BigDecimal(inprice.getText().trim());
	        BigDecimal outPrice = new BigDecimal(outprice.getText().trim());
	        BigDecimal profit = outPrice.subtract(inPrice).multiply(new BigDecimal(quantity));
	        allprofit.setText(profit.toString());
	    } catch (NumberFormatException e) {
	        allprofit.setText("");
	    }
	}
	//计算总价
	private void calculateAmount(JTextField num, JTextField outprice,JTextField allprice) {
	    try {
	        int quantity = Integer.parseInt(num.getText().trim());
	        BigDecimal price = new BigDecimal(outprice.getText().trim());
	        BigDecimal total = price.multiply(new BigDecimal(quantity));
	        allprice.setText(total.toString());
	    } catch (NumberFormatException e) {
	        allprice.setText("");
	    }
		
	}
	//查看订单
	private JPanel createOrdersPanel() {
		JPanel panel = new JPanel(new BorderLayout(20, 20));
	    JLabel titleLabel = new JLabel("订单管理", JLabel.LEFT);
	    titleLabel.setFont(Title);
	    panel.add(titleLabel, BorderLayout.NORTH);
	    
	    // 订单表格面板
	    JPanel tablePanel = new JPanel(new BorderLayout());
	    String[] columnNames = {"订单号", "客户姓名", "鲜花名称", "数量", "单价", "总价", "状态", "下单时间"};
	    orderTableModel = new DefaultTableModel(columnNames, 0) {
	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return false;
	        }
	    };
	    JTable orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(40); // 设置行高为40像素
	    JScrollPane scrollPane = new JScrollPane(orderTable);
	    tablePanel.add(scrollPane, BorderLayout.CENTER);
	    
	    // 按钮面板
	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	    JButton refreshButton = new JButton("刷新订单");
	    refreshButton.addActionListener(e -> refreshOrdersTable());
	    buttonPanel.add(refreshButton);
	    tablePanel.add(buttonPanel, BorderLayout.SOUTH);
	    
	    panel.add(tablePanel, BorderLayout.CENTER);
	    // 初始化订单数据
	    refreshOrdersTable();
	    
	    return panel;
	}
	//初始化订单数据
	private void refreshOrdersTable() {
	    orderTableModel.setRowCount(0); // 清空表格
	    List<Order> orders = storeService.getOrdersbySname(storename);
	        
	        for (Order order : orders) {
	            // 获取鲜花名称（假设需要从flowerDAO获取）
	            String flowerName = "未知";
	            try {
	                Flower flower = customerservice.getFlowerByID(order.getFlowerId());
	                if (flower != null) {
	                    flowerName = flower.getName();
	                }
	            } catch (Exception e) {
	                // 忽略异常，使用默认值
	            }
	            
	            Object[] rowData = {
	                order.getOrderNumber(),
	                order.getUser(),
	                flowerName,
	                order.getNum(),
	                order.getUnitPrice(),
	                order.getTotalAmount(),
	                order.getStatus(),
	                formatDate(order.getCreatedAt())
	            };
	            orderTableModel.addRow(rowData);
	        }
	}

	// 日期格式化工具方法
	private String formatDate(Date date) {
	    if (date == null) return "未知";
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return sdf.format(date);
	}
	//培育新品种
	private JPanel createNewFlowerPanel() {
		JPanel panel = new JPanel(new BorderLayout(20, 20));
		JLabel titleLabel = new JLabel("新品种管理", JLabel.LEFT);
		panel.add(titleLabel, BorderLayout.NORTH);
        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // 花名
        gbc.gridx = 0;gbc.gridy = 0;        
        JLabel nameLabel = new JLabel("花 名:");
        nameLabel.setFont(Label);
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;gbc.gridy = 0;       
        JTextField nameField = new JTextField();
        formPanel.add(nameField, gbc);
        // 类型
        gbc.gridx = 0;gbc.gridy = 1;        
        JLabel typeLabel = new JLabel("类 型:");
        typeLabel.setFont(Label);
        formPanel.add(typeLabel, gbc);
        gbc.gridx = 1;gbc.gridy = 1;       
        JTextField typeField = new JTextField();
        formPanel.add(typeField, gbc);
        // 进价
        gbc.gridx = 0;gbc.gridy = 2;        
        JLabel purchasePriceLabel = new JLabel("进 价:");
        purchasePriceLabel.setFont(Label);
        formPanel.add(purchasePriceLabel, gbc);       
        gbc.gridx = 1;gbc.gridy = 2;       
        JTextField purchasePriceField = new JTextField();
        formPanel.add(purchasePriceField, gbc);
        // 售价
        gbc.gridx = 0;gbc.gridy = 3;        
        JLabel salePriceLabel = new JLabel("售 价:");
        salePriceLabel.setFont(Label);
        formPanel.add(salePriceLabel, gbc);        
        gbc.gridx = 1;gbc.gridy = 3;        
        JTextField salePriceField = new JTextField();
        formPanel.add(salePriceField, gbc);
        // 库存
        gbc.gridx = 0;gbc.gridy = 4;        
        JLabel stockLabel = new JLabel("库 存:");
        stockLabel.setFont(Label);
        formPanel.add(stockLabel, gbc);        
        gbc.gridx = 1;gbc.gridy = 4;       
        JTextField stockField = new JTextField();
        formPanel.add(stockField, gbc);
        // 花语
        gbc.gridx = 0;gbc.gridy = 5;
        JLabel meaningLabel = new JLabel("花 语:");
        meaningLabel.setFont(Label);
        formPanel.add(meaningLabel, gbc);       
        gbc.gridx = 1;gbc.gridy = 5;        
        JTextField meaningField = new JTextField(20);
        formPanel.add(meaningField, gbc);
        // 按钮面板
        gbc.gridx = 0;gbc.gridy = 6;gbc.gridwidth = 2;        
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));       
        JButton addButton = new JButton("添加新品种");
        buttonPanel.add(addButton);       
        JButton resetButton = new JButton("重置");
        buttonPanel.add(resetButton);        
        formPanel.add(buttonPanel, gbc);        
        panel.add(formPanel, BorderLayout.NORTH);
     // 添加新品种按钮事件
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String type = typeField.getText().trim();
            String purchasePriceText = purchasePriceField.getText().trim();
            String salePriceText = salePriceField.getText().trim();
            String stockText = stockField.getText().trim();
            String meaning = meaningField.getText().trim();
            
            if (name.isEmpty() || type.isEmpty() || purchasePriceText.isEmpty() || 
                salePriceText.isEmpty() || stockText.isEmpty()||meaning.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请填写所有必填字段", 
                        "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            try {
                BigDecimal purchasePrice = new BigDecimal(purchasePriceText);
                BigDecimal salePrice = new BigDecimal(salePriceText);
                int stock = Integer.parseInt(stockText);
                
                // 创建新的Flower对象
                Flower newFlower = new Flower();
                newFlower.setName(name);
                newFlower.setType(type);
                newFlower.setPurchasePrice(purchasePrice);
                newFlower.setSalePrice(salePrice);
                newFlower.setStock(stock);
                newFlower.setMeaning(meaning);
                // ============ 新增获取storeId并设置的逻辑 ============
                FlowerStore store = storeService.getStoreByStoreName(storename);
                if (store != null) {
                    newFlower.setStoreId(store.getId()); // 设置花店ID
                } else {
                    JOptionPane.showMessageDialog(frame, "未找到对应的花店信息，请检查", 
                            "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // 调用业务方法添加新品种
                boolean success = storeService.addFlower(newFlower);
                
                if (success) {
                    JOptionPane.showMessageDialog(frame, "新品种添加成功!", 
                            "成功", JOptionPane.INFORMATION_MESSAGE);
                    
                    // 刷新库存表格
                    refreshStockTable();
                    
                    // 重置表单
                    nameField.setText("");
                    typeField.setText("");
                    purchasePriceField.setText("");
                    salePriceField.setText("");
                    stockField.setText("");
                    meaningField.setText("");
                    
                } else {
                    JOptionPane.showMessageDialog(frame, "该鲜花已存在，新品种添加失败，请重试", 
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "请输入有效的数字格式", 
                        "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "添加新品种失败: " + ex.getMessage(), 
                        "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        // 重置按钮事件
        resetButton.addActionListener(e -> {
            nameField.setText("");
            typeField.setText("");
            purchasePriceField.setText("");
            salePriceField.setText("");
            stockField.setText("");
            meaningField.setText("");
        });        
        return panel;
	}
	//创建销售报表
	private JPanel createSalesReportPanel() {
		JPanel panel = new JPanel(new BorderLayout(20, 20));
	    JLabel titleLabel = new JLabel("销售报表", JLabel.LEFT);
	    titleLabel.setFont(Title);
	    panel.add(titleLabel, BorderLayout.NORTH);
	    
	    // 表格面板
	    JPanel tablePanel = new JPanel(new BorderLayout());
	    String[] columnNames = {"鲜花ID", "鲜花名称", "销售数量", "总利润(元)"};
	    salesReportTableModel = new DefaultTableModel(columnNames, 0) {
	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return false;
	        }
	    	 @Override
	         public Class<?> getColumnClass(int columnIndex) {
	             switch (columnIndex) {
	                 case 0: // 鲜花ID
	                     return Integer.class;
	                 case 2: // 销售数量
	                     return Integer.class;
	                 case 3: // 总利润
	                     return Double.class;
	                 default: // 其他列（鲜花名称）
	                     return String.class;
	             }
	         }
	     };
	    
	    JTable reportTable = new JTable(salesReportTableModel);
	    reportTable.setRowHeight(40);
	    // 启用表格排序功能
	    reportTable.setAutoCreateRowSorter(true); 
	    // 创建并应用居左对齐的渲染器到所有列
	    DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
	    leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
	    
	    for (int i = 0; i < reportTable.getColumnCount(); i++) {
	        reportTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
	    }
	    
	 // 添加利润列的渲染器 - 保持两位小数显示
	    reportTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
	        private final DecimalFormat format = new DecimalFormat("#,##0.00");
	        
	        @Override
	        public Component getTableCellRendererComponent(
	                JTable table, Object value, boolean isSelected, 
	                boolean hasFocus, int row, int column) {
	            
	            // 先调用父类方法获取默认渲染组件
	            Component c = super.getTableCellRendererComponent(
	                    table, value, isSelected, hasFocus, row, column);
	            
	            // 格式化Double值
	            if (value instanceof Double) {
	                setText(format.format(value));
	            }
	            return c;
	        }
	    });
	    JScrollPane scrollPane = new JScrollPane(reportTable);
	    tablePanel.add(scrollPane, BorderLayout.CENTER);
	    
	    // 刷新按钮
	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	    JButton refreshButton = new JButton("刷新报表");
	    refreshButton.addActionListener(e -> refreshSalesReport());
	    buttonPanel.add(refreshButton);
	    tablePanel.add(buttonPanel, BorderLayout.SOUTH);
	    
	    panel.add(tablePanel, BorderLayout.CENTER);
	    
	    // 初始化报表数据
	    refreshSalesReport();
	    
	    return panel;	
	}
	// 刷新销售报表数据
	private void refreshSalesReport() {
	    salesReportTableModel.setRowCount(0); // 清空表格
	    
	    try {
	        // 获取销售数据
	        Map<Integer, int[]> salesData = storeService.getSalesSituation(storename);
	        
	        // 遍历销售数据并填充表格
	        for (Map.Entry<Integer, int[]> entry : salesData.entrySet()) {
	            int flowerId = entry.getKey();
	            int[] data = entry.getValue();
	            int quantity = data[0];
	            int profitInCents = data[1];
	            double profitInYuan = profitInCents / 100.0; // 转换为元
	            
	            // 获取鲜花名称
	            String flowerName = "未知";
	            try {
	                Flower flower = customerservice.getFlowerByID(flowerId);
	                if (flower != null) {
	                    flowerName = flower.getName();
	                }
	            } catch (Exception e) {
	                // 忽略异常，使用默认值
	            }
	            
	            Object[] rowData = {
	                flowerId,
	                flowerName,
	                quantity,
	                String.format("%.2f", profitInYuan) // 格式化利润为两位小数
	            };	            
	            salesReportTableModel.addRow(rowData);
	        }
	        
	        // 如果表格为空，显示提示信息
	        if (salesReportTableModel.getRowCount() == 0) {
	            JOptionPane.showMessageDialog(frame, "暂无销售数据", 
	                    "提示", JOptionPane.INFORMATION_MESSAGE);
	        }
	    } catch (IllegalArgumentException e) {
	        JOptionPane.showMessageDialog(frame, e.getMessage(), 
	                "错误", JOptionPane.ERROR_MESSAGE);
	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(frame, "获取销售报表失败: " + e.getMessage(), 
	                "错误", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	    }
	}
	// 库存操作
	private JPanel createInventoryPanel() {
	    JPanel panel = new JPanel(new BorderLayout(20, 20));
	    JLabel titleLabel = new JLabel("库存操作", JLabel.LEFT);
	    titleLabel.setFont(Title);
	    panel.add(titleLabel, BorderLayout.NORTH);
	    
	    // 表单面板
	    JPanel formPanel = new JPanel(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(20, 20, 20, 20);
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    
	    // 鲜花选择下拉框
	    gbc.gridx = 0; gbc.gridy = 0;
	    JLabel flowerLabel = new JLabel("选择鲜花:");
	    flowerLabel.setFont(Label);
	    formPanel.add(flowerLabel, gbc);
	    
	    gbc.gridx = 1; gbc.gridy = 0;
	    JComboBox<Flower> flowerComboBox = new JComboBox<>();
	    formPanel.add(flowerComboBox, gbc);
	    
	    // 数量输入框
	    gbc.gridx = 0; gbc.gridy = 1;
	    JLabel quantityLabel = new JLabel("数量:");
	    quantityLabel.setFont(Label);
	    formPanel.add(quantityLabel, gbc);
	    
	    gbc.gridx = 1; gbc.gridy = 1;
	    JTextField quantityField = new JTextField(20);
	    formPanel.add(quantityField, gbc);
	    
	    // 当前库存显示
	    gbc.gridx = 0; gbc.gridy = 2;
	    JLabel currentStockLabel = new JLabel("当前库存:");
	    currentStockLabel.setFont(Label);
	    formPanel.add(currentStockLabel, gbc);
	    
	    gbc.gridx = 1; gbc.gridy = 2;
	    JTextField currentStockField = new JTextField(20);
	    currentStockField.setEditable(false);
	    formPanel.add(currentStockField, gbc);
	    
	    // 操作类型选择
	    gbc.gridx = 0; gbc.gridy = 3;
	    JLabel operationLabel = new JLabel("操作类型:");
	    operationLabel.setFont(Label);
	    formPanel.add(operationLabel, gbc);
	    
	    gbc.gridx = 1; gbc.gridy = 3;
	    JComboBox<String> operationComboBox = new JComboBox<>(new String[]{"入库", "出库"});
	    formPanel.add(operationComboBox, gbc);
	    
	    // 按钮面板
	    gbc.gridx = 0; gbc.gridy = 4;
	    gbc.gridwidth = 2;
	    gbc.anchor = GridBagConstraints.CENTER;
	    
	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
	    
	    JButton executeButton = new JButton("执行操作");
	    buttonPanel.add(executeButton);
	    
	    JButton resetButton = new JButton("重置");
	    buttonPanel.add(resetButton);
	    
	    formPanel.add(buttonPanel, gbc);
	    
	    panel.add(formPanel, BorderLayout.NORTH);
	    
	    // 初始化鲜花下拉框
	    try {
	        List<Flower> flowers = storeService.getFStockByFSname(storename);
	        for (Flower flower : flowers) {
	            flowerComboBox.addItem(flower);
	        }
	        
	        // 设置自定义渲染器，只显示花名
	        flowerComboBox.setRenderer(new DefaultListCellRenderer() {
	            @Override
	            public Component getListCellRendererComponent(JList<?> list, Object value, 
	                                                         int index, boolean isSelected, 
	                                                         boolean cellHasFocus) {
	                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	                if (value instanceof Flower) {
	                    setText(((Flower) value).getName());
	                }
	                return this;
	            }
	        });
	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(frame, "获取鲜花列表失败: " + e.getMessage(), 
	                "错误", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	    }
	    
	    // 监听鲜花选择事件，更新当前库存
	    flowerComboBox.addActionListener(e -> {
	        Flower selectedFlower = (Flower) flowerComboBox.getSelectedItem();
	        if (selectedFlower != null) {
	            currentStockField.setText(String.valueOf(selectedFlower.getStock()));
	        }
	    });
	    
	    // 重置按钮事件
	    resetButton.addActionListener(e -> {
	        flowerComboBox.setSelectedIndex(0);
	        quantityField.setText("");
	        currentStockField.setText("");
	    });
	    
	    // 执行操作按钮事件
	    executeButton.addActionListener(e -> {
	        Flower selectedFlower = (Flower) flowerComboBox.getSelectedItem();
	        if (selectedFlower == null) {
	            JOptionPane.showMessageDialog(frame, "请选择鲜花", 
	                    "提示", JOptionPane.INFORMATION_MESSAGE);
	            return;
	        }
	        
	        String quantityText = quantityField.getText().trim();
	        if (quantityText.isEmpty()) {
	            JOptionPane.showMessageDialog(frame, "请输入数量", 
	                    "提示", JOptionPane.INFORMATION_MESSAGE);
	            return;
	        }
	        
	        try {
	            int quantity = Integer.parseInt(quantityText);
	            if (quantity <= 0) {
	                JOptionPane.showMessageDialog(frame, "数量必须大于0", 
	                        "错误", JOptionPane.ERROR_MESSAGE);
	                return;
	            }
	            
	            String operation = (String) operationComboBox.getSelectedItem(); // 定义operation
	            boolean success = false;
	            
	            if ("入库".equals(operation)) {
	                // 执行入库操作
	                success = storeService.addStock(selectedFlower.getId(), quantity);
	            } else {
	                // 执行出库操作
	                if (quantity > selectedFlower.getStock()) {
	                    JOptionPane.showMessageDialog(frame, "库存不足，当前库存: " + selectedFlower.getStock(), 
	                            "错误", JOptionPane.ERROR_MESSAGE);
	                    return;
	                }
	                success = storeService.outStock(selectedFlower.getId(), quantity);
	            }
	            
	            if (success) {
	                JOptionPane.showMessageDialog(frame, operation + "成功!", 
	                        "成功", JOptionPane.INFORMATION_MESSAGE);
	                
	                // 刷新库存表格
	                refreshStockTable();
	                
	                // 刷新当前面板的鲜花下拉框
	                flowerComboBox.removeAllItems();
	                List<Flower> flowers = storeService.getFStockByFSname(storename);
	                for (Flower flower : flowers) {
	                    flowerComboBox.addItem(flower);
	                }
	                
	                // 重置表单
	                quantityField.setText("");
	                currentStockField.setText("");
	            } else {
	                JOptionPane.showMessageDialog(frame, operation + "失败，请重试", 
	                        "错误", JOptionPane.ERROR_MESSAGE);
	            }
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(frame, "请输入有效的数量", 
	                    "错误", JOptionPane.ERROR_MESSAGE);
	        }
	    });
	    
	    return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {	}		   
}