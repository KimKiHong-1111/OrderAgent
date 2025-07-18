package api.orderagent.domain.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDBTable(tableName = "Product")
public class Product {

	@DynamoDBHashKey(attributeName = "pk")
	private String pk; //상품URL

	@DynamoDBRangeKey(attributeName = "sk")
	private String sk; //옵션명 ex: M,L,XL 등 사이즈

	@DynamoDBAttribute(attributeName = "productName")
	private String productName;

	@DynamoDBAttribute(attributeName = "imageUrl")
	private String imageUrl;

	@DynamoDBAttribute(attributeName = "price")
	private int price;

	@DynamoDBAttribute(attributeName = "inStock")
	private boolean inStock;

	@DynamoDBAttribute(attributeName = "checkedAt")
	@DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
	private LocalDateTime checkedAt;

	public static class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {
		@Override
		public String convert(final LocalDateTime time) {
			return time.toString();
		}
		@Override
		public LocalDateTime unconvert(final String stringValue) {
			return LocalDateTime.parse(stringValue);
		}
	}

	public void update(Product product) {
		this.inStock = product.inStock;
		this.checkedAt = product.checkedAt;
		this.price = product.price;
		this.productName = product.productName;
		this.imageUrl = product.imageUrl;
	}
}
