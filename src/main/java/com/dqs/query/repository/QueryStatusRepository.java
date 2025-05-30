package com.dqs.query.repository;

import com.dqs.query.entity.QueryDescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryStatusRepository extends MongoRepository<QueryDescription, String> {
}
