package api.orderagent.service;

import api.orderagent.crawler.dto.ProductRecord;
import api.orderagent.crawler.option.OptionCrawler;
import api.orderagent.domain.entity.OptionStock;
import api.orderagent.domain.entity.OrderLog;
import api.orderagent.domain.entity.Product;
import api.orderagent.domain.repository.OptionStockRepository;
import api.orderagent.domain.repository.OrderLogRepository;
import api.orderagent.domain.repository.ProductRepository;
import java.time.LocalDateTime;
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
	private final OrderLogRepository orderLogRepository;
	private final OptionCrawler optionCrawler;

	public void saveOptionsForAllProducts() {
		List<Product> products = productRepository.findAll();

		for (Product product : products) {
			List<OptionStock> crawledOptions = optionCrawler.crawlOptions(product);
			for (OptionStock option : crawledOptions) {
				optionStockRepository.findByProductAndOptionName(option.getProduct(), option.getOptionName())
					.ifPresentOrElse(existing -> {
						boolean wasInStock = existing.isInStock();
						boolean isNowOutOfStock = !option.isInStock();

						existing.updateWith(option);
						optionStockRepository.save(existing);

						if (wasInStock && isNowOutOfStock) {
							triggerAutoOrder(existing);
						}

					}, () -> {
						optionStockRepository.save(option);
						if (!option.isInStock()) {
							triggerAutoOrder(option);
						}
					});
			}
			log.info("[{}] 옵션 {}개 저장 완료", product.getName(), crawledOptions.size());
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

	private void triggerAutoOrder(OptionStock option) {
		log.info("자동 주문 실행: [{} - {}] 품절 감지", option.getProduct(), option.getOptionName());

		OrderLog logEntry = OrderLog.builder()
			.triggerCondition("품절 감지")
			.status("SIMULATED")
			.product(option.getProduct())
			.optionStock(option)
			.orderedAt(LocalDateTime.now())
			.build();

		orderLogRepository.save(logEntry);

	}
}
