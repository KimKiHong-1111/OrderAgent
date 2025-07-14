package api.orderagent.domain.repository;

import api.orderagent.domain.entity.OptionStock;
import api.orderagent.domain.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionStockRepository extends JpaRepository<OptionStock, Long> {

	List<OptionStock> findByProduct(Product product);

}
