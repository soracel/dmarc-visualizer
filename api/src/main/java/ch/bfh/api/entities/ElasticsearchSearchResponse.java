package ch.bfh.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticsearchSearchResponse {
    private Hits hits;

    public Hits getHits() {
        return hits;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hits {
        private List<Hit> hits;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Hit {
            @JsonProperty("_source")
            private DmarcReport source;

            public DmarcReport getSource() {
                return source;
            }

            // Getters and setters
        }

        public List<Hit> getHits() {
            return hits;
        }
    }
}