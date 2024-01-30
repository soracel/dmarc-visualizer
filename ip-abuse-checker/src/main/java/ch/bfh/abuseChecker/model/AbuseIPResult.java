package ch.bfh.abuseChecker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AbuseIPResult {
    @JsonProperty("data")
    private IPAbuseData ipAbuseData;


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IPAbuseData {
        @JsonProperty("ipAddress")
        private String ipAddress;

        @JsonProperty("abuseConfidenceScore")
        private int abuseConfidenceScore;

        @JsonProperty("last_updated")
        private Date last_updated;


        @Override
        public String toString(){
            return String.format("IP: %s, AbuseConfidence: %d", this.ipAddress, this.abuseConfidenceScore);
        }

        public void setLastUpdatedNow(){
            this.last_updated = new Date();
        }

        public void setAbuseConfidenceScore(int abuseConfidenceScore){
            this.abuseConfidenceScore = abuseConfidenceScore;
        }

        public int getAbuseConfidenceScore(){
            return this.abuseConfidenceScore;
        }

        public Date getLast_updated(){
            return this.last_updated;
        }

        public String getIpAddress(){
            return this.ipAddress;
        }

        public void setIpAddress(String ip){
            this.ipAddress = ip;
        }


    }


    public IPAbuseData getData() {
        return ipAbuseData;
    }






}
