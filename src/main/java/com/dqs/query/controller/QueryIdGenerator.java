package com.dqs.query.controller;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class QueryIdGenerator {
    public UUID generate() {
        return UUID.randomUUID();
    }
}