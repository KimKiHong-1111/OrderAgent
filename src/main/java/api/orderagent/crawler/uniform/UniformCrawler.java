package api.orderagent.crawler.uniform;

import api.orderagent.crawler.dto.ProductRecord;
import java.util.List;

public interface UniformCrawler {
	List<ProductRecord> crawl();
}
