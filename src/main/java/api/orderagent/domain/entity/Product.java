package api.orderagent.domain.entity;

import api.orderagent.crawler.dto.ProductRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseTimeEntity{

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(unique = true, length = 1000)
	private String productUrl;

	@Column(nullable = false)
	private String imageUrl;

	private int price;

	@Column(nullable = false)
	private boolean soldOut;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<OptionStock> optionStocks = new ArrayList<>();

	public void update(ProductRecord record) {
		this.name = record.name();
		this.price = record.price();
		this.imageUrl = record.imageUrl();
		this.soldOut = record.soldOut();
	}
}
