package api.orderagent.controller;

import api.orderagent.dto.ProductResDto;
import api.orderagent.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping
	public List<ProductResDto> getProducts() {
		return productService.getAllProducts()
			.stream()
			.map(ProductResDto::toDto)
			.toList();
	}

}
