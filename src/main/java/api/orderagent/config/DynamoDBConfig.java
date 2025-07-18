package api.orderagent.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	public AmazonDynamoDB amazonDynamoDB() {
		log.info("🔌 DynamoDB 연결 시도: endpoint={}, region={}, accessKey={}", dynamoEndpoint, region, accessKey);
		return AmazonDynamoDBClientBuilder.standard()
			.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dynamoEndpoint, region))
			.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
			.build();
	}

	@Bean
	public DynamoDBMapper dynamoDBMapper() {
		return new DynamoDBMapper(amazonDynamoDB());
	}
}
