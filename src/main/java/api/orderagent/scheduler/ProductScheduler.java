package api.orderagent.scheduler;

import api.orderagent.crawler.dto.ProductRecord;
import api.orderagent.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductScheduler {

	private final ProductService productService;

	@Scheduled(cron = "0 0/30 * * * ?")
	public void autoCrawl() {
		List<ProductRecord> records = productService.getProductCrawler().crawl();
		log.info("📦 크롤링된 상품 수: {}", records.size());
		productService.saveCrawledProducts(records);
		log.info("✅ 최종 크롤링 완료된 상품 수: {}", records.size());
	}
}
