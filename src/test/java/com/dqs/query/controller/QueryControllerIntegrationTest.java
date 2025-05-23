package com.dqs.query.controller;

import com.dqs.query.event.QueryReceived;
import com.dqs.query.model.Query;
import com.dqs.query.publisher.QueryReceivedPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EmbeddedKafka(partitions = 1, topics = "incoming_queries_jpmc")
@AutoConfigureMockMvc
class QueryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void submitQuery() throws Exception {
        Query query = new Query("Historical", "JPMC", 2001, 2004);
        String jsonResponse = mockMvc.perform(
                        post("/v1/query")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(query))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        KafkaTestUtils.getSingleRecord(consumer, "incoming_queries_jpmc");

        String queryId = JsonPath.parse(jsonResponse).read("$.id", String.class);
        assertNotNull(queryId);
    }

    @Test
    void submitQueryAndPublishQueryReceived() throws Exception {
        Query query = new Query("Historical", "JPMC", 2001, 2004);
        mockMvc.perform(
                        post("/v1/query")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(query))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ConsumerRecord<String, QueryReceived> record = KafkaTestUtils.getSingleRecord(consumer, "incoming_queries_jpmc");

        QueryReceived queryReceived = record.value();
        assertEquals(query.tenant(), queryReceived.tenant());
        assertEquals(query.term(), queryReceived.term());
        assertEquals(query.beginYear(), queryReceived.beginYear());
        assertEquals(query.endYear(), queryReceived.endYear());
    }
}