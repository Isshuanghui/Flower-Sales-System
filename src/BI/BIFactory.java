package BI;

public class BIFactory {
	private BIFactory() {}
	public static CustomerService getCustomerService() {
		return new CustomerServiceImpl();
	}
	public static FlowerStoreService getFlowerStoreService() {
		return new FlowerStoreServiceImpl();
	}
}
