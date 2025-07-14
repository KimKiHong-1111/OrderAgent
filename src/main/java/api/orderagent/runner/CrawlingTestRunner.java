package api.orderagent.runner;

import api.orderagent.crawler.UniformCrawler;
import api.orderagent.crawler.UniformCrawler.UniformItem;
import api.orderagent.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlingTestRunner implements CommandLineRunner {

	private final UniformCrawler uniformCrawler;
	private final ProductService productService;

	@Override
	public void run(String... args) throws Exception {
		List<UniformItem> items = uniformCrawler.crawlUniformList();
		productService.saveCrawledProducts(items);
	}
}
