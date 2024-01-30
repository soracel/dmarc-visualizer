package ch.bfh.abuseChecker.controller;

import ch.bfh.abuseChecker.model.AbuseIPResult;
import ch.bfh.abuseChecker.model.ElasticAbuseResultSet;
import ch.bfh.abuseChecker.model.ElasticSearchResultSet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ElasticSearchConnector {

    Logger logger;
    private ObjectMapper objectMapper;
    private URL url;
    private String host;
    private String searchApi = "_search";
    private String updateApi = "_update";
    private String ipDoc = "ips";
    private String docApi = "_doc";
    private HttpURLConnection con;

    public ElasticSearchConnector(String host) {
        this.host = host;
        this.objectMapper = new ObjectMapper();
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.logger = Logger.getLogger(this.getClass().getName());

    }

    public List<ElasticSearchResultSet.Hits.Hit> getReportEntries(String index) throws IOException {

        this.url = new URL(this.host + "/" + index + "/" + searchApi);
        con = (HttpURLConnection) this.url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        con.connect();

        if (con.getResponseCode() == 200) {

            String json = new String(con.getInputStream().readAllBytes(), UTF_8);
            ElasticSearchResultSet resultSet = objectMapper.readValue(json, ElasticSearchResultSet.class);
            return resultSet.getHits().getEntries();
        }

        return null;

    }

    public boolean writeIPReputations(AbuseIPResult.IPAbuseData lookupResult, String index) throws JsonProcessingException, IOException {
        String json = this.objectMapper.writeValueAsString(lookupResult);
        URL url = new URL(this.host + "/" + index + "/" + this.updateApi + "/" + lookupResult.getIpAddress());
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.connect();

        OutputStream outputStream = con.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(outputStream, UTF_8);

        String postBody = "{\"doc\":" + json + ", \"doc_as_upsert\":true}";
        osw.write(postBody);

        osw.flush();
        osw.close();

        outputStream.flush();
        outputStream.close();

        if (con.getResponseCode() >= 200 && con.getResponseCode() < 300) {
            return true;
        } else {
            logger.log(Level.SEVERE, String.format("Request failed with response %d: %s",con.getResponseCode(), con.getResponseMessage()));
            return false;
        }

    }

    public boolean checkIndex(String index) throws IOException {
        URL url = new URL(this.host + "/" + index);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.connect();

        if (con.getResponseCode() == 200) {
            return true;
        } else {
            return false;
        }
    }

    public boolean createIndex(String index, String mappings) throws IOException {
        URL url = new URL(this.host + "/" + index);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.connect();
        OutputStream os = con.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, UTF_8);
        osw.write(mappings);
        osw.flush();
        osw.close();
        os.close();
        os.flush();

        if (con.getResponseCode() == 200) {
            return true;
        } else {
            System.out.println(con.getResponseCode() + " " + con.getResponseMessage());
            return false;
        }
    }


    public HashSet<AbuseIPResult.IPAbuseData> getIPReputations(String index, String doc) throws IOException {
        ObjectMapper om = new ObjectMapper();
        URL url = new URL(this.host + "/" + index + "/" + searchApi);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.connect();

        int response = con.getResponseCode();

        switch (response) {
            case 200:
                HashSet<AbuseIPResult.IPAbuseData> ipAbuseResults = new HashSet<>();

                String resultJson = new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                ElasticAbuseResultSet result = om.readValue(resultJson, ElasticAbuseResultSet.class);
                for (ElasticAbuseResultSet.Hits.Hit entry : result.getHits().getAbuseResults()) {
                    ipAbuseResults.add(entry.getAbuseData());
                }

                return ipAbuseResults;
            case 404:
                //System.out.println("no entry found");
                break;
            default:
                //System.out.println(response);
        }
        return null;
    }

}
