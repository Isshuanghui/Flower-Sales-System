package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Entity.Flower;
import Entity.FlowerStore;

import Util.DBUtil;


public class FlowerStoreDAOImpl implements FlowerStoreDAO{
	//打印所有花店信息
	@Override
	public List<FlowerStore> getAllStores(){
		String sql = "SELECT * FROM flower_store";
		return excutequery(sql,null);
	}
	//通用查询方法,params：SQL 参数数组，用于替换 ? 占位符。
	private List<FlowerStore> excutequery(String sql,Object[] params){
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<FlowerStore> stores = new ArrayList<>(); 
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
            while (rs.next()) {
                FlowerStore store = new FlowerStore(
                    rs.getInt("id"),
                    rs.getString("store_name"),
                    rs.getString("pwd"),
                    rs.getString("owner"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    new Date(rs.getTimestamp("created_at").getTime())
                );
                stores.add(store); // 将对象添加到列表
            }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBUtil.close(conn, ps, rs);
		}
		return stores;
	}
	//根据花店ID得到花店全部信息
	@Override
	public FlowerStore getStoreById(int storeid) {
	    List<FlowerStore> stores = excutequery("SELECT * FROM flower_store where id=?", 
                new Object[]{storeid});
	    return stores.isEmpty() ? null : stores.get(0);
		
	}
	//根据店名找到店
	@Override
	public FlowerStore getStoreByName(String storeName) {
	    List<FlowerStore> stores = excutequery("SELECT * FROM flower_store WHERE store_name = ?", 
	                                           new Object[]{storeName});
	    return stores.isEmpty() ? null : stores.get(0);  // 返回第一个结果或 null
	}
	//根据花店ID得到该花店下所有鲜花
	@Override
	public List<Flower> getFlowersByStoreID(int storeid) {
		String sql = "SELECT f.* FROM flower f " +
                "JOIN flower_store fs ON f.store_id = fs.id " +
                "WHERE fs.id = ?";
		// 2. 调用FlowerDAO的通用查询方法（返回Flower列表）
		return FlowerDAOImpl.excutequery(sql, new Object[]{storeid});
	}
	//根据花店名得到该花店下所有鲜花
	@Override
	public List<Flower> getFlowersByStorename(String storename) {
	    String sql = "SELECT f.* " +
                "FROM flower f " +
                "JOIN flower_store fs ON f.store_id = fs.id " +
                "WHERE fs.store_name = ?";
		// 2. 调用FlowerDAO的通用查询方法（返回Flower列表）
		return FlowerDAOImpl.excutequery(sql, new Object[]{storename});
	}
	//新花店入驻
	@Override
	public boolean addFlowerStore(FlowerStore flowerstore) {
		String sql ="INSERT INTO flower_store (store_name,pwd,owner,phone,address,created_at)"+
					"VALUES( ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBUtil.getConnection();                // 获取数据库连接
		        PreparedStatement  pstmt = conn.prepareStatement(sql)){
				pstmt.setString(1, flowerstore.getStoreName());
				pstmt.setString(3, flowerstore.getOwner());
				pstmt.setString(5, flowerstore.getAddress());
				pstmt.setString(4, flowerstore.getPhone());
				pstmt.setString(2, flowerstore.getPwd());
				pstmt.setTimestamp(6, new Timestamp(flowerstore.getCreatedAt().getTime()));
				return pstmt.executeUpdate() > 0;//若返回值大于 0，表示至少有一行被插入，返回true；否则返回false。
			}catch (SQLException e) {
				e.printStackTrace();
				return false;
			}	
	}

}

