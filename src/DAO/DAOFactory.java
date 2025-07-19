package DAO;

public class DAOFactory {
	//私有构造防止实例化
	private DAOFactory() {}
	public static OrderDAO getOrderDAO() {
		return new OrderDAOImpl();
	}
	public static FlowerDAO getFlowerDAO() {
		return new FlowerDAOImpl();
	}
	public static CustomerDAO getCustomerDAO() {
		return new CustomerDAOImpl();
	}
	public static FlowerStoreDAO getFlowerStoreDAO() {
		return new FlowerStoreDAOImpl();
	}
}
