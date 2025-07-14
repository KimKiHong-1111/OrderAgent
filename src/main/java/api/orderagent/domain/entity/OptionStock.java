package api.orderagent.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class OptionStock {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String optionName;// 예시"XL-오승환"
	private boolean inStock;//재고 있음 여부
	private Integer stockCount; //추정 간으한 경우만 사용(nullable)
	private LocalDateTime checkedAt; //크롤링한 시점

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

}
