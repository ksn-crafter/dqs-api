package com.dqs.query.publisher;

import com.dqs.query.event.QueryReceived;
import com.dqs.query.model.Query;
import com.dqs.query.model.QueryId;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EmbeddedKafka(partitions = 1, topics = "incoming_queries_jpmc")
class QueryReceivedPublisherIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private QueryReceivedPublisher publisher;

    private Consumer<String, QueryReceived> consumer;

    @BeforeAll
    void setupConsumer() {
        Map<String, Object> props = new HashMap<>(KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        DefaultKafkaConsumerFactory<String, QueryReceived> consumerFactory =
                new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(QueryReceived.class, false));

        consumer = consumerFactory.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "incoming_queries_jpmc");
    }

    @AfterAll
    void tearDown() {
        consumer.close();
    }

    @Test
    void publishQueryReceived() {
        UUID id = UUID.randomUUID();
        QueryReceived event = new QueryReceived(new QueryId(id), new Query("Historical", "JPMC", 2001, 2002));

        publisher.publish(event);

        ConsumerRecord<String, QueryReceived> record = KafkaTestUtils.getSingleRecord(consumer, "incoming_queries_jpmc");

        assertEquals(id.toString(), record.key());

        QueryReceived queryReceived = record.value();
        assertEquals(event.tenant(), queryReceived.tenant());
        assertEquals(event.term(), queryReceived.term());
        assertEquals(event.beginYear(), queryReceived.beginYear());
        assertEquals(event.endYear(), queryReceived.endYear());
        assertEquals(event.creationTime(), queryReceived.creationTime());
    }
}
