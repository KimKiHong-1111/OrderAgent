package api.orderagent.domain.repository;

import api.orderagent.domain.entity.OptionStock;
import api.orderagent.domain.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionStockRepository extends JpaRepository<OptionStock, Long> {

	Optional<OptionStock> findByProductAndOptionName(Product product, String optionName);
}
