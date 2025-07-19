package BI;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import DAO.DAOFactory;
import DAO.FlowerDAO;
import DAO.FlowerStoreDAO;
import DAO.OrderDAO;
import Entity.Flower;
import Entity.FlowerStore;
import Entity.Order;

public class FlowerStoreServiceImpl implements FlowerStoreService{
    private final FlowerStoreDAO flowerStoreDAO = DAOFactory.getFlowerStoreDAO();
    private final FlowerDAO flowerDAO = DAOFactory.getFlowerDAO();
    private final OrderDAO orderDAO = DAOFactory.getOrderDAO();
    //登录
	@Override
	public boolean ALogin(String storename, String password) {
		if(storename==null ||password ==null)
			return false;
		FlowerStore fs=flowerStoreDAO.getStoreByName(storename);
		if(fs==null) return false;
		 return ((FlowerStore) fs).getPwd().equals(password);
	}
	//注册
	@Override
	public boolean ARegister(String storename,String pwd,String owner,String phone,String address) {
		// 参数校验
        if (storename == null || pwd == null || storename.trim().isEmpty() || pwd.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }
        FlowerStore fs=flowerStoreDAO.getStoreByName(storename);
        if (fs != null) {
            throw new IllegalStateException("花店名已存在: " + storename);
        }
        FlowerStore newfs=new FlowerStore();
        newfs.setStoreName(storename); 
        newfs.setPwd(pwd);
        newfs.setOwner(owner);
        newfs.setPhone(phone);
        newfs.setAddress(address);
        newfs.setCreatedAt(new Date()); 	
		return flowerStoreDAO.addFlowerStore(newfs);
	}
	
	//查看库存
	@Override
	public List<Flower> getFStockByFSname(String storename) {
		List<Flower> flowers=flowerStoreDAO.getFlowersByStorename(storename);
		return flowers;
	}
		
	//查看订单
	@Override
	public List<Order> getOrdersbySname(String storename) {
		//根据店名得到店ID，将店ID代入下表
		FlowerStore flowerstore=flowerStoreDAO.getStoreByName(storename);
		int id=flowerstore.getId();
		List<Order> orders=orderDAO.getAllOrdersByStoreId(id);
		return orders;
	}
	
	//培育新品种
	@Override
	public boolean addFlower(Flower flower) {
		//检查花是否存在
		Flower flowers=flowerDAO.getFlowersbyName(flower.getName());
		if(flowers!=null) {
			 System.out.println("花卉已存在，花名: " + flower.getName());
			 return false;
		}
		return flowerDAO.addNewFlower(flower);
	}
	//入库
	@Override
	public boolean addStock(int id,int num) {
		boolean result=flowerDAO.addFlower(id,num);
		return result;
	}
	//出库
	@Override
	public boolean outStock(int id, int num) {
		boolean result=flowerDAO.outFlower(id,num);
		return result;
	}
	//查看销售情况
	@Override
	public Map<Integer, int[]> getSalesSituation(String storename) {
	    // 1. 获取花店ID
	    FlowerStore store = flowerStoreDAO.getStoreByName(storename);
	    if (store == null) {
	        throw new IllegalArgumentException("花店不存在: " + storename);
	    }
	    int storeId = store.getId();
	    
	    // 2. 获取所有订单项（订单表中的每条记录代表一个订单项）
	    List<Order> orderItems = orderDAO.getAllOrdersByStoreId(storeId);
	    
	    // 3. 创建一个Map来存储销售数据
	    // Key: 鲜花ID
	    // Value: int数组 [销售数量, 利润] (利润以分为单位存储，避免浮点数问题)
	    Map<Integer, int[]> salesData = new HashMap<>();
	    
	    // 4. 遍历所有订单项
	    for (Order orderItem : orderItems) {
	        int flowerId = orderItem.getFlowerId();
	        int quantity = orderItem.getNum();
	        
	        // 获取鲜花成本价
	        Flower flower = flowerDAO.getFlowersbyID(flowerId);
	        if (flower == null) {
	            // 如果找不到鲜花信息，跳过此项
	            continue;
	        }
	        
	        BigDecimal purchasePrice = flower.getPurchasePrice();
	        BigDecimal unitPrice = orderItem.getUnitPrice();
	        
	        // 计算利润 (销售价 - 成本价) * 数量
	        BigDecimal profitPerItem = unitPrice.subtract(purchasePrice);
	        BigDecimal totalProfit = profitPerItem.multiply(new BigDecimal(quantity));
	        
	        // 转换为分存储，避免浮点数精度问题
	        int profitInCents = totalProfit.multiply(new BigDecimal(100)).intValue();
	        
	        // 更新销售数据
	        int[] existingData = salesData.get(flowerId);
	        if (existingData == null) {
	            salesData.put(flowerId, new int[]{quantity, profitInCents});
	        } else {
	            existingData[0] += quantity; // 增加销售数量
	            existingData[1] += profitInCents; // 增加利润
	        }
	    }
	    
	    return salesData;
	}
	//销售鲜花
	@Override
	public boolean sellFlower(String storename, int flowerId, int quantity, BigDecimal unitPrice,String customerName, String customerPhone) {
	    // 1. 参数校验
	    if (storename == null || storename.trim().isEmpty() || 
	            quantity <= 0 || unitPrice == null ||
	            customerName == null || customerName.trim().isEmpty() ||
	            customerPhone == null || customerPhone.trim().isEmpty()) {
	            throw new IllegalArgumentException("销售参数不能为空");
	        }
	    
	    // 2. 业务逻辑验证
	    FlowerStore store = flowerStoreDAO.getStoreByName(storename);
	    if (store == null) {
	        throw new IllegalStateException("花店不存在");
	    }
	    
	    Flower flower = flowerDAO.getFlowersbyID(flowerId);
	    if (flower == null) {
	        throw new IllegalStateException("鲜花不存在");
	    }
	    
	    if (flower.getStock() < quantity) {
	        throw new IllegalStateException("库存不足，当前库存: " + flower.getStock());
	    }
	    
	    // 3. 创建订单
	    Order order = new Order();
        order.setOrderNumber(generateOrderNumber()); // 生成唯一订单号
	    order.setFlowerId(flowerId);
	    order.setStoreId(store.getId());
	    order.setNum(quantity);
	    order.setUnitPrice(unitPrice);
        order.setTotalAmount(unitPrice.multiply(new BigDecimal(quantity)));
        order.setUser(customerName);   
        order.setStatus(Order.STATUS_COMPLETED); // 设置订单状态为已完成
	    order.setCreatedAt(new Date());
	    
	    // 4. 执行事务（订单添加 + 库存扣除）
	    try {
	        boolean orderAdded = orderDAO.addOrder(order);
	        boolean stockReduced = flowerDAO.outFlower(flowerId, quantity);
	        return orderAdded && stockReduced;
	    } catch (Exception e) {
	        // 实际项目中应添加事务回滚
	        e.printStackTrace();
	        return false;
	    }
	}

//生成唯一订单号（示例实现）
private String generateOrderNumber() {
 return "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + 
        "-" + new Random().nextInt(1000);
}
//根据花店名获取花店信息
@Override
public FlowerStore getStoreByStoreName(String name) {
	FlowerStore store=flowerStoreDAO.getStoreByName(name);
	return store;
}

}