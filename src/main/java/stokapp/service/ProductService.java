package stokapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stokapp.entity.Product;
import stokapp.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product product) {
        validateNewProduct(product);
        product.setIlacIsmi(product.getIlacIsmi().trim());
        product.setActive(true);
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findByActiveTrue();
    }

    public List<Product> searchProductsByName(String ilacIsmi) {
        if (ilacIsmi == null || ilacIsmi.trim().isEmpty()) {
            return productRepository.findByActiveTrue();
        }
        return productRepository.findByActiveTrueAndIlacIsmiContainingIgnoreCase(ilacIsmi.trim());
    }

    public Product getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        if (Boolean.FALSE.equals(product.getActive())) {
            throw new RuntimeException("Ürün bulunamadı");
        }

        return product;
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        if (Boolean.FALSE.equals(existingProduct.getActive())) {
            throw new RuntimeException("Pasif ürün güncellenemez");
        }

        if (updatedProduct.getIlacIsmi() != null && !updatedProduct.getIlacIsmi().trim().isEmpty()) {
            existingProduct.setIlacIsmi(updatedProduct.getIlacIsmi().trim());
        }

        if (updatedProduct.getAlisFiyati() != null) {
            if (updatedProduct.getAlisFiyati() < 0) {
                throw new RuntimeException("Alış fiyatı geçersiz");
            }
            existingProduct.setAlisFiyati(updatedProduct.getAlisFiyati());
        }

        if (updatedProduct.getSatisFiyati() != null) {
            if (updatedProduct.getSatisFiyati() < 0) {
                throw new RuntimeException("Satış fiyatı geçersiz");
            }
            existingProduct.setSatisFiyati(updatedProduct.getSatisFiyati());
        }

        if (updatedProduct.getKdv() != null) {
            if (updatedProduct.getKdv() < 0) {
                throw new RuntimeException("KDV geçersiz");
            }
            existingProduct.setKdv(updatedProduct.getKdv());
        }

        if (updatedProduct.getStock() != null) {
            if (updatedProduct.getStock() < 0) {
                throw new RuntimeException("Stok negatif olamaz");
            }
            existingProduct.setStock(updatedProduct.getStock());
        }

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        existingProduct.setActive(false);
        productRepository.save(existingProduct);
    }

    private void validateNewProduct(Product product) {
        if (product.getIlacIsmi() == null || product.getIlacIsmi().trim().isEmpty()) {
            throw new RuntimeException("İlaç ismi boş olamaz");
        }

        if (product.getAlisFiyati() == null || product.getAlisFiyati() < 0) {
            throw new RuntimeException("Alış fiyatı geçersiz");
        }

        if (product.getSatisFiyati() == null || product.getSatisFiyati() < 0) {
            throw new RuntimeException("Satış fiyatı geçersiz");
        }

        if (product.getKdv() == null || product.getKdv() < 0) {
            throw new RuntimeException("KDV geçersiz");
        }

        if (product.getStock() == null || product.getStock() < 0) {
            throw new RuntimeException("Stok geçersiz");
        }
    }
}