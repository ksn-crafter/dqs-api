package com.dqs.query.controller;

import com.dqs.query.event.QueryReceived;
import com.dqs.query.model.Query;
import com.dqs.query.model.QueryId;
import com.dqs.query.publisher.QueryReceivedPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class QueryController {

    private final QueryIdGenerator idGenerator;

    private final QueryReceivedPublisher queryReceivedPublisher;

    public QueryController(QueryIdGenerator idGenerator, QueryReceivedPublisher queryReceivedPublisher) {
        this.idGenerator = idGenerator;
        this.queryReceivedPublisher = queryReceivedPublisher;
    }

    @PostMapping("/query")
    public QueryId submit(@RequestBody Query query) {
        QueryId queryId = new QueryId(this.idGenerator.generate());
        System.out.println("Generated queryId " + queryId.id().toString() + " for the term " + query.term() + " for the tenant " + query.tenant());

        this.queryReceivedPublisher.publish(new QueryReceived(queryId, query));
        return queryId;
    }
}
