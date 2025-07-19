//订单
package Entity;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    // 订单状态常量
    public static final String STATUS_PENDING = "PENDING"; // 待支付
    public static final String STATUS_COMPLETED = "COMPLETED"; // 已完成
    
    private int id;               // 订单ID（主键）
    private String orderNumber;   // 订单号（唯一）
    private String user;		  // 用户名(外键)
    private int storeId;          // 花店ID（外键）
    private int flowerId;		  //鲜花ID（外键）
    private int num;			  //购买数量
    private BigDecimal unitPrice; // 购买时单价 
    private BigDecimal totalAmount; // 订单总金额
    private String status;        // 订单状态
    private Date createdAt;       // 下单时间
    public Order() {	}
	public Order(int id, String orderNumber,String user, int storeId, int flowerId, int num, BigDecimal unitPrice,
			BigDecimal totalAmount, String status, Date createdAt) {
		super();
		this.id = id;
		this.orderNumber = orderNumber;
		this.user=user;
		this.storeId = storeId;
		this.flowerId = flowerId;
		this.num = num;
		this.unitPrice = unitPrice;
		this.totalAmount = totalAmount;
		this.status = status;
		this.createdAt = createdAt;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public int getStoreId() {
		return storeId;
	}
	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}
	public int getFlowerId() {
		return flowerId;
	}
	public void setFlowerId(int flowerId) {
		this.flowerId = flowerId;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
}
