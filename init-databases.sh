#!/bin/bash

# Customer Service Database
PGPASSWORD=postgres psql -h localhost -U postgres -d customer_db -f init-data.sql

# Product Service Database
PGPASSWORD=postgres psql -h localhost -U postgres -d product_db -f init-data.sql

# Inventory Service Database
PGPASSWORD=postgres psql -h localhost -U postgres -d inventory_db -f init-data.sql

# Order Service Database
PGPASSWORD=postgres psql -h localhost -U postgres -d order_db -f init-data.sql

# Payment Service Database
PGPASSWORD=postgres psql -h localhost -U postgres -d payment_db -f init-data.sql
