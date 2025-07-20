package api.orderagent.controller;

import api.orderagent.crawler.dto.ProductRecord;
import api.orderagent.domain.entity.Product;
import api.orderagent.dto.ProductResDto;
import api.orderagent.service.ProductService;
import api.orderagent.service.ProductService.SaveResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
		SaveResult result = productService.saveCrawledProducts(crawled);
		log.info("✅ 수동 크롤링 완료된 상품 수: {}", crawled.size());
		log.info("🛒 자동 주문 시도된 품절 상품 수: {}", result.autoOrderCount());
		if (!result.soldOutRecords().isEmpty()) {
			log.info("품절 상품 목록:");
			for (ProductRecord soldOut : result.soldOutRecords()) {
				log.info("⛔ {} [{}]", soldOut.productName(), soldOut.optionName());
			}
		}
		return "크롤링 완료: " + crawled.size() + "개";
	}
}
