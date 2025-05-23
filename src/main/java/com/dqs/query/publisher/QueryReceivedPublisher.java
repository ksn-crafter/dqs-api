package com.dqs.query.publisher;

import com.dqs.query.event.QueryReceived;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueryReceivedPublisher {

    private final KafkaTemplate<String, QueryReceived> kafkaTemplate;

    private static final String topicPrefix = "incoming_queries_";

    public QueryReceivedPublisher(KafkaTemplate<String, QueryReceived> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(QueryReceived event) {
        kafkaTemplate.send(topicNameFor(event.tenant()),
                event.queryId(),
                event);
    }

    static String topicNameFor(String tenant) {
        return topicPrefix + tenant.toLowerCase();
    }
}
