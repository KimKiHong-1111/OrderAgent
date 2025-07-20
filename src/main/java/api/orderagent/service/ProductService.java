package api.orderagent.service;

import api.orderagent.dto.ProductRecord;
import api.orderagent.crawler.uniform.ProductCrawler;
import api.orderagent.domain.entity.Product;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductCrawler productCrawler;
	private final DynamoDBMapper dynamoDBMapper;

	public SaveResult saveCrawledProducts(List<ProductRecord> records) {
		int autoOrderCount = 0;
		List<ProductRecord> soldOutList = new ArrayList<>();

		for (ProductRecord record : records) {
			try {
				Product product = Product.builder()
					.pk(record.productUrl())
					.sk(record.optionName())
					.productName(record.productName())
					.price(record.price())
					.imageUrl(record.imageUrl())
					.inStock(record.inStock())
					.checkedAt(record.checkedAt() != null ? record.checkedAt() : LocalDateTime.now())
					.build();

				if (!record.inStock()) {
					log.warn("품절 상품 자동 주문 시도: {} [{}]", record.productName(), record.optionName());
					autoOrderCount++;
					soldOutList.add(record);
				}
				dynamoDBMapper.save(product);
				log.info("저장 완료 : {}", product.getProductName() + "[" + product.getSk() + "]");
			} catch (Exception e) {
				log.error("저장 실패: {} / 에러 : {}", record.productName(), e.getMessage());
			}
		}
		return new SaveResult(autoOrderCount, soldOutList);
	}

	public record SaveResult(int autoOrderCount, List<ProductRecord> soldOutRecords) {}

	public List<Product> findAll() {
		return dynamoDBMapper.scan(Product.class, new DynamoDBScanExpression());
	}

	public ProductCrawler getProductCrawler() {
		return productCrawler;
	}
}
