package api.orderagent.runner;

import api.orderagent.crawler.dto.ProductRecord;
import api.orderagent.crawler.uniform.ProductCrawler;
import api.orderagent.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductInitRunner implements ApplicationRunner {

	private final ProductCrawler productCrawler;
	private final ProductService productService;

	@Override
	public void run(ApplicationArguments args) {
		List<ProductRecord> records = productCrawler.crawl();
		log.info("📦 크롤링된 상품 수: {}", records.size());
		productService.saveCrawledProducts(records);
		log.info("✅ 최종 크롤링 완료된 상품 수: {}", records.size());
	}

}
