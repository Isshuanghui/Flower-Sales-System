//顾客表,映射数据库中的customer表结构，作为数据载体在各层间传递
package Entity;

import java.util.Date;

public class Customer {
    private int id;           //顾客唯一标识符
    private String username;  //登录账号
    private String password;  //登录密码
    private String name;      //顾客真实姓名
    private String phone;     //联系方式
    private String address;   //收获地址
    private Date createdAt;   //账户创建时间
    public Customer() {};
	public Customer(int id, String username, String password, String name, String phone, String address,
			Date createdAt) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.createdAt = createdAt;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
