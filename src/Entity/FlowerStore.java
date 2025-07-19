//鲜花商店表
package Entity;

import java.util.Date;

public class FlowerStore {
    private int id;           // 商店ID（主键）
    private String storeName; // 商店名称（用户名）
    private String pwd;		  // 商店密码
    private String owner;     // 店主姓名
    private String address;   // 商店地址
    private String phone;     // 商店电话
    private Date createdAt;   //账户创建时间
    
    public FlowerStore() { };
    
    public FlowerStore(int id, String storeName, String pwd, String owner, String address, String phone,
			Date createdAt) {
		super();
		this.id = id;
		this.storeName = storeName;
		this.pwd = pwd;
		this.owner = owner;
		this.address = address;
		this.phone = phone;
		this.createdAt = createdAt;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
