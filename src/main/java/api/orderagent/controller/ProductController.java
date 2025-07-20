package api.orderagent.controller;

import api.orderagent.crawler.dto.ProductRecord;
import api.orderagent.domain.entity.Product;
import api.orderagent.dto.ProductResDto;
import api.orderagent.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	// 전체 조회
	@GetMapping
	public List<Product> getAllProducts() {
		return productService.findAll();
	}

	@PostMapping("/crawl")
	public String manualCrawl() {
		List<ProductRecord> crawled = productService.getProductCrawler().crawl();
		productService.saveCrawledProducts(crawled);
		return "크롤링 완료: " + crawled.size() + "개";
	}
}
