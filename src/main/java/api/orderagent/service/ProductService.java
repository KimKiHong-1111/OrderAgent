package api.orderagent.service;

import api.orderagent.crawler.UniformCrawler.UniformItem;
import api.orderagent.domain.entity.Product;
import api.orderagent.domain.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	public void saveCrawledProducts(List<UniformItem> itemList) {
		for (UniformItem item : itemList) {
			if (productRepository.existsByProductUrl(item.productUrl())) {
				log.info("이미 등록된 상품: {}", item.name());
				continue;
			}
			Product product = Product.builder()
				.name(item.name())
				.productUrl(item.productUrl())
				.imageUrl(item.imageUrl())
				.price(item.price())
				.build();

			productRepository.save(product);
			log.info("신규 상품 저장: {}", item.name());
		}
	}
}
