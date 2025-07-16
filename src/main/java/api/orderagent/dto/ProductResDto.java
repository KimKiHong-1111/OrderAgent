package api.orderagent.dto;


import api.orderagent.domain.entity.Product;

public record ProductResDto(
	Long id,
	String name,
	int price,
	boolean soldOut,
	String imageUrl
) {
	public static ProductResDto toDto(Product product) {
		return new ProductResDto(
			product.getId(),
			product.getName(),
			product.getPrice(),
			product.isSoldOut(),
			product.getImageUrl()
		);
	}
}
