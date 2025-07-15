package api.orderagent.service;

import api.orderagent.crawler.dto.ProductRecord;
import api.orderagent.crawler.option.OptionCrawler;
import api.orderagent.domain.entity.OptionStock;
import api.orderagent.domain.entity.Product;
import api.orderagent.domain.repository.OptionStockRepository;
import api.orderagent.domain.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final OptionStockRepository optionStockRepository;
	private final OptionCrawler optionCrawler;


	public void saveOptionsForAllProducts() {
		List<Product> products = productRepository.findAll();

		for (Product product : products) {
			List<OptionStock> optionStocks = optionCrawler.crawlOptions(product);
			optionStockRepository.saveAll(optionStocks);
			log.info("[{}} 옵션 {}개 저장 완료", product.getName(), optionStocks.size());
		}
	}


	public void saveCrawledProducts(List<ProductRecord> records) {

		List<Product> existing = productRepository.findAll();
		Map<String, Product> productMap = existing.stream()
			.collect(Collectors.toMap(Product::getProductUrl, Function.identity()));

		List<Product> toSave = new ArrayList<>();

		for (ProductRecord record : records) {
			Product existingProduct = productMap.get(record.productUrl());

			if (existingProduct == null) {
				toSave.add(Product.builder()
					.name(record.name())
					.price(record.price())
					.imageUrl(record.imageUrl())
					.productUrl(record.productUrl())
					.soldOut(record.soldOut())
					.build());
				continue;
			}

			// 품절 -> 판매중 일경우
			if (existingProduct.isSoldOut() && !record.soldOut()) {
				log.info("자동 주문 트리거: {}", record.name());
				triggerAutoOrder(record);
			}

			// 업데이트가 필요한 경우 업데이트
			if (isUpdated(existingProduct, record)) {
				existingProduct.update(record);
				toSave.add(existingProduct);
			}
		}
		productRepository.saveAll(toSave);

	}

	private boolean isUpdated(Product product, ProductRecord record) {
		return !product.getName().equals(record.name())
			|| product.getPrice() != record.price()
			|| !product.getImageUrl().equals(record.imageUrl())
			|| product.isSoldOut() != record.soldOut();
	}

	private void triggerAutoOrder(ProductRecord record) {
		// 현재는 로그 출력만, 추후 슬랙/이메일/웹훅 등으로 확장 가능
		log.info("자동 주문 실행: {}", record.name());
	}
}
