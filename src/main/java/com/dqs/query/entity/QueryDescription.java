package com.dqs.query.entity;

import com.dqs.query.model.QueryStatus;
import com.dqs.query.model.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;

@Document(collection = "query_descriptions")
public class QueryDescription {

    @Id
    private String queryId;
    private String tenant;
    private String term;
    private int yearStart;
    private int yearEnd;
    private Status status;
    private LocalDateTime creationTime;
    private LocalDateTime completionTime;

    public QueryDescription() {
    }

    public QueryDescription(String queryId, String tenant, String term, int yearStart, int yearEnd, Status status, LocalDateTime creationTime) {
        this.queryId = queryId;
        this.tenant = tenant;
        this.term = term;
        this.yearStart = yearStart;
        this.yearEnd = yearEnd;
        this.status = status;
        this.creationTime = creationTime;
    }

    public Status status() {
        return status;
    }

    public QueryStatus toQueryStatus() {
        Duration completionDuration = null;
        if (status == Status.Completed) {
            completionDuration = Duration.between(this.creationTime, this.completionTime);
        }
        return new QueryStatus(this.queryId, this.term, this.status, completionDuration);
    }

    public void completeAt(LocalDateTime completionTime) {
        this.completionTime = completionTime;
        this.status = Status.Completed;
    }
}
