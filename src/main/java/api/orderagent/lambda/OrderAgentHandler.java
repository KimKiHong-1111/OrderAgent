package api.orderagent.lambda;

import api.orderagent.crawler.dto.ProductRecord;
import api.orderagent.crawler.uniform.ProductCrawler;
import api.orderagent.service.ProductService;
import java.util.List;
import java.util.function.Function;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;


public class OrderAgentHandler implements Function<String, String> {

	private static final ProductService productService;
	private static final ProductCrawler PRODUCT_CRAWLER;

	static {
		GenericApplicationContext context = new AnnotationConfigApplicationContext(OrderAgentHandler.class);
		productService = context.getBean(ProductService.class);
		PRODUCT_CRAWLER = context.getBean(ProductCrawler.class);
	}

	@Override
	public String apply(String input) {
		List<ProductRecord> records = PRODUCT_CRAWLER.crawl();
//		productService.saveCrawledProducts(records);
//		productService.saveOptionsForAllProducts();
		return "작업 완료";
	}
}
