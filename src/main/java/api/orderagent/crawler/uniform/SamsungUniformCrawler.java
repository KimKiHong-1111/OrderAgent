package api.orderagent.crawler.uniform;

import api.orderagent.crawler.dto.ProductRecord;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SamsungUniformCrawler implements UniformCrawler{

	private static final String UNIFORM_LIST_URL = "https://samsunglionsmall.com/product/list.html?cate_no=117";

	@Override
	public List<ProductRecord> crawl() {
		List<ProductRecord> products = new ArrayList<>();

		// Selenium 드라이버 설정
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		try {
			driver.get(UNIFORM_LIST_URL);
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul.prdList > li")));

			List<WebElement> items = driver.findElements(By.cssSelector("ul.prdList > li"));

			for (WebElement item : items) {
				try {
					String name = item.findElement(By.cssSelector(".name a")).getText().trim();
					boolean soldOut = name.contains("[품절]") || name.contains("품절");
					//가격이 없는경우
					String priceText;
					try {
						WebElement discountPrice = item.findElement(By.cssSelector("strong#span_product_price_text"));
						priceText = discountPrice.getText().trim();
					} catch (Exception e) {
						priceText = item.findElement(By.cssSelector("li.product_price")).getText().trim();
					}

					int price = parsePrice(priceText);

					String imageUrl = item.findElement(By.cssSelector(".thumbnail img")).getAttribute("src");

					String productUrl = item.findElement(By.cssSelector(".name a")).getAttribute("href");
					if (!productUrl.startsWith("http")) {
						productUrl = "https://samsunglionsmall.com" + productUrl;
					}

					products.add(new ProductRecord(name, price, imageUrl, productUrl, soldOut));
				} catch (Exception e) {
					log.warn("상품 정보 파싱 중 오류 발생: {} /  내용: {} ", e.getMessage(), item.getText());
				}
			}
		} catch (Exception e) {
			log.error("크롤링 실패: {}", e.getMessage());
		} finally {
			driver.quit();
		}
		return products;
	}

	private int parsePrice(String rawPrice) {
		if (rawPrice == null || rawPrice.isBlank()) return 0;
		return Integer.parseInt(rawPrice.replaceAll("[^0-9]", ""));
	}
}
