package api.orderagent.lambda;

import api.orderagent.crawler.dto.ProductRecord;
import api.orderagent.crawler.uniform.UniformCrawler;
import api.orderagent.service.ProductService;
import java.util.List;
import java.util.function.Function;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;


public class OrderAgentHandler implements Function<String, String> {

	private static final ProductService productService;
	private static final UniformCrawler uniformCrawler;

	static {
		GenericApplicationContext context = new AnnotationConfigApplicationContext(OrderAgentHandler.class);
		productService = context.getBean(ProductService.class);
		uniformCrawler = context.getBean(UniformCrawler.class);
	}

	@Override
	public String apply(String input) {
		List<ProductRecord> records = uniformCrawler.crawl();
		productService.saveCrawledProducts(records);
		productService.saveOptionsForAllProducts();
		return "작업 완료";
	}
}
