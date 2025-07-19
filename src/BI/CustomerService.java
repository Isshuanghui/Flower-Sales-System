/*
 * 1.查询鲜花
 * 2.购买鲜花
 * 3.查看订单
 * 4.登录
 * 5.注册
 * 6.查看所有花店*/
package BI;

import java.math.BigDecimal;
import java.util.List;

import Entity.Flower;
import Entity.FlowerStore;
import Entity.Order;

public interface CustomerService {
	//1.查询鲜花，根据花店ID查询所有鲜花，根据类别查询鲜花,根据鲜花名称查询鲜花
    List<Flower> searchFlowersByStoreAndName(int storeId, String name);
    List<Flower> searchFlowersByStoreAndType(int storeId, String type);	
	//2.购买鲜花(鲜花ID,购买数量)
	Order BuyFlower(String user,int flowerid,int num,boolean ispaid);
	//3.查看订单,根据用户名
	List<Order> getOrdersByUser(String user);
	//4.登录
	boolean Login(String username,String password);
	//5.注册
	boolean Register(String username, String password, String name, 
            String phone, String address);
	//6.查看所有花店
	List<FlowerStore> getAllFlowerStores();
	//根据鲜花ID得到鲜花信息
	Flower getFlowerByID(int id);
	//查询用户在某一年的订单列表
	List<Order> getOrdersByUserAndYear(String username, int year);
	//获取指定用户本年度订单总金额
    BigDecimal getAnnualTotalAmount(String username, int year);
}
