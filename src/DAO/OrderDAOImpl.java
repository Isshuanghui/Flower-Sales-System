package DAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Entity.Order;
import Util.DBUtil;
public class OrderDAOImpl implements OrderDAO{
	//根据花店ID得到该花店下所有鲜花
	@Override
	public List<Order> getAllOrdersByStoreId(int storeid) {
		String sql="SELECT * FROM orders where store_id=?";
		return excutequery(sql,new Object[] {storeid});
	}
	//通用查询方法,params：SQL 参数数组，用于替换 ? 占位符。
	static List<Order> excutequery(String sql,Object[] params){
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>(); 
        try {
            conn = DBUtil.getConnection();                 // 获取数据库连接
            ps = conn.prepareStatement(sql);			   //预编译SQL语句
            // 设置参数,将参数按顺序设置到预编译语句中
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);//若 params = ["玫瑰"]，则 ps.setObject(1, "玫瑰") 替换第一个 ?。
                }
            }
            rs = ps.executeQuery();
			while(rs.next()) {
				Order order =new Order(
						rs.getInt("id"),
						rs.getString("order_number"),
						rs.getString("user"),
						rs.getInt("store_id"),
						rs.getInt("flower_id"),
						rs.getInt("num"),
						rs.getBigDecimal("unit_price"),
						rs.getBigDecimal("total_amount"),
						rs.getString("status"),
						new Date(rs.getTimestamp("created_at").getTime()));
				orders.add(order);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBUtil.close(conn, ps, rs);
		}
		return orders;
	}
	//添加订单
	@Override
	public boolean addOrder(Order order) {
		String sql = "INSERT INTO orders (id, order_number, user, store_id, flower_id, num, unit_price, total_amount, status,created_at)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBUtil.getConnection();                // 获取数据库连接
		     PreparedStatement  pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1,order.getId());
			pstmt.setString(2,order.getOrderNumber());
			pstmt.setString(3,order.getUser());
			pstmt.setInt(4,order.getStoreId());
			pstmt.setInt(5,order.getFlowerId());
			pstmt.setInt(6,order.getNum());
			pstmt.setBigDecimal(7,order.getUnitPrice());
			pstmt.setBigDecimal(8,order.getTotalAmount());
			pstmt.setString(9,order.getStatus());
			pstmt.setTimestamp(10, new Timestamp(order.getCreatedAt().getTime()));
			return pstmt.executeUpdate() > 0;//若返回值大于 0，表示至少有一行被插入，返回true；否则返回false。
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
			
		}
	}
	//根据用户名得到该用户的所有订单
	@Override
	public List<Order> getOrdersByUser(String user) {
		String sql="SELECT * FROM orders where user = ?";
		return excutequery(sql,new Object[] {user});
	}
	//更改订单状态
	 @Override
	    public boolean updateOrderStatus(int orderId, String status) {
	        String sql = "UPDATE orders SET status = ? WHERE id = ?";
	        try (Connection conn = DBUtil.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            
	            pstmt.setString(1, status);
	            pstmt.setInt(2, orderId);
	            
	            return pstmt.executeUpdate() > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
		//得到用户某一年的全部订单
	    @Override
	    public List<Order> getOrdersByUserAndYear(String username, int year) {
	        List<Order> allOrders = getOrdersByUser(username);
	        List<Order> annualOrders = new ArrayList<>();	        
	        Calendar calendar = Calendar.getInstance();
	        for (Order order : allOrders) {
	            calendar.setTime(order.getCreatedAt());
	            if (calendar.get(Calendar.YEAR) == year) {
	                annualOrders.add(order);
	            }
	        }	        
	        return annualOrders;
	    }
	    //得到年度订单金额
	    @Override
	    public BigDecimal getAnnualTotalAmount(String username, int year) {
	        List<Order> annualOrders = getOrdersByUserAndYear(username, year);
	        BigDecimal total = BigDecimal.ZERO;	        
	        for (Order order : annualOrders) {
	            total = total.add(order.getTotalAmount());
	        }	        
	        return total;
	    }
}

