package api.orderagent.lambda;

import api.orderagent.crawler.dto.ProductRecord;
import api.orderagent.crawler.uniform.UniformCrawler;
import api.orderagent.service.ProductService;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderAgentHandler implements Function<String, String> {

	@Autowired
	private ProductService productService;
	@Autowired
	private UniformCrawler uniformCrawler;

	@Override
	public String apply(String input) {
		List<ProductRecord> records = uniformCrawler.crawl();
		productService.saveCrawledProducts(records);
		productService.saveOptionsForAllProducts();
		return "작업 완료";
	}
}
