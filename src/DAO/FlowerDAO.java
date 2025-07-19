/*
 * 对鲜花表操作
 * 1.查询所有鲜花
 * 2.根据ID查找鲜花
 * 2.根据鲜花名称查找
 * 3.根据类别查询所有该类别下的鲜花信息
 * 4.查看库存
 * 5.培育新品种入库
 * 6.入库(增加库存)
 * 7.出库(减少库存)
 * */
package DAO;

import java.util.List;
import Entity.Flower;

public interface FlowerDAO {
	List<Flower> getAllFlowers();//查询所有鲜花信息
	Flower getFlowersbyID(int id);//根据ID查询
	Flower getFlowersbyName(String name);//根据花名查询
	List<Flower> getFlowersbytype(String type);//根据类型查找
	List<Flower> getStockByStoreID(int storeid);//查看自家花店库存
	boolean addNewFlower(Flower flower);//培育新品种
	boolean addFlower(int id,int num);//入库
	boolean outFlower(int id,int num);//出库
}
