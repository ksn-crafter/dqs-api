package com.dqs.query.controller;

import com.dqs.query.model.QueryStatus;
import com.dqs.query.service.QueryStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1")
public class QueryStatusController {

    private final QueryStatusService queryStatusService;

    public QueryStatusController(QueryStatusService queryStatusService) {
        this.queryStatusService = queryStatusService;
    }

    @GetMapping("/queries/{id}/status")
    public QueryStatus get(@PathVariable("id") String queryId) {
        System.out.println("Finding the status of queryId " + queryId);
        return this.queryStatusService
                .statusFor(queryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QueryId not found"));
    }
}
