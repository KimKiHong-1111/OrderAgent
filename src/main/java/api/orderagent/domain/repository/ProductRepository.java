package api.orderagent.domain.repository;

import api.orderagent.domain.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findByName(String name);
	boolean existsByProductUrl(String productUrl); //URL 기준 중복 여부 확인

}
