package com.dqs.query.service;

import com.dqs.query.entity.QueryDescription;
import com.dqs.query.model.QueryStatus;
import com.dqs.query.repository.QueryStatusRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QueryStatusService {

    private final QueryStatusRepository queryStatusRepository;

    public QueryStatusService(QueryStatusRepository queryStatusRepository) {
        this.queryStatusRepository = queryStatusRepository;
    }

    public Optional<QueryStatus> statusOf(String queryId) {
        Optional<QueryDescription> optionalQueryDescription = this.queryStatusRepository.findById(queryId);
        System.out.println("Query found by queryId? " + optionalQueryDescription.isPresent());

        return optionalQueryDescription.map(QueryDescription::toQueryStatus);
    }
}
