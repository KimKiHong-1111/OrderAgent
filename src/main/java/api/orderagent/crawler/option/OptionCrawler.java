package api.orderagent.crawler.option;

import api.orderagent.domain.entity.OptionStock;
import api.orderagent.domain.entity.Product;
import java.util.List;

public interface OptionCrawler {
	List<OptionStock> crawlOptions(Product product);
}
