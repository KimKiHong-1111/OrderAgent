package api.orderagent.crawler.uniform;

import api.orderagent.crawler.dto.ProductRecord;
import java.util.List;

public interface ProductCrawler {
	List<ProductRecord> crawl();
}
