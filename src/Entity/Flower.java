//鲜花表
package Entity;

import java.math.BigDecimal;
import java.util.Date;

public class Flower {
	private int id;						//ID编号
	private String name;				//鲜花名称
	private String type;				//品种
	private BigDecimal purchasePrice;	//入库价格
    private BigDecimal salePrice;		//销售价格
    private int stock;		     		//库存数量
    private int storeId;         		//所属花店ID（外键） 
    private Date createdAt;             //创建时间/入库时间
    private String meaning;				//花语
    //构造方法
    public Flower() { };
    //全参构造方法
	public Flower(int id, String name, String type, BigDecimal purchasePrice, BigDecimal salePrice, int stock,
			int storeId, Date createdAt, String meaning) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.purchasePrice = purchasePrice;
		this.salePrice = salePrice;
		this.stock = stock;
		this.storeId = storeId;
		this.createdAt = createdAt;
		this.meaning = meaning;
	    this.createdAt = new Date(); // 默认初始化为当前时间
	}
	// 业务构造方法（无ID和创建时间，适用于新增鲜花）
    public Flower(String name, String type, BigDecimal purchasePrice, 
                 BigDecimal salePrice, int stock, int storeId, String meaning) {
        this.name = name;
        this.type = type;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.setStock(stock);
        this.storeId = storeId;
        this.meaning = meaning;
        this.createdAt = new Date(); // 自动设置当前时间为创建时间
    }

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public BigDecimal getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		if(stock < 0) {
	        throw new IllegalArgumentException("库存不能为负数");
	    }
	    this.stock = stock;
	}
	public int getStoreId() {
		return storeId;
	}
	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getMeaning() {
		return meaning;
	}
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
}

