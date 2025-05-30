package com.dqs.query.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.util.function.Function;

public record QueryStatus(String queryId, String term, Status status, @JsonIgnore Duration completionDuration) {

    @JsonProperty
    public Long completionDurationInSeconds() {
        return safeMapCompletionDuration(Duration::toSeconds);
    }

    @JsonProperty
    public Long completionDurationInMilliSeconds() {
        return safeMapCompletionDuration(Duration::toMillis);
    }

    private Long safeMapCompletionDuration(Function<Duration, Long> mapper) {
        if (completionDuration != null) {
            return mapper.apply(completionDuration);
        }
        return null;
    }
}

