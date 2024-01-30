package ch.bfh.abuseChecker.controller;

import ch.bfh.abuseChecker.model.AbuseIPResult;
import ch.bfh.abuseChecker.model.ElasticSearchResultSet;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IPReputationsController {
    private Logger logger;
    private HashSet<AbuseIPResult.IPAbuseData> localIPReputations;

    private HashSet<AbuseIPResult.IPAbuseData> updatedIPReputation;

    private Set<String> ipsFromReports;

    private ElasticSearchConnector elcon;

    private AbuseDBChecker abusecon;

    private String elasticIndex = "ip_reputation";
    private String elasticDoc = "ips";
    private String elasticIndexMappings = "{\"mappings\":{\"properties\":{\"ipAddress\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"abuseConfidenceScore\":{\"type\":\"integer\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"last_updated\":{\"type\":\"date\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}}}}";

    private long updateSeconds;

    public IPReputationsController(String elasticUrl, String url, String apiKey, long updateSeconds) {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.elcon = new ElasticSearchConnector(elasticUrl);
        this.abusecon = new AbuseDBChecker(url, apiKey);
        this.updatedIPReputation = new HashSet<>();
        this.localIPReputations = new HashSet<>();
        this.localIPReputations = new HashSet<>();

        this.updateSeconds = updateSeconds;

        try {

            if (!elcon.checkIndex(this.elasticIndex)) {
                elcon.createIndex(this.elasticIndex, this.elasticIndexMappings);
            }

            this.getIPsFromReports();
            this.getIPsFromReputationIndex();
            this.updateIPReputation();

            if (!this.updatedIPReputation.isEmpty() && this.updatedIPReputation != null) {
                this.pushToElasticSearch(this.updatedIPReputation);
            }

        } catch (Exception e) {
            this.logger.log(Level.SEVERE, e.getMessage());
        }

    }

    private void getIPsFromReputationIndex() {
        HashSet<AbuseIPResult.IPAbuseData> ipReputationsFromIndex = new HashSet<>();

        try {
            ipReputationsFromIndex = elcon.getIPReputations(this.elasticIndex, this.elasticDoc);
        } catch (Exception e) {
            this.logger.log(Level.SEVERE, e.getMessage());
        }

        this.localIPReputations.addAll(ipReputationsFromIndex);

    }

    private void getIPsFromReports() throws IOException {
        List<ElasticSearchResultSet.Hits.Hit> reportEntries = this.elcon.getReportEntries("dmarc_aggregate-*");
        if (this.ipsFromReports == null) {
            this.ipsFromReports = new HashSet<>();
        }

        HashSet<String> newIps = new HashSet<>();
        for (ElasticSearchResultSet.Hits.Hit entry : reportEntries) {
            String ip = entry.getReport().getSourceIpAddress();
            newIps.add(ip);
        }

        for(String ip : newIps){
            if (ip != null) {
                AbuseIPResult.IPAbuseData newIp = new AbuseIPResult.IPAbuseData();
                newIp.setIpAddress(ip);
                this.localIPReputations.add(newIp);
            }
        }
    }

    private void pushToElasticSearch(HashSet<AbuseIPResult.IPAbuseData> results) {
        try {
            for (AbuseIPResult.IPAbuseData entry : results) {
                elcon.writeIPReputations(entry, elasticIndex);
            }

        } catch (Exception e) {
            this.logger.log(Level.SEVERE, e.getMessage());
        }
    }

    private void updateIPReputation() {
        long nowSecs = new Date().getTime() / 1000;
        HashSet<String> alreadyUpdatedIps = new HashSet<>();


        for (AbuseIPResult.IPAbuseData ip : this.localIPReputations) {
            long last_updated;
            if (ip.getLast_updated() != null) {
                last_updated = ip.getLast_updated().getTime() / 1000;
            } else {
                last_updated = 0;
            }
            if (!alreadyUpdatedIps.contains(ip.getIpAddress())) {

                if ((nowSecs - last_updated) > this.updateSeconds) {
                    try {

                        if (abusecon.getIPInformation(ip) != null) {
                            ip.setLastUpdatedNow();
                            this.updatedIPReputation.add(ip);
                            alreadyUpdatedIps.add(ip.getIpAddress());

                        }

                    } catch (Exception e) {
                        this.logger.log(Level.SEVERE, e.getMessage());
                    }
                }

            }else{
                this.logger.log(Level.INFO, String.format("IP: %s already checked.", ip.getIpAddress()));

            }
        }

    }

}