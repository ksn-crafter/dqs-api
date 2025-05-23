package com.dqs.query.event;

import com.dqs.query.model.Query;
import com.dqs.query.model.QueryId;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class QueryReceived {

    @JsonProperty
    private String queryId;
    @JsonProperty
    private String term;
    @JsonProperty
    private String tenant;
    @JsonProperty
    private int beginYear;
    @JsonProperty
    private int endYear;
    @JsonProperty
    private LocalDateTime creationTime;

    //needed for serialization and deserialization
    public QueryReceived() {
    }

    public QueryReceived(QueryId queryId, Query query) {
        this.queryId = queryId.id().toString();
        this.term = query.term();
        this.tenant = query.tenant();
        this.beginYear = query.beginYear();
        this.endYear = query.endYear();
        this.creationTime = LocalDateTime.now();
    }

    public String tenant() {
        return this.tenant;
    }

    public String queryId() {
        return this.queryId;
    }

    public String term() {
        return term;
    }

    public int beginYear() {
        return beginYear;
    }

    public int endYear() {
        return endYear;
    }

    public LocalDateTime creationTime() {
        return creationTime;
    }
}
