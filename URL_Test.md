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

# Update product by ID
PUT http://localhost:8080/api/products/1
Content-Type: application/json

{
    "name": "Updated Laptop",
    "description": "Updated description",
    "price": 1599.99,
    "quantity": 60
}

# Delete product by ID
DELETE http://localhost:8080/api/products/1

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

# Update customer by ID
PUT http://localhost:8080/api/customers/1
Content-Type: application/json

{
    "name": "Updated Customer",
    "email": "updated@email.com",
    "address": "Updated Address",
    "phone": "+0987654321"
}

# Delete customer by ID
DELETE http://localhost:8080/api/customers/1
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
            "quantity": 1,
            "price": 1299.99
        },
        {
            "productId": 3,
            "quantity": 1,
            "price": 199.99
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
            "quantity": 1000,
            "price": 1299.99
        }
    ]
}

# Delete order by ID
DELETE http://localhost:8080/api/orders/1
```

## Payment Service
```
# Get all payments
GET http://localhost:8080/api/payments

# Get payment by ID
GET http://localhost:8080/api/payments/1

# Refund payment by ID
POST http://localhost:8080/api/payments/1/refund
```

## Inventory Service
```
# Get all inventory
GET http://localhost:8080/api/inventory

# Get inventory by product ID (replace {productId} with 1-5)
GET http://localhost:8080/api/inventory/product/1

# Create new inventory
POST http://localhost:8080/api/inventory
Content-Type: application/json

{
    "productId": 6,
    "quantity": 100
}

# Update inventory quantity by product ID
PUT http://localhost:8080/api/inventory/product/1?quantity=150

# Delete inventory by ID
DELETE http://localhost:8080/api/inventory/1
```

## Saga Pattern Test Scenarios

### 1. Successful Order Flow
```
# Step 1: Create order (triggers saga)
POST http://localhost:8080/api/orders
Content-Type: application/json

{
    "customerId": 1,
    "items": [
        {
            "productId": 1,
            "quantity": 1,
            "price": 1299.99
        }
    ]
}

# Expected flow:
# 1. Order created → status: CREATED
# 2. Payment processed → status: PAYMENT_PENDING → PAYMENT_COMPLETED
# 3. Inventory updated → status: INVENTORY_PENDING → COMPLETED
# 4. Check order status: GET /api/orders/{orderId}
# 5. Check payment: GET /api/payments
# 6. Check inventory: GET /api/inventory/product/1
```

### 2. Failed Order Flow (Inventory Insufficient)
```
# Step 1: Create order with quantity > available stock
POST http://localhost:8080/api/orders
Content-Type: application/json

{
    "customerId": 1,
    "items": [
        {
            "productId": 1,
            "quantity": 1000,
            "price": 1299.99
        }
    ]
}

# Expected flow:
# 1. Order created → status: CREATED
# 2. Payment processed → status: PAYMENT_PENDING → PAYMENT_COMPLETED
# 3. Inventory fails → status: INVENTORY_FAILED
# 4. Saga rollback triggered
# 5. Check order status: GET /api/orders/{orderId}
# 6. Check payment: GET /api/payments
# 7. Check inventory: GET /api/inventory/product/1
```

### 3. Payment Failure Simulation
```
# To simulate payment failure, modify PaymentService.processPaymentWithExternalService()
# Set simulateFailure = true in PaymentService.java

# Then create order:
POST http://localhost:8080/api/orders
Content-Type: application/json

{
    "customerId": 1,
    "items": [
        {
            "productId": 1,
            "quantity": 1,
            "price": 1299.99
        }
    ]
}

# Expected flow:
# 1. Order created → status: CREATED
# 2. Payment fails → status: PAYMENT_FAILED
# 3. Saga stops here (no inventory update)
# 4. Check order status: GET /api/orders/{orderId}
# 5. Check payment: GET /api/payments
```

## Redis Cache Test Scenarios

### 1. First Time Product Fetch (Cache Miss)
```
# Clear cache first
POST http://localhost:8080/api/products/clear-cache

# Get products (will fetch from DB and cache)
GET http://localhost:8080/api/products

# Expected: Log shows "Fetching products from DB and caching in Redis"
```

### 2. Second Time Product Fetch (Cache Hit)
```
# Get products again (will fetch from Redis)
GET http://localhost:8080/api/products

# Expected: Log shows "Fetching products from Redis cache"
```

### 3. Cache Invalidation After Update
```
# Update a product
PUT http://localhost:8080/api/products/1
Content-Type: application/json

{
    "name": "Updated Laptop",
    "description": "Updated description",
    "price": 1599.99,
    "quantity": 60
}

# Get products (cache cleared, will fetch from DB again)
GET http://localhost:8080/api/products

# Expected: Log shows "Fetching products from DB and caching in Redis"
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

Inventory: 
  - Product 1: 50 units
  - Product 2: 30 units
  - Product 3: 100 units
  - Product 4: 25 units
  - Product 5: 75 units
```

## Monitoring & Debugging

### Check Service Status
```
# Eureka Dashboard
http://localhost:8761

# Check all registered services and their health status
```

### Check Kafka Topics
```bash
# List all topics
kafka-topics.sh --list --bootstrap-server localhost:9092

# Check messages in order.created topic
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order.created --from-beginning

# Check messages in payment.completed topic
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic payment.completed --from-beginning

# Check messages in inventory.updated topic
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic inventory.updated --from-beginning
```

### Check Redis Cache
```bash
# Connect to Redis CLI
redis-cli -h localhost -p 6379 -a redis123

# Check all keys
KEYS *

# Check specific product cache
GET product:1

# Check all products cache
GET products:all

# Clear all cache
FLUSHALL
```