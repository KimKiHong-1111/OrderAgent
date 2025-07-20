package api.orderagent.domain.entity;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class Product {


	private String pk; //상품URL
	private String sk; //옵션명 ex: M,L,XL 등 사이즈
	private String productName;
	private String imageUrl;
	private int price;
	private boolean inStock;
	private LocalDateTime checkedAt;

	@DynamoDbPartitionKey
	@DynamoDbAttribute("pk")
	public String getPk() {
		return pk;
	}

	@DynamoDbSortKey
	@DynamoDbAttribute("sk")
	public String getSk() {
		return sk;
	}

	@DynamoDbAttribute("productName")
	public String getProductName() {
		return productName;
	}

	@DynamoDbAttribute("imageUrl")
	public String getImageUrl() {
		return imageUrl;
	}

	@DynamoDbAttribute("price")
	public int getPrice() {
		return price;
	}

	@DynamoDbAttribute("inStock")
	public boolean isInStock() {
		return inStock;
	}

	@DynamoDbConvertedBy(LocalDateTimeAttributeConverter.class)
	@DynamoDbAttribute("checkedAt")
	public LocalDateTime getCheckedAt() {
		return checkedAt;
	}

	public static class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime> {

		private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

		@Override
		public AttributeValue transformFrom(LocalDateTime input) {
			return AttributeValue.fromS(FORMATTER.format(input));
		}

		@Override
		public LocalDateTime transformTo(AttributeValue input) {
			return LocalDateTime.parse(input.s(), FORMATTER);
		}

		@Override
		public EnhancedType<LocalDateTime> type() {
			return EnhancedType.of(LocalDateTime.class);
		}

		@Override
		public AttributeValueType attributeValueType() {
			return AttributeValueType.S;
		}
	}

}
