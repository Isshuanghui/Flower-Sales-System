package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {

	private static String driver="com.mysql.cj.jdbc.Driver";
//	private static String url="jdbc:mysql://127.0.0.1:3306/flowersystem?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=GMT%2B8";
	private static String user = "root";
	private static String pwd ="123456";
	// 修改DBUtil中的URL
	private static final String url = "jdbc:mysql://localhost:3306/flowersystem?" +
	                                  "useSSL=false&" +
	                                  "allowPublicKeyRetrieval=true&" +  // 允许公钥检索
	                                  "serverTimezone=UTC";


	//1. 驱动器注册(一次)  2. 获取Connection  3. 关闭操作
	static{
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection(){
		try {
			Connection conn = DriverManager.getConnection(url,user,pwd);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}//多态 实现类的对象-》屏蔽了实现类的具体信息，只关注的功能
		return null;
	}

	
	public static void close(Connection conn, PreparedStatement st, ResultSet rs){
		//打开的资源关闭，逆序
		try {
			if(rs != null)
				rs.close();
			if(st != null)
				st.close();
			if(conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

