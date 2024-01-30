package ch.bfh.abuseChecker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticSearchIPResultset {
    @JsonProperty("hits")
    private Hits hits;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hits {
        @JsonProperty("hits")
        private List<Hit> entries;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Hit {
            @JsonProperty("_source")
            private DmarcReport source;

            public DmarcReport getReport(){
                return this.source;
            }
        }

        public List<Hit> getEntries(){
            return this.entries;
        }

    }

    public Hits getHits(){
        return this.hits;
    }

}
