package com.example.productservice.service;

import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_KEY_PREFIX = "product:";
    private static final String REDIS_LIST_KEY = "products:all";

    public List<Product> getAllProducts() {
        // Try to get from Redis
        @SuppressWarnings("unchecked")
        List<Product> cachedProducts = (List<Product>) redisTemplate.opsForValue().get(REDIS_LIST_KEY);
        
        if (cachedProducts != null) {
            return cachedProducts;
        }

        // If not in Redis, get from DB and cache
        List<Product> products = productRepository.findAll();
        redisTemplate.opsForValue().set(REDIS_LIST_KEY, products, 1, TimeUnit.HOURS);
        return products;
    }

    public Product getProductById(Long id) {
        String key = REDIS_KEY_PREFIX + id;
        
        // Try to get from Redis
        Product cachedProduct = (Product) redisTemplate.opsForValue().get(key);
        if (cachedProduct != null) {
            return cachedProduct;
        }

        // If not in Redis, get from DB and cache
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        redisTemplate.opsForValue().set(key, product, 1, TimeUnit.HOURS);
        return product;
    }

    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        clearCache();
        return savedProduct;
    }

    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        
        Product updatedProduct = productRepository.save(existingProduct);
        clearCache();
        return updatedProduct;
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        clearCache();
    }

    public void clearCache() {
        redisTemplate.delete(REDIS_LIST_KEY);
        // Could also delete individual product keys if needed
    }
}
