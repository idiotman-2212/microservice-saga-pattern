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