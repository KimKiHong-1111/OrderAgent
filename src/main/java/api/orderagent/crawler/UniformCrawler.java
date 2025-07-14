package api.orderagent.crawler;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UniformCrawler {

	private static final String UNIFORM_LIST_URL = "https://samsunglionsmall.com/product/list.html?cate_no=117";

	public List<UniformItem> crawlUniformList() {
		List<UniformItem> items = new ArrayList<>();

		try {
			Document doc = Jsoup.connect(UNIFORM_LIST_URL).get();

			Elements productElements = doc.select("ul.prdList li");

			for (Element el : productElements) {
				String name = el.select(".name").text();
				String href = el.select("a[href]").attr("href");
				String productUrl = "https://samsunglionsmall.com" + href;

				String imgUrl = el.select("img.thumb").attr("src");
				String priceStr = el.select(".price span:nth-child(1)").text().replaceAll("[^0-9]", "");
				int price = priceStr.isEmpty() ? 0 : Integer.parseInt(priceStr);

				items.add(new UniformItem(name, productUrl, imgUrl, price));
			}
		} catch (Exception e) {
			log.error("크롤링 실패: {}", e.getMessage());
		}

		return items;
	}

	//크롤러 결과를 임시로 담는 용도로 클래스 내부에 작성.
	public record UniformItem(
		String name,
		String productUrl,
		String imageUrl,
		int price
	) {}
}
