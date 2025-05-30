package com.dqs.query.controller;

import com.dqs.query.entity.QueryDescription;
import com.dqs.query.model.Status;
import com.dqs.query.repository.QueryStatusRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class QueryStatusControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QueryStatusRepository queryStatusRepository;

    private static final MongodExecutable mongodExecutable;

    private static final int mongoPort;

    static {
        try {
            mongoPort = Network.getFreeServerPort();
            MongodConfig config = MongodConfig.builder()
                    .version(Version.Main.V6_0)
                    .net(new Net(mongoPort, Network.localhostIsIPv6()))
                    .build();
            mongodExecutable = MongodStarter.getDefaultInstance().prepare(config);
            mongodExecutable.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void setMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:" + mongoPort + "/dqs");
    }

    @AfterAll
    public void stopEmbeddedMongo() {
        mongodExecutable.stop();
    }

    @Test
    void getQueryStatusOfACompletedQuery() throws Exception {
        queryStatusRepository.deleteById("query-1");

        LocalDateTime creationTime = LocalDateTime.now();
        QueryDescription queryDescription = new QueryDescription("query-1", "JPMC", "Historical", 2001, 2005, Status.InProgress, creationTime);
        queryDescription.completeAt(creationTime.plusSeconds(10));
        queryStatusRepository.save(queryDescription);

        mockMvc.perform(get("/v1/queries/query-1/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(Status.Completed.toString()))
                .andExpect(jsonPath("$.completionDurationInSeconds").value(10))
                .andExpect(jsonPath("$.queryId").value("query-1"))
                .andExpect(jsonPath("$.term").value("Historical"));
    }

    @Test
    void getQueryStatusOfAnInProgressQuery() throws Exception {
        queryStatusRepository.deleteById("query-2");

        LocalDateTime creationTime = LocalDateTime.now();
        QueryDescription queryDescription = new QueryDescription("query-2", "JPMC", "Historical", 2001, 2005, Status.InProgress, creationTime);
        queryStatusRepository.save(queryDescription);

        mockMvc.perform(get("/v1/queries/query-2/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(Status.InProgress.toString()))
                .andExpect(jsonPath("$.completionDurationInSeconds").doesNotExist())
                .andExpect(jsonPath("$.queryId").value("query-2"))
                .andExpect(jsonPath("$.term").value("Historical"));
    }

    @Test
    void getQueryStatusOfANonExistingQuery() throws Exception {
        mockMvc.perform(get("/v1/queries/query-unknown/status"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}