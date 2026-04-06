package stokapp.repository;

// Bu repository satış kalemlerini yönetmek için kullanılır.
import org.springframework.data.jpa.repository.JpaRepository;
import stokapp.entity.SaleItem;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
}