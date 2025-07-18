package api.orderagent.runner;

import api.orderagent.crawler.dto.ProductRecord;
import api.orderagent.crawler.uniform.ProductCrawler;
import api.orderagent.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductInitRunner implements ApplicationRunner {

	private final ProductCrawler productCrawler;
	private final ProductService productService;

	@Override
	public void run(ApplicationArguments args) {
		List<ProductRecord> records = productCrawler.crawl();
		productService.saveCrawledProducts(records);
	}

}
