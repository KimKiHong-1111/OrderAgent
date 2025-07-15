package api.orderagent.scheduler;

import api.orderagent.crawler.uniform.SamsungUniformCrawler;
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
public class UniformScheduler {

	private final SamsungUniformCrawler crawler;
	private final ProductService productService;

	@Scheduled(cron = "0 0/5 * * * *") // 매 30분마다
	public void runCrawler() {
		log.info("[스케쥴러] 유니폼 크롤링 시작");

		try {
			List<ProductRecord> records = crawler.crawl();
			productService.saveCrawledProducts(records);
			log.info("크롤링 및 저장 완료, 수량 : {}", records.size());
		} catch (Exception e) {
			log.error(" 스케줄링 크롤링 중 에러 발생: {}", e.getMessage() , e);
		}
	}
}
