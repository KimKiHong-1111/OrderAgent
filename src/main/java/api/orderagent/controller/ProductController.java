package api.orderagent.controller;

import api.orderagent.domain.entity.Product;
import api.orderagent.dto.ProductResDto;
import api.orderagent.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

	// 개별 조회 (PK + SK)
	@GetMapping("/detail")
	public Product getProduct(@RequestParam String pk, @RequestParam String sk) {
		return productService.find(pk, sk);
	}
}
