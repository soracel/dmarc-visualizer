package ch.bfh.api.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.OffsetDateTime;
import java.util.List;

@Document(indexName = "dmarc_aggregate-*")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DmarcReport {
    @JsonProperty("xml_schema")
    private String xmlSchema;

    @JsonProperty("org_name")
    private String orgName;

    @JsonProperty("org_email")
    private String orgEmail;

    @JsonProperty("report_id")
    private String reportId;

    @JsonProperty("date_range")
    private List<String> dateRange;

    @JsonProperty("date_begin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime dateBegin;

    @JsonProperty("date_end")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime dateEnd;

    @JsonProperty("published_policy")
    private PublishedPolicy publishedPolicy;

    @JsonProperty("source_ip_address")
    private String sourceIpAddress;

    @JsonProperty("source_country")
    private String sourceCountry;

    @JsonProperty("source_reverse_dns")
    private String sourceReverseDns;

    @JsonProperty("source_base_domain")
    private String sourceBaseDomain;

    @JsonProperty("message_count")
    private int messageCount;

    @JsonProperty("disposition")
    private String disposition;

    @JsonProperty("dkim_aligned")
    private boolean dkimAligned;

    @JsonProperty("spf_aligned")
    private boolean spfAligned;

    @JsonProperty("header_from")
    private String headerFrom;

    @JsonProperty("envelope_from")
    private String envelopeFrom;

    @JsonProperty("policy_overrides")
    private List<PolicyOverride> policyOverrides;

    @JsonProperty("dkim_results")
    private List<DkimResult> dkimResults;

    @JsonProperty("spf_results")
    private List<SpfResult> spfResults;

    @JsonProperty("passed_dmarc")
    private boolean passedDmarc;

    // Nested static classes for complex fields
    public static class PublishedPolicy {
        @JsonProperty("domain")
        private String domain;
        @JsonProperty("adkim")
        private String adkim;
        @JsonProperty("aspf")
        private String aspf;
        @JsonProperty("p")
        private String p;
        @JsonProperty("sp")
        private String sp;
        @JsonProperty("pct")
        private int pct;
        @JsonProperty("fo")
        private String fo;
    }

    public static class PolicyOverride {
        @JsonProperty("type")
        private String type;
        @JsonProperty("comment")
        private String comment;
    }

    public static class DkimResult {
        @JsonProperty("domain")
        private String domain;
        @JsonProperty("selector")
        private String selector;
        @JsonProperty("result")
        private String result;
    }

    public static class SpfResult {
        @JsonProperty("domain")
        private String domain;
        @JsonProperty("scope")
        private String scope;
        @JsonProperty("result")
        private String result;
    }
}
