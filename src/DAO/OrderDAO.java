package DAO;

import java.math.BigDecimal;
import java.util.List;
import Entity.Order;

public interface OrderDAO {
	List<Order> getAllOrdersByStoreId(int storeid);//根据花店ID得到该花店下所有订单
	List<Order> getOrdersByUser(String user);//根据用户名得到该用户的所有订单信息
	boolean addOrder(Order order);//添加订单
	boolean updateOrderStatus(int orderId, String statusCancelled);//修改订单状态
	List<Order> getOrdersByUserAndYear(String username, int year);//根据用户名和年份查询订单	
    BigDecimal getAnnualTotalAmount(String username, int year);//获取指定用户本年度订单总金额
}
