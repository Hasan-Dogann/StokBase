package stokapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stokapp.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIlacIsmiContainingIgnoreCase(String ilacIsmi);

    List<Product> findByActiveTrue();

    List<Product> findByActiveTrueAndIlacIsmiContainingIgnoreCase(String ilacIsmi);
}