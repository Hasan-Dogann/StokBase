package stokapp.repository;

// Bu repository satış fişlerini veritabanından çeker.
import org.springframework.data.jpa.repository.JpaRepository;
import stokapp.entity.Sale;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findBySaleTimeBetween(LocalDateTime start, LocalDateTime end);
}