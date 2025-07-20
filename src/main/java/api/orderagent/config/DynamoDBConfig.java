package api.orderagent.config;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Slf4j
@Configuration
public class DynamoDBConfig {

	@Value("${aws.dynamodb.endpoint}")
	private String dynamoEndpoint;

	@Value("${aws.dynamodb.region}")
	private String region;

	@Value("${aws.dynamodb.access-key}")
	private String accessKey;

	@Value("${aws.dynamodb.secret-key}")
	private String secretKey;

	@Bean
	public DynamoDbClient dynamoDbClient() {
		log.info("🔌 DynamoDB 연결 시도: endpoint={}, region={}, accessKey={}", dynamoEndpoint, region, accessKey);
		return DynamoDbClient.builder()
			.endpointOverride(URI.create(dynamoEndpoint))
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey,secretKey)))
			.build();
	}

	@Bean
	public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
		return DynamoDbEnhancedClient.builder()
			.dynamoDbClient(dynamoDbClient)
			.build();
	}
}
