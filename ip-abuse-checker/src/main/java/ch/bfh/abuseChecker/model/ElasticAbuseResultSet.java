package ch.bfh.abuseChecker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticAbuseResultSet {
    @JsonProperty("hits")
    private ElasticAbuseResultSet.Hits hits;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hits {
        @JsonProperty("hits")
        private HashSet<ElasticAbuseResultSet.Hits.Hit> entries;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Hit {

            @JsonProperty("_source")
            private AbuseIPResult.IPAbuseData entry;

            public AbuseIPResult.IPAbuseData getAbuseData(){
                return this. entry;
            }

        }
        public HashSet<Hit> getAbuseResults() {
            return this.entries;
        }
    }

    public ElasticAbuseResultSet.Hits getHits(){
        return this.hits;
    }


}
