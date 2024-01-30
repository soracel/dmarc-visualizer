package ch.bfh.api.services;

import ch.bfh.api.entities.DmarcReport;
import ch.bfh.api.entities.ElasticsearchSearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DmarcReportService {
    private final RestClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public DmarcReportService(RestClient client) {
        this.client = client;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public List<DmarcReport> getAllReports() throws IOException {
        Request request = new Request("GET", "/dmarc_aggregate-*/_search");
        Response response = client.performRequest(request);

        String jsonResponse = EntityUtils.toString(response.getEntity());
        ElasticsearchSearchResponse searchResponse = objectMapper.readValue(jsonResponse, ElasticsearchSearchResponse.class);

        List<DmarcReport> reports = new ArrayList<>();
        for (ElasticsearchSearchResponse.Hits.Hit hit : searchResponse.getHits().getHits()) {
            reports.add(hit.getSource());
        }

        return reports;
    }
}