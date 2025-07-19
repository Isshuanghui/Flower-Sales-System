package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Entity.Flower;
import Util.DBUtil;

public class FlowerDAOImpl implements FlowerDAO{
	//查询所有鲜花信息
	@Override
    public List<Flower> getAllFlowers() {
        String sql = "SELECT * FROM flower";
         return excutequery(sql,null);
    }
	//根据ID查询鲜花信息
	@Override
	public Flower getFlowersbyID(int id){
		List<Flower> flower=excutequery("SELECT * FROM flower where id =?",new Object[] {id});
		return flower.isEmpty() ? null :flower.get(0);
	}
	//根据花名查询鲜花信息
	@Override
	public Flower getFlowersbyName(String name){
		List<Flower> flower=excutequery("SELECT * FROM flower where name =?",new Object[] {name});
		return flower.isEmpty() ? null :flower.get(0);
	}
	//根据类型查找鲜花
	@Override
    public List<Flower> getFlowersbytype(String type) {	
       String sql = "SELECT * FROM flower where type =?";      
       return excutequery(sql,new Object[] {type});
	}
	//查询库存
	@Override
    public List<Flower> getStockByStoreID(int storeid) {
		String sql="SELECT * FROM flower where id=?";
		return excutequery(sql,new Object[] {storeid});
	}
	//通用查询方法,params：用于查询的SQL 参数数组，用于替换 ? 占位符。
	static List<Flower> excutequery(String sql,Object[] params){
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Flower> flowers = new ArrayList<>(); 
        try {
            conn = DBUtil.getConnection();                 // 获取数据库连接
            ps = conn.prepareStatement(sql);			   //预编译SQL语句
            // 设置参数,如果参数数组不为空，就遍历该数组，将参数按顺序设置到预编译语句中
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);//若 params = ["玫瑰"]，则 ps.setObject(1, "玫瑰") 替换第一个 ?。
                }
            }
            rs = ps.executeQuery(); //执行预编译的查询语句，并获取结果集rs。
			while(rs.next()) {
				//针对每一行数据，创建一个Flower对象，将结果集中各字段的值映射到Flower对象的对应属性上。
				Flower flower=new Flower(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("type"),
	                    rs.getBigDecimal("purchase_price"),
	                    rs.getBigDecimal("sale_price"),
	                    rs.getInt("stock"),
	                    rs.getInt("store_id"),
	                    new Date(rs.getTimestamp("created_at").getTime()), // 转换时间戳为Date对象
	                    rs.getString("meaning"));
				flowers.add(flower);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBUtil.close(conn, ps, rs);
		}
		return flowers;
	}
	//培育新品种
	@Override
	public boolean addNewFlower(Flower flower) {
		String sql = "INSERT INTO flower (id,name,type,purchase_price,sale_price,stock, store_id,created_at,meaning) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBUtil.getConnection();                // 获取数据库连接
	        PreparedStatement  pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, flower.getId());
			pstmt.setString(2, flower.getName());
			pstmt.setString(3, flower.getType());
			pstmt.setBigDecimal(4, flower.getPurchasePrice());
			pstmt.setBigDecimal(5, flower.getSalePrice());
			pstmt.setInt(6, flower.getStock());
			pstmt.setInt(7, flower.getStoreId());
	        // 安全处理createdAt字段
	        Timestamp timestamp = flower.getCreatedAt() != null 
	            ? new Timestamp(flower.getCreatedAt().getTime()) 
	            : new Timestamp(System.currentTimeMillis());
	        pstmt.setTimestamp(8, timestamp);
			pstmt.setString(9, flower.getMeaning());	
			return pstmt.executeUpdate() > 0;//若返回值大于 0，表示至少有一行被插入，返回true；否则返回false。
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	
	}
	//入库
	public boolean addFlower(int id,int num) {
		 // 1. 参数校验
	    if (num <= 0) {
	        throw new IllegalArgumentException("入库数量必须大于0");
	    }
	    String sql = "UPDATE flower SET stock = stock + ? WHERE id = ?";
		return both(sql,id,num);
	}
	//出库
	@Override
	public boolean outFlower(int id,int num) {
		String sql = "UPDATE flower SET stock =stock - ? WHERE id = ?";
		return both(sql,id,num);
	}
	//出入库共同操作
	private boolean both(String sql,int id,int num) {
		try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {	            
	            pstmt.setInt(1, num);
	            pstmt.setInt(2, id);      
	            return pstmt.executeUpdate() > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	}
}
