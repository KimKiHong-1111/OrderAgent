package api.orderagent.crawler.dto;

import java.time.LocalDateTime;

public record ProductRecord(
	String productName,
	String optionName,
	int price,
	String imageUrl,
	String productUrl,
	boolean inStock,
	LocalDateTime checkedAt
){}
