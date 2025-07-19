/*
 * 1.登录
 * 2.注册
 * 3.查看库存
 * 4.销售鲜花（商品的入库价，卖出价，利润）
 * 5.查看订单
 * 6.查看销售情况
 * 7.培育新品种鲜花
 * 8.入库
 * 9.出库
 * */
package BI;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import Entity.Flower;
import Entity.FlowerStore;
import Entity.Order;

public interface FlowerStoreService {
	//登录
	boolean ALogin(String storename,String password);
	//注册
	boolean ARegister(String storename,String pwd,String owner,String phone,String address);	
	//查看库存
	List<Flower> getFStockByFSname(String storename);
	//查看订单
	List<Order> getOrdersbySname(String storename);
	//查看销售情况
	Map<Integer, int[]> getSalesSituation(String storename);
	//培育新品种鲜花
	boolean addFlower(Flower flower);
	//入库
	boolean addStock(int id,int num);
	//出库
	boolean outStock(int id,int num);
	//销售鲜花
	boolean sellFlower(String storename, int flowerId, int quantity, BigDecimal unitPrice,String customerName, String customerPhone);
	//根据花店名获取花店信息
	FlowerStore getStoreByStoreName(String name);
}
