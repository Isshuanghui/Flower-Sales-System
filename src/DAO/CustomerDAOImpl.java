//实现CustomerDAO接口，通过 JDBC 操作数据库
package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import Entity.Customer;
import Util.DBUtil;

public class CustomerDAOImpl implements CustomerDAO{
	//添加顾客
	@Override
	public boolean addCustomer(Customer customer) {
		String sql = "INSERT INTO customer (id,username,password,name,phone,address,created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBUtil.getConnection();                // 获取数据库连接
	        PreparedStatement  pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, customer.getId());
			pstmt.setString(2, customer.getUsername());
			pstmt.setString(3, customer.getPassword());
			pstmt.setString(4, customer.getName());
			pstmt.setString(5, customer.getPhone());
			pstmt.setString(6, customer.getAddress());
			pstmt.setTimestamp(7, new Timestamp(customer.getCreatedAt().getTime()));
			return pstmt.executeUpdate() > 0;//若返回值大于 0，表示至少有一行被插入，返回true；否则返回false。
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	
	}
	//查询顾客信息根据用户名
	@Override
	public Customer getCustomerbyun(String username) {
		String sql ="select * from customer where username=?";
		return excutequery(sql,new Object[] {username});
	}
	//通用查询方法,params：SQL 参数数组，用于替换 ? 占位符。
		static Customer excutequery(String sql,Object[] params){
			Connection conn = null;
	        PreparedStatement ps = null;
	        ResultSet rs = null;
	        Customer customer = null; 
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
					customer=new Customer(
							rs.getInt("id"),
							rs.getString("username"),
							rs.getString("password"),
		                    rs.getString("name"),
		                    rs.getString("phone"),
		                    rs.getString("address"),
		                    new Date(rs.getTimestamp("created_at").getTime()));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				DBUtil.close(conn, ps, rs);
			}
			return customer;
		}
}
