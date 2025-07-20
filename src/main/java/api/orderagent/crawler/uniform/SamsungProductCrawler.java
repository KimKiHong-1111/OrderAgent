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
import org.openqa.selenium.JavascriptExecutor;
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
			log.info("🔗 페이지 로딩 완료");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			while (true) {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.prdList__item")));
				log.info("✅ 상품 셀렉터 로딩 완료");

				List<WebElement> items = driver.findElements(By.cssSelector("div.prdList__item"));
				log.info("📦 추출된 상품 수: {}", items.size());

				// ✅ StaleElement 방지를 위해 URL만 먼저 따로 저장
				List<String> detailUrls = new ArrayList<>();
				for (WebElement item : items) {
					try {
						String href = item.findElement(By.cssSelector("div.prdImg > a")).getDomAttribute("href");
						if (href != null && !href.startsWith("http")) {
							href = BASE_URL + href;
						}
						String encodeUrl = java.net.URI.create(href).toASCIIString(); // 한글 인코딩 방지
						detailUrls.add(encodeUrl);
					} catch (Exception e) {
						log.warn("상세 링크 추출 실패 : {}", e.getMessage());
					}
				}

				// ✅ 상세 페이지 방문 및 정보 추출
				for (String detailUrl : detailUrls) {
					try {
						driver.navigate().to(Objects.requireNonNull(detailUrl));
						wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.xans-product-detail")));

						String productName = driver.findElement(By.cssSelector("h3.title")).getText().trim();

						String imageUrl = "";
						try {
							WebElement imageElement = wait.until(
								ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.thumbnail img.BigImage")));
							imageUrl = imageElement.getDomAttribute("src");
							//혹시 이미지 경로가 //로 시작하면 http: 붙여주기
							if (Objects.requireNonNull(imageUrl).startsWith("//")) {
								imageUrl = "https:" + imageUrl;
							}
						} catch (NoSuchElementException e) {
							log.warn("이미지 없음: {}", detailUrl);
						}

						int price = 0;
						try {
							WebElement metaPrice = driver.findElement(
								By.cssSelector("meta[property='product:price:amount']"));
							String priceText = Objects.requireNonNull(metaPrice.getDomAttribute("content")).trim();
							price = parsePrice(priceText);
						} catch (NoSuchElementException e) {
							log.warn("가격 정보 메타 태그 없음 : {}", detailUrl);
						}


						List<WebElement> optionElements = new ArrayList<>();
						try {
							WebElement selectElement = driver.findElement(By.name("option1"));
							optionElements = selectElement.findElements(By.tagName("option"));
							log.info("옵션 개수: {}", optionElements.size());
						} catch (NoSuchElementException e) {
							log.warn("옵션 셀렉트 박스 없음: {}", detailUrl);
						}
						for (WebElement option : optionElements) {
//							String optionText = option.getText().trim();
							JavascriptExecutor js = (JavascriptExecutor) driver;
							String optionText = (String) js.executeScript("return arguments[0].textContent;", option);
							optionText = optionText != null ? optionText.trim() : "";
							String optionValue = option.getDomAttribute("value");
							log.info("🧪 원본 option tag: {}, text='{}', value='{}'",
								option.getDomAttribute("outerHTML"),
								optionText,
								optionValue
							);

							if (optionValue == null || optionValue.contains("옵션을 선택") || optionValue.contains("---")) continue;

							boolean soldOut = optionText.toLowerCase().replaceAll("\\s", "").contains("[품절]");

							products.add(new ProductRecord(
								productName,
								optionValue,
								price,
								imageUrl,
								detailUrl,
								!soldOut,
								LocalDateTime.now()
							));

							log.info("🧵저장 대상: product={}, option={}, price={}, inStock={}",
								productName, optionValue, price, !soldOut);
							log.info("🔎 옵션 파싱 결과 - optionText='{}', soldOut={}, inStock={}",
								optionText, soldOut, !soldOut);
						}

						driver.navigate().back();
						wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.prdList__item")));

					} catch (Exception e) {
						log.warn("상세 페이지 처리 중 오류 ", e);
					}
				}

				// 다음 페이지 이동 처리
				try {
					List<WebElement> pageLinks = driver.findElements(By.cssSelector("ol li a"));
					String currentUrl = driver.getCurrentUrl();
					String nextUrl = null;

					for (WebElement pageLink : pageLinks) {
						String href = pageLink.getDomAttribute("href");
						String clazz = pageLink.getDomAttribute("class");

						if (clazz != null && !clazz.contains("this") && href != null) {
							nextUrl = BASE_URL + href;
							break;
						}
					}

					if (nextUrl != null && !nextUrl.equals(currentUrl)) {
						driver.get(nextUrl);
						wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.prdList__item")));
					} else {
						break; // 마지막 페이지
					}
				} catch (NoSuchElementException e) {
					break; // 페이지 네비게이션 없음
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
