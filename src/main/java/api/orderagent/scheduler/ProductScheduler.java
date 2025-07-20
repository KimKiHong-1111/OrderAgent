package api.orderagent.scheduler;

import api.orderagent.crawler.dto.ProductRecord;
import api.orderagent.service.ProductService;
import api.orderagent.service.ProductService.SaveResult;
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

	@Scheduled(cron = "0 0/50 * * * ?")
	public void autoCrawl() {
		List<ProductRecord> records = productService.getProductCrawler().crawl();
		log.info("📦 크롤링된 상품 수: {}", records.size());
		SaveResult result = productService.saveCrawledProducts(records);

		log.info("✅ 최종 크롤링 완료된 상품 수: {}", records.size());
		log.info("🛒 자동 주문 시도된 품절 상품 수: {}", result.autoOrderCount());

		if (!result.soldOutRecords().isEmpty()) {
			log.info("품절 상품 목록:");
			for (ProductRecord soldOut : result.soldOutRecords()) {
				log.info("⛔ {} [{}]", soldOut.productName(), soldOut.optionName());
			}
		}
	}
}
