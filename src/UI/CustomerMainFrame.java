package UI;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import Entity.Flower;
import Entity.FlowerStore;
import BI.BIFactory;
import BI.CustomerService;
import BI.FlowerStoreService;
//类定义和成员变量
public class CustomerMainFrame {
	//用户相关数据
    private String username;                //当前登录用户名
    private FlowerStoreService storeService;//花店服务接口
    private CustomerService customerService;//顾客服务接口
    //UI组件
    private JFrame currentFrame;			//当前活动窗口
    private FlowerStore selectedStore;		//用户选择的花店
    private JTextField cartTotalField;		//显示购物车总金额的文本框
    private DefaultTableModel cartTableModel;//购物车表格数据模型
    //字体
    Font title = new Font("Microsoft YaHei", Font.BOLD, 24);
    Font label = new Font("Microsoft YaHei", Font.BOLD, 14);
    Font label2 = new Font("Microsoft YaHei", Font.BOLD, 12);
    //构造函数
    public CustomerMainFrame(String username) {
        this.username = username;								//保存用户名
        //根据工厂获取服务实例
        this.storeService = BIFactory.getFlowerStoreService();
        this.customerService = BIFactory.getCustomerService();
        showStoreSelectionFrame();								//显示花店选择界面
    }

    // 花店选择界面
    private void showStoreSelectionFrame() {
        JFrame storeSelectionFrame = new JFrame("鲜花选购 - " + username);
        storeSelectionFrame.setSize(800, 600);
        storeSelectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        storeSelectionFrame.setLocationRelativeTo(null);//窗口居中显示					
        storeSelectionFrame.setLayout(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("请选择花店", JLabel.CENTER);
        titleLabel.setFont(title);
        storeSelectionFrame.add(titleLabel, BorderLayout.NORTH);

        // 花店列表面板
        JPanel storeListPanel = new JPanel();
        storeListPanel.setLayout(new GridLayout(2, 1, 20, 20));//两行一列布局
        try {
            // 获取所有花店
            List<FlowerStore> stores = customerService.getAllFlowerStores();
            // 遍历花店列表，为每个花店创建卡片
            for (FlowerStore store : stores) {
                JPanel storeCard = createStoreCard(store, storeSelectionFrame);
                storeListPanel.add(storeCard);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(storeSelectionFrame, "获取花店列表失败: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // 添加滚动条
        JScrollPane scrollPane = new JScrollPane(storeListPanel);
        storeSelectionFrame.add(scrollPane, BorderLayout.CENTER);
        storeSelectionFrame.setVisible(true);//显示窗口
        currentFrame = storeSelectionFrame;	//保存当前窗口引用
    }

    // 创建花店卡片
    private JPanel createStoreCard(FlowerStore store, JFrame parentFrame) {
    	//创建卡片容器
        JPanel card = new JPanel(new BorderLayout(0, 5));
        //设置卡片边框样式
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        card.setBackground(Color.WHITE);

        // 花店名称
        JLabel nameLabel = new JLabel(store.getStoreName());
        nameLabel.setFont(label);
        card.add(nameLabel, BorderLayout.NORTH);

        // 地址和店主信息面板
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // 花店地址
        JLabel addressLabel = new JLabel("地址: " + store.getAddress());
        addressLabel.setFont(label2);
        infoPanel.add(addressLabel);
        // 店主
        JLabel storeOwnerLabel = new JLabel("店主: " + store.getOwner());
        storeOwnerLabel.setFont(label2);
        infoPanel.add(storeOwnerLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // 点击事件
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedStore = store;//保存当前选择的花店
                parentFrame.dispose();//关闭当前窗口
                showFlowerStoreMainFrame();//显示花店主界面
            }
            //鼠标悬停效果
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(240, 240, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    // 花店主界面
    private void showFlowerStoreMainFrame() {
        JFrame mainFrame = new JFrame("鲜花选购 - " + selectedStore.getStoreName() + " - " + username);
        mainFrame.setSize(1000, 700);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);

        // 创建顶部导航栏
        mainFrame.add(createNavbar(mainFrame), BorderLayout.NORTH);

        // 创建选项卡式界面
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("浏览鲜花", createBrowsePanel());
        tabbedPane.addTab("查询鲜花", createSearchPanel());
        tabbedPane.addTab("购物车", createCartPanel());
        tabbedPane.addTab("历史订单", createHistoryPanel());
        tabbedPane.addTab("年度统计",createAnnualStatisticsPanel());

        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        mainFrame.setVisible(true);
        currentFrame = mainFrame;//更新当前窗口引用
    }

    // 创建顶部导航栏
    private JPanel createNavbar(JFrame frame) {
        JPanel navbar = new JPanel(new BorderLayout());

        // 左侧花店名称
        JLabel storeNameLabel = new JLabel(selectedStore.getStoreName(), JLabel.LEFT);
        storeNameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        navbar.add(storeNameLabel, BorderLayout.WEST);

        // 右侧退出按钮
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("退出登录");
        logoutButton.addActionListener(e -> {
            showStoreSelectionFrame();
            frame.dispose();
        });
        rightPanel.add(logoutButton);
        navbar.add(rightPanel, BorderLayout.EAST);
        return navbar;
    }

    // 浏览鲜花面板
    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "花名", "类型", "价格", "库存", "花语", "操作"};
        //创建表格模型(第六列可编辑)
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;//仅操作列可编辑
            }
        };
        
        JTable flowerTable = new JTable(tableModel);//创建表格
        flowerTable.setRowHeight(35);//行高为35

        // 添加操作列的渲染器和编辑器
        flowerTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        flowerTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), tableModel, this));
        //添加到面板中心
        panel.add(new JScrollPane(flowerTable), BorderLayout.CENTER);

        try {
        	//获取该花店下鲜花列表
            List<Flower> flowers = storeService.getFStockByFSname(selectedStore.getStoreName());
            int flowerid=1;
            //遍历鲜花列表，添加到表格
            for (Flower flower : flowers) {
                tableModel.addRow(new Object[]{
                        flowerid++,
                        flower.getName(),
                        flower.getType(),
                        flower.getSalePrice(),
                        flower.getStock(),
                        flower.getMeaning(),
                        "加入购物车"
                });
            }
        } catch (Exception e) {
            showMessageSafe("获取鲜花列表失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return panel; //返回面板
    }

    // 查询鲜花面板
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 搜索条件面板
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField nameField = new JTextField(10);//花名输入框
        JTextField typeField = new JTextField(10);//类型输入框
        JButton searchButton = new JButton("搜索");

        searchPanel.add(new JLabel("花名:"));
        searchPanel.add(nameField);
        searchPanel.add(new JLabel("类型:"));
        searchPanel.add(typeField);
        searchPanel.add(searchButton);

        panel.add(searchPanel, BorderLayout.NORTH);//添加到面板顶部

        // 结果表格
        String[] columnNames = {"ID", "花名", "类型", "价格", "库存", "花语", "操作"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        
        JTable resultTable = new JTable(tableModel);//创建表格
        resultTable.setRowHeight(35);//设置行高
        resultTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        resultTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), tableModel, this));

        panel.add(new JScrollPane(resultTable), BorderLayout.CENTER);//添加表格到面板中心

        // 搜索按钮事件
        searchButton.addActionListener(e -> {
            String name = nameField.getText().trim();//获取花名
            String type = typeField.getText().trim();//获取类型

            tableModel.setRowCount(0);//清空表格

            try {
                List<Flower> flowers = new ArrayList<>();
                int storeId = selectedStore.getId();              
                if (!name.isEmpty() && !type.isEmpty()) {
                    // 组合搜索：先按名称查，再按类型过滤
                    List<Flower> byName = customerService.searchFlowersByStoreAndName(storeId, name);
                    for (Flower flower : byName) {
                        if (flower.getType().equals(type)) {
                            flowers.add(flower);
                        }
                    }
                } else if (!name.isEmpty()) {
                    // 仅按花名搜索
                    flowers = customerService.searchFlowersByStoreAndName(storeId, name);
                } else if (!type.isEmpty()) {
                    // 仅按类型搜索
                    flowers = customerService.searchFlowersByStoreAndType(storeId, type);
                } else {
                    // 无条件，显示所有鲜花
                    flowers = customerService.searchFlowersByStoreAndName(storeId, "");
                }
                
                // 显示搜索结果
                for (Flower flower : flowers) {
                    tableModel.addRow(new Object[]{
                        flower.getId(),
                        flower.getName(),
                        flower.getType(),
                        flower.getSalePrice(),
                        flower.getStock(),
                        flower.getMeaning(),
                        "加入购物车"
                    });
                }

                if (tableModel.getRowCount() == 0) {
                    showMessageSafe("未找到匹配的鲜花", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                showMessageSafe("搜索失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        return panel;
    }

    // 购物车面板
    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "花名", "价格", "数量", "小计", "操作"};
        cartTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 5;
            }
        };
        JTable cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(35);
        cartTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        cartTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), cartTableModel, this));

        // 添加数量编辑监听器，更新总计
        cartTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) {
                updateCartTotals();//更新购物车总计
            }
        });

        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // 结算面板
        JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("总计: ");
        cartTotalField = new JTextField(10);
        cartTotalField.setEditable(false);//不可编辑
        cartTotalField.setName("totalField");//设置名称
        JButton checkoutButton = new JButton("结算");

        checkoutPanel.add(totalLabel);
        checkoutPanel.add(cartTotalField);
        checkoutPanel.add(checkoutButton);
        panel.add(checkoutPanel, BorderLayout.SOUTH);

        // 初始化购物车数据
        updateCartTable();

        // 结算按钮事件
        checkoutButton.addActionListener(e -> {
            if (cartTableModel.getRowCount() == 0) {
                showMessageSafe("购物车为空", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                boolean success = performCheckout();//执行结算
                if (success) {
                    showMessageSafe("结算成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
                    updateCartTable();//刷新购物车
                } else {
                    showMessageSafe("结算失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                showMessageSafe("结算失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        return panel;
    }

    // 执行结算
    private boolean performCheckout() {
        Map<Integer, Integer> cartItems = getCurrentUserCart(); // 获取当前用户购物车

        if (cartItems.isEmpty()) {
            showMessageSafe("购物车为空", "提示", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        // 先检查所有商品库存
        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            int flowerId = entry.getKey();
            int quantity = entry.getValue();
            Flower flower = customerService.getFlowerByID(flowerId);

            if (flower == null) {
                showMessageSafe("鲜花ID: " + flowerId + " 不存在", "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (quantity > flower.getStock()) {
                showMessageSafe("鲜花[" + flower.getName() + "]库存不足，需要: " + quantity + 
                              "，当前库存: " + flower.getStock(), "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // 结算
        try {
            for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
                int flowerId = entry.getKey();
                int quantity = entry.getValue();
                customerService.BuyFlower(username, flowerId, quantity,true);
            }

            clearCurrentUserCart(); // 清空当前用户购物车
            return true;
        } catch (Exception e) {
            showMessageSafe("结算失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

 // 历史订单面板
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"订单号", "花名", "数量", "单价", "总价", "状态", "下单时间"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable orderTable = new JTable(tableModel);
        orderTable.setRowHeight(35);

        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // 添加刷新按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("刷新订单");
        refreshButton.addActionListener(e -> refreshOrderHistory(tableModel));
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 初始化订单数据
        loadOrderHistory(tableModel);

        return panel;
    }

    // 加载订单历史数据
    private void loadOrderHistory(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // 清空表格

        try {
            List<Entity.Order> orders = customerService.getOrdersByUser(username);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Entity.Order order : orders) {
                // 获取鲜花名称
                String flowerName = "未知";
                Flower flower = customerService.getFlowerByID(order.getFlowerId());
                if (flower != null) {
                    flowerName = flower.getName();
                }

                tableModel.addRow(new Object[]{
                        order.getOrderNumber(),
                        flowerName,
                        order.getNum(),
                        order.getUnitPrice(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        sdf.format(order.getCreatedAt())
                });
            }

            if (tableModel.getRowCount() == 0) {
                showMessageSafe("暂无历史订单", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            showMessageSafe("获取历史订单失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // 刷新订单历史
    private void refreshOrderHistory(DefaultTableModel tableModel) {
        showMessageSafe("正在刷新订单...", "提示", JOptionPane.INFORMATION_MESSAGE);
        loadOrderHistory(tableModel);
    }


    // 安全显示消息框的方法
    private void showMessageSafe(String message, String title, int messageType) {
        Window activeWindow = null;
        
        // 尝试找到当前激活的窗口
        for (Window window : Window.getWindows()) {
            if (window.isActive()) {
                activeWindow = window;
                break;
            }
        }
        
        // 如果找到激活窗口，使用它作为父组件
        if (activeWindow != null) {
            JOptionPane.showMessageDialog(activeWindow, message, title, messageType);
        } 
        // 否则使用默认方式（无父组件）
        else {
            JOptionPane.showMessageDialog(null, message, title, messageType);
        }
    }

    // 更新购物车表格数据
    private void updateCartTable() {
        if (cartTableModel == null) return;
        
        cartTableModel.setRowCount(0);//清空表格
        Map<Integer, Integer> cartItems = getCurrentUserCart(); //获取购物车数据
        BigDecimal total = BigDecimal.ZERO;//初始化总计

        try {
            for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
                int flowerId = entry.getKey();
                int quantity = entry.getValue();
                Flower flower = customerService.getFlowerByID(flowerId);

                if (flower != null) {
                	//计算小计
                    BigDecimal subtotal = flower.getSalePrice().multiply(new BigDecimal(quantity));
                    total = total.add(subtotal);//累加总计
                    //添加到购物车表格
                    cartTableModel.addRow(new Object[]{
                            flowerId,
                            flower.getName(),
                            flower.getSalePrice(),
                            quantity,
                            subtotal,
                            "删除"
                    });
                }
            }

            if (cartTotalField != null) {
                cartTotalField.setText(String.format("¥%.2f", total));//显示总计
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 更新购物车总计
    private void updateCartTotals() {
        if (cartTableModel == null || cartTotalField == null) return;
        
        BigDecimal total = BigDecimal.ZERO;
        Map<Integer, Integer> cart = getCurrentUserCart(); //获取购物车
        
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            int flowerId = (Integer) cartTableModel.getValueAt(i, 0);
            int quantity;
            try {
                quantity = Integer.parseInt(cartTableModel.getValueAt(i, 3).toString());
            } catch (NumberFormatException e) {
                // 数量格式错误时恢复为原数量
                quantity = cart.getOrDefault(flowerId, 0);
                cartTableModel.setValueAt(quantity, i, 3);
            }
            
            // 更新购物车中的数量
            cart.put(flowerId, quantity);
            
            // 更新小计
            BigDecimal price = (BigDecimal) cartTableModel.getValueAt(i, 2);
            BigDecimal subtotal = price.multiply(new BigDecimal(quantity));
            cartTableModel.setValueAt(subtotal, i, 4);
            
            total = total.add(subtotal);
        }
        
        cartTotalField.setText(String.format("¥%.2f", total));
    }

    // 添加到购物车
    public void addToCart(int flowerId, int quantity) {
        Map<Integer, Integer> cart = getCurrentUserCart();
        cart.put(flowerId, cart.getOrDefault(flowerId, 0) + quantity);
        updateCartTable();
    }

    // 从购物车移除
    public void removeFromCart(int flowerId) {
        Map<Integer, Integer> cart = getCurrentUserCart();
        cart.remove(flowerId);
        updateCartTable();
    }

    // 获取当前用户的购物车
    private Map<Integer, Integer> getCurrentUserCart() {
        return CartManager.getInstance().getCart(username);
    }

    // 清空当前用户的购物车
    private void clearCurrentUserCart() {
        CartManager.getInstance().clearCart(username);
    }

    // 表格按钮渲染器
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);//设置按钮透明
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());//设置按钮文本
            return this;//返回按钮组件
        }
    }

    // 表格按钮编辑器
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private DefaultTableModel tableModel;
        private CustomerMainFrame mainFrame;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox, DefaultTableModel tableModel, CustomerMainFrame mainFrame) {
            super(checkBox);
            this.tableModel = tableModel;
            this.mainFrame = mainFrame;
            button = new JButton();
            button.setOpaque(true);//按钮设置为透明
            button.addActionListener(e -> fireEditingStopped());//点击按钮时结束编辑
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
            
            if (currentRow >= 0 && currentRow < tableModel.getRowCount()) {
                if ("加入购物车".equals(label)) {
                    int flowerId = (int) tableModel.getValueAt(currentRow, 0);
                    try {
                        // 检查库存
                        Flower flower = customerService.getFlowerByID(flowerId);
                        if (flower == null) {
                            showMessageSafe("鲜花不存在", "错误", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        // 弹出数量输入对话框
                        int quantity = showQuantityDialog(flower);
                        if (quantity <= 0) {
                            showMessageSafe("请输入有效的购买数量", "提示", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        
                        // 检查库存
                        if (quantity > flower.getStock()) {
                            showMessageSafe("库存不足，当前库存: " + flower.getStock(), 
                                          "错误", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        mainFrame.addToCart(flowerId, quantity);
                        showMessageSafe("已添加到购物车", "成功", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        showMessageSafe("添加失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else if ("删除".equals(label)) {
                    int flowerId = (int) tableModel.getValueAt(currentRow, 0);
                    mainFrame.removeFromCart(flowerId);
                }
            }
        }
    
        private int showQuantityDialog(Flower flower) {
            while (true) {
                String input = (String) JOptionPane.showInputDialog(
                    mainFrame.currentFrame,
                    "请输入购买数量 (库存: " + flower.getStock() + "):",
                    "设置购买数量",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "1"
                );
                
                // 用户点击取消
                if (input == null) return 0;
                
                try {
                    int quantity = Integer.parseInt(input.trim());
                    if (quantity <= 0) {
                        showMessageSafe("购买数量必须大于0", "提示", JOptionPane.WARNING_MESSAGE);
                        continue; // 重新循环
                    }
                    return quantity;
                } catch (NumberFormatException e) {
                    showMessageSafe("请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
                    // 继续循环，重新显示对话框
                }
            }
        }
    }


    // 购物车管理器（单例模式）
    static class CartManager {
        private static CartManager instance;
        private Map<String, Map<Integer, Integer>> userCarts;

        private CartManager() {
            userCarts = new HashMap<>();
        }

        public static CartManager getInstance() {
            if (instance == null) {
                synchronized (CartManager.class) {
                    if (instance == null) {
                        instance = new CartManager();
                    }
                }
            }
            return instance;
        }

        public Map<Integer, Integer> getCart(String username) {
            return userCarts.computeIfAbsent(username, k -> new HashMap<>());
        }

        public void clearCart(String username) {
            userCarts.remove(username);
        }
    }
    // 创建年度统计面板
    private JPanel createAnnualStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 顶部控制区 - 选择年份
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel yearLabel = new JLabel("选择年份:");
        JComboBox<Integer> yearComboBox = new JComboBox<>();
        
        // 填充最近10年的年份
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 10; i--) {
            yearComboBox.addItem(i);
        }
        yearComboBox.setSelectedItem(currentYear);//将其加入到下拉框里面
        
        JButton queryButton = new JButton("查询");
        controlPanel.add(yearLabel);
        controlPanel.add(yearComboBox);
        controlPanel.add(queryButton);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // 统计信息展示区
        JPanel statsPanel = new JPanel(new BorderLayout(10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 年度订单表格
        String[] columnNames = {"订单号", "花名", "数量", "单价", "总价", "下单时间"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable orderTable = new JTable(tableModel);
        orderTable.setRowHeight(30);
        
        statsPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        
        // 统计信息面板
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("年度总金额: ");
        JTextField totalField = new JTextField(15);
        totalField.setEditable(false);
        totalField.setFont(label);
        
        summaryPanel.add(totalLabel);
        summaryPanel.add(totalField);
        statsPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // 加载当前年度数据
        loadAnnualOrders(tableModel, totalField, currentYear);
        
        // 查询按钮事件
        queryButton.addActionListener(e -> {
            int selectedYear = (Integer) yearComboBox.getSelectedItem();
            loadAnnualOrders(tableModel, totalField, selectedYear);
        });
        
        return panel;
    }
    
    // 加载指定年份的订单数据
    private void loadAnnualOrders(DefaultTableModel tableModel, JTextField totalField, int year) {
        tableModel.setRowCount(0); // 清空表格
        try {
            List<Entity.Order> orders = customerService.getOrdersByUserAndYear(username, year);
            BigDecimal annualTotal = customerService.getAnnualTotalAmount(username, year);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            for (Entity.Order order : orders) {
                // 获取鲜花名称
                String flowerName = "未知";
                Flower flower = customerService.getFlowerByID(order.getFlowerId());
                if (flower != null) {
                    flowerName = flower.getName();
                }
                
                tableModel.addRow(new Object[]{
                        order.getOrderNumber(),
                        flowerName,
                        order.getNum(),
                        order.getUnitPrice(),
                        order.getTotalAmount(),
                        sdf.format(order.getCreatedAt())
                });
            }            
            if (tableModel.getRowCount() == 0) {
            	totalField.setText(null);//年度总金额设置为空
                showMessageSafe("该年份暂无订单记录", "提示", JOptionPane.INFORMATION_MESSAGE);
                
            } else {
                totalField.setText(String.format("¥%.2f", annualTotal));
            }
        } catch (Exception e) {
            showMessageSafe("获取年度订单失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
