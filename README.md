# 🌷🌸🌻🌼💐Flower Shop Sales System

##📜 Project Overview
This project is a complete flower shop management system designed to streamline operations for both customers and shop owners. Customers can browse available flowers, place orders, and track their purchase history, while shop owners can manage inventory, process orders, and generate sales reports.

## 🔖Key Features

### 🙋‍♀️Customer Portal
- **Shop Selection**: Choose from multiple flower shop locations.
- **Flower Browsing & Search**: Query flowers by name or category.
- **Shopping Cart**: Add/remove items and view subtotal.
- **Order History**: Track past purchases and delivery status.
- **Annual Statistics**: View personal spending analytics.

### 💁Shop Owner Dashboard
- **Inventory Management**: Real-time stock tracking and updates.
- **Sales Processing**: Manage walk-in and wholesale orders.
- **Order Fulfillment**: Process incoming customer orders.
- **Sales Reports**: Generate profit and revenue analytics.
- **New Product Introduction**: Add new flower varieties.
- **Stock Operations**: Record stock inflow/outflow.

## 🛠️Tech Stack
- **Frontend**: Java Swing GUI components
- **Backend**: Java (Object-Oriented Design)
- **Database**: Relational database for storing shop, customer, flower, and order data

## 🪄Project Structure
```plaintext
FlowerShopSystem/
├── src/
│   ├── UI/                 # User interface classes
│   │   ├── CustomerMainFrame.java
│   │   ├── StoreOwnerMainFrame.java
│   │   ├── RoleSelectionFrame.java
│   │   └── LoginRegisterFrame.java
│   ├── Entity/             # Business entities
│   │   ├── FlowerStore.java
│   │   ├── Customer.java
│   │   ├── Flower.java
│   │   └── Order.java
│   ├── DAO/                # Data access objects
│   │   ├── DAOFactory.java
│   │   ├── FlowerDAOImpl.java
│   │   ├── FlowerStoreDAOImpl.java
│   │   ├── CustomerDAOImpl.java
│   │   └── OrderDAOImpl.java
│   ├── BI/                 # Business logic layer
│   │   ├── FlowerStoreServiceImpl.java
│   │   └── CustomerServiceImpl.java
│   └── module-info.java
└── ...

