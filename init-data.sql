-- Customer Service Database (customer_db)
INSERT INTO customer (name, email, address, phone) VALUES
('John Doe', 'john.doe@email.com', '123 Main St, City', '+1234567890'),
('Jane Smith', 'jane.smith@email.com', '456 Park Ave, Town', '+1234567891'),
('Mike Johnson', 'mike.j@email.com', '789 Oak Rd, Village', '+1234567892'),
('Sarah Williams', 'sarah.w@email.com', '321 Pine St, County', '+1234567893'),
('David Brown', 'david.b@email.com', '654 Maple Dr, State', '+1234567894');

-- Product Service Database (product_db)
INSERT INTO product  (name, description, price, quantity) VALUES
('Laptop', 'High-performance gaming laptop', 1299.99, 50),
('Smartphone', 'Latest model smartphone', 899.99, 100),
('Headphones', 'Wireless noise-cancelling headphones', 199.99, 200),
('Tablet', '10-inch tablet with stylus', 599.99, 75),
('Smartwatch', 'Fitness tracking smartwatch', 299.99, 150);

-- Inventory Service Database (inventory_db)
INSERT INTO inventory (product_id, quantity) VALUES
(1, 50),  -- Laptop inventory
(2, 100), -- Smartphone inventory
(3, 200), -- Headphones inventory
(4, 75),  -- Tablet inventory
(5, 150); -- Smartwatch inventory

-- Payment Service Database (payment_db)
-- Note: Payments will be created through the order process

-- Order Service Database (order_db)
-- First, create orders
INSERT INTO orders (customer_id, order_date, total_amount, status) VALUES
(1, CURRENT_TIMESTAMP, 1499.98, 'COMPLETED'),
(2, CURRENT_TIMESTAMP, 899.99, 'COMPLETED'),
(3, CURRENT_TIMESTAMP, 399.98, 'COMPLETED'),
(4, CURRENT_TIMESTAMP, 899.98, 'COMPLETED'),
(5, CURRENT_TIMESTAMP, 499.98, 'COMPLETED');

-- Then, create order items
INSERT INTO order_item (order_id, product_id, quantity, price, subtotal) VALUES
(1, 1, 1, 1299.99, 1299.99),  -- Laptop in first order
(1, 3, 1, 199.99, 199.99),    -- Headphones in first order
(2, 2, 1, 899.99, 899.99),    -- Smartphone in second order
(3, 3, 2, 199.99, 399.98),    -- 2 Headphones in third order
(4, 2, 1, 899.99, 899.99),    -- Smartphone in fourth order
(5, 3, 1, 199.99, 199.99),    -- Headphones in fifth order
(5, 5, 1, 299.99, 299.99);    -- Smartwatch in fifth order

# Test Endpoints

## Base URLs
```
Eureka: http://localhost:8761
API Gateway: http://localhost:8080
```

## Product Service
```
# Get all products
GET http://localhost:8080/api/products

# Get product by ID (replace {id} with 1-5)
GET http://localhost:8080/api/products/1

# Create new product
POST http://localhost:8080/api/products
Content-Type: application/json

{
    "name": "New Laptop",
    "description": "Latest model",
    "price": 1499.99,
    "quantity": 50
}

# Clear Redis cache
POST http://localhost:8080/api/products/clear-cache
```

## Customer Service
```
# Get all customers
GET http://localhost:8080/api/customers

# Get customer by ID (replace {id} with 1-5)
GET http://localhost:8080/api/customers/1

# Create new customer
POST http://localhost:8080/api/customers
Content-Type: application/json

{
    "name": "Test Customer",
    "email": "test@email.com",
    "address": "Test Address",
    "phone": "+1234567890"
}
```

## Order Service
```
# Get all orders
GET http://localhost:8080/api/orders

# Get order by ID (replace {id} with 1-5)
GET http://localhost:8080/api/orders/1

# Create order (success case)
POST http://localhost:8080/api/orders
Content-Type: application/json

{
    "customerId": 1,
    "items": [
        {
            "productId": 1,
            "quantity": 1
        },
        {
            "productId": 3,
            "quantity": 1
        }
    ]
}

# Create order (failure case - over stock)
POST http://localhost:8080/api/orders
Content-Type: application/json

{
    "customerId": 1,
    "items": [
        {
            "productId": 1,
            "quantity": 1000
        }
    ]
}
```

## Payment Service
```
# Get all payments
GET http://localhost:8080/api/payments

# Get payment by order ID (replace {orderId} with 1-5)
GET http://localhost:8080/api/payments/order/1
```

## Inventory Service
```
# Get inventory by product ID (replace {productId} with 1-5)
GET http://localhost:8080/api/inventory/product/1

# Get inventory transactions by order ID (replace {orderId} with 1-5)
GET http://localhost:8080/api/inventory/transactions/order/1
```

## Sample Data Reference
```
Customers: ID 1-5
Products: ID 1-5
  - Laptop (ID: 1) - $1,299.99
  - Smartphone (ID: 2) - $899.99
  - Headphones (ID: 3) - $199.99
  - Tablet (ID: 4) - $599.99
  - Smartwatch (ID: 5) - $299.99
```

-- Create payments for orders
INSERT INTO payment (order_id, customer_id, amount, payment_date, status) VALUES
(1, 1, 1499.98, CURRENT_TIMESTAMP, 'COMPLETED'),
(2, 2, 899.99, CURRENT_TIMESTAMP, 'COMPLETED'),
(3, 3, 399.98, CURRENT_TIMESTAMP, 'COMPLETED'),
(4, 4, 899.98, CURRENT_TIMESTAMP, 'COMPLETED'),
(5, 5, 499.98, CURRENT_TIMESTAMP, 'COMPLETED');
