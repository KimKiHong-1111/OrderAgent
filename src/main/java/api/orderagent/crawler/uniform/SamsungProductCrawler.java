package api.orderagent.crawler.uniform;

import api.orderagent.crawler.dto.ProductRecord;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SamsungProductCrawler implements ProductCrawler {

	private static final String BASE_URL = "https://samsunglionsmall.com";
	private static final String LIST_URL = BASE_URL + "/product/list.html?cate_no=117";

	@Override
	public List<ProductRecord> crawl() {
		List<ProductRecord> products = new ArrayList<>();
		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1920,1080");

		WebDriver driver = new ChromeDriver();

		try {
			driver.get(LIST_URL);
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			while (true) {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.prdList_item")));

				List<WebElement> items = driver.findElements(By.cssSelector("div.prdList_item"));

				for (WebElement item : items) {
					try {
						WebElement linkElement = item.findElement(By.cssSelector("div.prdImg > a"));
						String detailUrl = linkElement.getDomAttribute("href");

						//상세 페이지 접근
						driver.navigate().to(Objects.requireNonNull(detailUrl));
						wait.until(
							ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.xans-product-detail")));

						String productName = driver.findElement(By.cssSelector("h3.title")).getText().trim();
						String imageUrl = driver.findElement(By.cssSelector("div.keyImg img")).getDomAttribute("src");
						String priceText = driver.findElement(By.cssSelector("span#span_product_price_text")).getText()
							.trim();
						int price = parsePrice(priceText);

						//옵션 추출
						List<WebElement> optionElements = driver.findElements(
							By.cssSelector("div.option_box select option"));
						for (WebElement option : optionElements) {
							String optionName = option.getText().trim();
							if (optionName.isBlank() || optionName.contains("옵션 선택")) {
								continue;
							}

							boolean soldOut = optionName.contains("품절");

							products.add(new ProductRecord(
								productName,
								optionName,
								price,
								imageUrl,
								detailUrl,
								!optionName.contains("품절"),
								LocalDateTime.now()));
						}
						driver.navigate().back();
						wait.until(
							ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("ul.prdList > li")));

					} catch (Exception e) {
						log.warn("상세 페이지 처리 중 오류 : {}", e.getMessage());
					}
				}
				try {
					WebElement nextBtn = driver.findElement(By.cssSelector("a.btn.next"));
					if (nextBtn.getDomAttribute("href") != null) {
						nextBtn.click();
						wait.until(ExpectedConditions.stalenessOf(items.get(0)));
					} else {
						break;
					}
				} catch (NoSuchElementException e) {
					break;
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
		if (rawPrice == null || rawPrice.isBlank()) {
			return 0;
		}
		return Integer.parseInt(rawPrice.replaceAll("[^0-9]", ""));
	}
}
