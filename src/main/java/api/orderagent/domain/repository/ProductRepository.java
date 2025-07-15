package api.orderagent.domain.repository;

import api.orderagent.domain.entity.Product;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findByName(String name);

}
