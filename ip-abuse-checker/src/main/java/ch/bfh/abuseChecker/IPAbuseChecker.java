package ch.bfh.abuseChecker;

import ch.bfh.abuseChecker.controller.IPReputationsController;
import org.apache.commons.cli.*;

import java.io.FileNotFoundException;

public class IPAbuseChecker {
    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("h", false, "prints this help information");
        options.addOption("elasticUrl",true,"URL where the elasticsearch cluster is reachable");
        options.addOption("abuseApiKey",true, "API key for the AbuseIPDB service");
        options.addOption("updateInterval", true, "Update interval or maximum age of the individual IP reputation entries");

        String elasticUrl;
        String abuseKey;
        int updateInterval = 86400;
        String updateIntervalString;
        String abuseUrl = "https://api.abuseipdb.com/api/v2/check";

        try{
            CommandLine cmd = parser.parse(options, args);
            if(cmd.hasOption("elasticUrl") && cmd.hasOption("abuseApiKey")){
                elasticUrl = cmd.getOptionValue("elasticUrl");
                abuseKey = cmd.getOptionValue("abuseApiKey");
                updateIntervalString = cmd.getOptionValue("updateInterval");
                if(updateIntervalString != null){
                    updateInterval = Integer.parseInt(updateIntervalString);
                }
            } else if(cmd.hasOption("h")){
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp(" ", options);
                return;
            }else{
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp(" ", options);
                return;
            }
        } catch(ParseException e){
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            return;
        }

        IPReputationsController ipAbuseController = new IPReputationsController(elasticUrl, abuseUrl, abuseKey, 86400);

    }

}
