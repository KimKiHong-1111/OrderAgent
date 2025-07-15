package api.orderagent.crawler.option;

import api.orderagent.domain.entity.OptionStock;
import api.orderagent.domain.entity.Product;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
public class SamsungOptionCrawler implements OptionCrawler {

	private final WebDriver driver;

	public SamsungOptionCrawler() {
		WebDriverManager.chromedriver().setup();
		ChromeOptions opts = new ChromeOptions();
		opts.addArguments("--headless", "--disable-gpu");
		opts.addArguments("--window-size=1920,1080");
		this.driver = new ChromeDriver(opts);
	}

	@Override
	public List<OptionStock> crawlOptions(Product product) {
		List<OptionStock> optionStocks = new ArrayList<>();
		driver.get(product.getProductUrl());

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		// 스크롤 시도
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}// JS 렌더링 유도

		try {
			List<WebElement> listOptions = driver.findElements(By.cssSelector("ul.optionList > li"));
			if (!listOptions.isEmpty()) {
				log.info("[{}] ul.optionList 구조 감지", product.getName());
				for (WebElement option : listOptions) {
					String text = option.getText().trim();
					if (text.isBlank() || text.contains("선택") || text.contains("필수")) {
						continue;
					}

					boolean soldOut = text.contains("품절");

					optionStocks.add(OptionStock.builder()
						.optionName(text)
						.inStock(!soldOut)
						.checkedAt(LocalDateTime.now())
						.product(product)
						.build());
				}
			} else {
				List<WebElement> selectOptions = driver.findElements(By.cssSelector("select[name^='option'] > option"));

				if (selectOptions.size() <= 1) {
					wait.until(d -> d.findElements(By.cssSelector("select[name^='option'] > option")).size() > 1);
					selectOptions = driver.findElements(By.cssSelector("select[name^='option'] > option"));
				}

				log.info("[{}] select > option 구조 감지", product.getName());

				for (WebElement option : selectOptions) {
					String text = option.getText().trim();
					if (text.isBlank() || text.contains("선택") || text.contains("필수") || text.contains("---")) {
						continue;
					}

					boolean soldOut = text.contains("품절");

					optionStocks.add(OptionStock.builder()
						.optionName(text)
						.inStock(!soldOut)
						.checkedAt(LocalDateTime.now())
						.product(product)
						.build());
				}
			}

			log.info("[{}] 옵션 {}개 크롤링 완료", product.getName(), optionStocks.size());

		} catch (TimeoutException e) {
			log.warn("옵션 로딩 타임아웃: {} / {}", product.getName(), product.getProductUrl());
		} catch (Exception e) {
			log.warn("옵션 크롤링 실패: {} / {} / {}", product.getName(), product.getProductUrl(), e.getMessage());
		}

		return optionStocks;
	}

	@PreDestroy
	public void cleanUp() {
		driver.quit();
		log.info("SamsungCrawler WebDriver 종료 완료");
	}
}
