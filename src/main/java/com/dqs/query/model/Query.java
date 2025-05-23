package com.dqs.query.model;

//TODO: begin and end year may change into a range: InclusiveYearRange or ExclusiveYearRange. It is ok for now.
public record Query(String term, String tenant, int beginYear, int endYear) {
}