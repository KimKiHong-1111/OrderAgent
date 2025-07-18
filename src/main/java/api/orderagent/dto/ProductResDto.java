package api.orderagent.dto;


import api.orderagent.domain.entity.Product;

public record ProductResDto(
	Long id,
	String name,
	int price,
	String imageUrl
) {

}
