package api.orderagent.domain.repository;

import api.orderagent.domain.entity.OptionStock;
import api.orderagent.domain.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OptionStockRepository extends JpaRepository<OptionStock, Long> {

	@Query("SELECT o FROM OptionStock o WHERE o.product.id = :productId AND o.optionName = :optionName")
	Optional<OptionStock> findByProductIdAndOptionName(@Param("productId") Long productId, @Param("optionName") String optionName);
}
