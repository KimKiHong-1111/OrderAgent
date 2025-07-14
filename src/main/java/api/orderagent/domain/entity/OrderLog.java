package api.orderagent.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLog extends BaseTimeEntity{

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String triggerCondition; // 재주문 조건 ex. 재고 10개미만 등
	private String status; //SUCCESS, FAIL, SIMULATED

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "option_stock_id")
	private OptionStock optionStock; // null가능

	private LocalDateTime orderedAt;

}
