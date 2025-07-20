package api.orderagent;

import api.orderagent.dto.ProductRecord;
import api.orderagent.service.ProductService;
import api.orderagent.service.ProductService.SaveResult;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class OrderAgentApplication {


	@Bean
	public Function<String, String> orderAgentFunction(ProductService productService) {
		return input -> {
			List<ProductRecord> records = productService.getProductCrawler().crawl();
			SaveResult result = productService.saveCrawledProducts(records);

			log.info("📦 크롤링된 상품 수: {}", records.size());
			log.info("🛒 자동 주문 시도된 품절 상품 수: {}", result.autoOrderCount());

			if (!result.soldOutRecords().isEmpty()) {
				log.info("📋 품절 상품 목록:");
				for (ProductRecord soldOut : result.soldOutRecords()) {
					log.info("⛔ {} [{}]", soldOut.productName(), soldOut.optionName());
				}
			}
			return "✅ Lambda 크롤링 완료: 총 " + records.size() + "개, 품절 " + result.autoOrderCount() + "개";
		};
	}
}
