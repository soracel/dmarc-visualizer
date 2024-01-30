package ch.bfh.api.configurations;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchClientConfig {
    @Value("${elasticsearch.scheme}")
    private String scheme;
    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.port}")
    private int port;

    @Bean
    public RestClient restClient() {
        return RestClient.builder(new HttpHost(host, port, scheme)).build();
    }
}
