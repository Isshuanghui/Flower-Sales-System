/*顾客表接口,声明顾客表需要的访问操作
	定义对顾客表的规范操作
	1.新增顾客信息
	2.查询顾客信息
*/
package DAO;

import Entity.Customer;

public interface CustomerDAO {
	boolean addCustomer(Customer customer);//新增顾客
	Customer getCustomerbyun(String username);//查询顾客信息
}
