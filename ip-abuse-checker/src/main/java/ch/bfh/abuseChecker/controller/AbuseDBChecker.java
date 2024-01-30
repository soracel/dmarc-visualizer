package ch.bfh.abuseChecker.controller;

import ch.bfh.abuseChecker.model.AbuseIPResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AbuseDBChecker {
    private final String host;
    private final String apiKey;
    private AbuseIPResult.IPAbuseData abuseData;
    private URL url;
    private HttpsURLConnection con;
    private Logger logger;

    public AbuseDBChecker(String host, String apiKey){
        this.host = host;
        this.apiKey = apiKey;
        logger = Logger.getLogger(this.getClass().getName());

    }

    public AbuseIPResult.IPAbuseData getIPInformation(AbuseIPResult.IPAbuseData abuseData) throws IOException {
        ObjectMapper om = new ObjectMapper();
        this.abuseData = abuseData;
        this.url = new URL(this.host + "?ipAddress=" + this.abuseData.getIpAddress());
        this.logger.log(Level.INFO,String.format("Querying URL %s.",this.url));
        con = (HttpsURLConnection) this.url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Key", apiKey);
        con.connect();

        int response = con.getResponseCode();
        switch(response){
            case 200:
                String resultJson = new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                AbuseIPResult result = om.readValue(resultJson, AbuseIPResult.class);
                abuseData.setAbuseConfidenceScore(result.getData().getAbuseConfidenceScore());
                this.logger.log(Level.INFO, String.format("Got abuse data for %s", abuseData.getIpAddress()));
                abuseData.setLastUpdatedNow();

                return result.getData();
            case 404:
                this.logger.log(Level.INFO,String.format("No entry for %s found.",abuseData.getIpAddress()));
                break;

            case 401:
                String serverMessage = new String(con.getInputStream().readAllBytes(),StandardCharsets.UTF_8);
                this.logger.log(Level.SEVERE, String.format("Return Code %d: %s,\n Error Message: %s", response, con.getResponseMessage(), serverMessage));

                break;
            case 405:
                System.out.println(response);
                this.logger.log(Level.SEVERE, String.format("Query for address %s failed. Message from api: %s: %s", abuseData.getIpAddress(), con.getResponseCode(), new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8)));
                break;
            default:
        }

        return null;
    }
}
