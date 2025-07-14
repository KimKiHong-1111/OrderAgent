package api.orderagent.domain.repository;

import api.orderagent.domain.entity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {


}
