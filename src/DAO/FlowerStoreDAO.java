/*
 * 对鲜花商店操作
 * 1.打印所有花店信息
 * 2.按id选择花店,直接生成该花店下所有鲜花
 * 3.根据店名寻找花店，并生成该花店下所有鲜花
 * 4.新花店入驻
 * */
package DAO;

import java.util.List;
import Entity.Flower;
import Entity.FlowerStore;

public interface FlowerStoreDAO {
	List<FlowerStore> getAllStores();//打印所有花店信息
	FlowerStore getStoreById(int storeid);//按花店ID得到花店
	List<Flower> getFlowersByStoreID(int storeid);//根据花店ID得到该花店下所有鲜花
	FlowerStore getStoreByName(String name); //根据店名找到店
	List<Flower> getFlowersByStorename(String storename);//根据店名得到该花店写所有鲜花
	boolean addFlowerStore(FlowerStore flowerstore);//新花店入驻
}
