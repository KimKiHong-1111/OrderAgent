package api.orderagent.crawler.dto;

public record ProductRecord(
	String name,
	int price,
	String imageUrl,
	String productUrl,
	boolean soldOut)
{}
