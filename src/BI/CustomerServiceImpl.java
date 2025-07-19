package BI;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DAO.*;
import Entity.Customer;
import Entity.Flower;
import Entity.FlowerStore;
import Entity.Order;

public class CustomerServiceImpl implements CustomerService{
    private final CustomerDAO customerDAO = DAOFactory.getCustomerDAO();
    private final FlowerDAO flowerDAO = DAOFactory.getFlowerDAO();
    private final OrderDAO orderDAO = DAOFactory.getOrderDAO();
    private final FlowerStoreDAO flowerStoreDAO = DAOFactory.getFlowerStoreDAO();
    //根据店ID和鲜花名得到鲜花
    @Override
    public List<Flower> searchFlowersByStoreAndName(int storeId, String name) {
        List<Flower> result = new ArrayList<>();             
        // 按名称精确查询（根据现有DAO方法）
        Flower flower = flowerDAO.getFlowersbyName(name);
        if (flower != null && flower.getStoreId() == storeId) {
            result.add(flower);
        }        
        return result;
    }
    //根据店ID和鲜花类型得到鲜花
    @Override
    public List<Flower> searchFlowersByStoreAndType(int storeId, String type) {
        // 按类型查询并筛选花店
        List<Flower> flowers = flowerDAO.getFlowersbytype(type);
        List<Flower> result = new ArrayList<>();
        
        if (flowers != null) {
            for (Flower flower : flowers) {
                if (flower.getStoreId() == storeId) {
                    result.add(flower);
                }
            }
        }        
        return result;
    }
	//购买鲜花
	@Override
	public Order BuyFlower(String user,int flowerid,int num,boolean ispaid) {
		//参数校验
		if(flowerid<0||num<0)
			throw new IllegalArgumentException("鲜花ID和购买数量必须为正数");
		//查询鲜花是否存在
		Flower flower = flowerDAO.getFlowersbyID(flowerid);
	    if (flower==null) {
	        throw new IllegalArgumentException("鲜花不存在，ID: " + flowerid);
	    }
	    // 检查库存
	    if (flower.getStock() < num) {
	        throw new IllegalStateException("库存不足，剩余: " + flower.getStock());
	    }
	    // 计算订单金额
	    BigDecimal unitPrice = flower.getSalePrice();
	    BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(num));
	    // 创建订单
	    Order order = new Order(
	        0,                          // ID由数据库自增
	        generateOrderNumber(),      // 生成唯一订单号
	        user,						// 顾客用户名
	        flower.getStoreId(),        // 使用鲜花的店铺ID
	        flowerid,					// 鲜花ID
	        num,						// 购买数量
	        unitPrice,					// 单价
	        totalAmount,				// 总价
            Order.STATUS_COMPLETED ,    // 初始状态为已支付
	        new Date()                  // 创建时间
	    );
	    
	    try {
	    	//减少库存
	    	flowerDAO.outFlower(flowerid,num);
	    	//创建订单
	    	boolean success=orderDAO.addOrder(order);
	        if (!success) {
	            throw new RuntimeException("订单创建失败");
	        }
	        return order;
	    }catch (Exception e) {
	        throw new RuntimeException("购买失败: " + e.getMessage(), e);
	    	}	
		}
		// 生成唯一订单号
	    private String generateOrderNumber() {
	        return "ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
	    }
	    //根据用户名查询该用户所有订单
		@Override
		public List<Order> getOrdersByUser(String user) {
			List<Order> orders=orderDAO.getOrdersByUser(user);
			return orders;
		}
		//登录
		@Override
		public boolean Login(String username, String password) {
	        if (username == null || password == null) {
	            return false;
	        }
	        Customer customer=customerDAO.getCustomerbyun(username);
	        if (customer==null) {return false;}
	        return customer.getPassword().equals(password);
			
		}
		//注册
		@Override
		public boolean Register(String username, String password, String name, 
                String phone, String address) {
			// 参数校验
	        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
	            throw new IllegalArgumentException("用户名和密码不能为空");
	        }
	        // 检查用户名是否已存在
	        Customer existingCustomer = customerDAO.getCustomerbyun(username);
	        if (existingCustomer != null) {
	            throw new IllegalStateException("用户名已存在: " + username);
	        }
	        // 创建新用户
	        Customer newCustomer = new Customer();
	        newCustomer.setUsername(username);
	        newCustomer.setPassword(password);  // 注意：实际应先加密再存储
	        newCustomer.setName(name);
	        newCustomer.setPhone(phone);
	        newCustomer.setAddress(address);
	        newCustomer.setCreatedAt(new Date()); 
	        // 注册用户
	        return customerDAO.addCustomer(newCustomer);
		}
		@Override
		public List<FlowerStore> getAllFlowerStores() {
			List<FlowerStore> stores=flowerStoreDAO.getAllStores();
			return stores;
		}
		@Override
		public Flower getFlowerByID(int id) {
			Flower flower=flowerDAO.getFlowersbyID(id);
			return flower;
		}
		//得到用户某一年的全部订单
	    @Override
	    public List<Order> getOrdersByUserAndYear(String username, int year) {
	        List<Order> annualOrders= orderDAO.getOrdersByUserAndYear(username,year); 
	        return annualOrders;
	    }
	    //得到年度订单金额
	    @Override
	    public BigDecimal getAnnualTotalAmount(String username, int year) {
	        BigDecimal total = orderDAO.getAnnualTotalAmount(username,year);        	        
	        return total;
	    }
}
