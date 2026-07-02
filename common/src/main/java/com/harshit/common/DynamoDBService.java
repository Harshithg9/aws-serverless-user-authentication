package com.harshit.common;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDBService {

    private final DynamoDbClient dynamoDbClient;

    public DynamoDBService() {
        this.dynamoDbClient = DynamoDbClient.create();
    }

    public void saveUser(User user) {

        Map<String, AttributeValue> item = new HashMap<>();

        item.put("username", AttributeValue.builder()
                .s(user.getUsername())
                .build());

        item.put("password", AttributeValue.builder()
                .s(user.getPassword())
                .build());

        item.put("email", AttributeValue.builder()
                .s(user.getEmail())
                .build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("Users")
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }
    public User getUser(String username) {

        Map<String, AttributeValue> key = new HashMap<>();

        key.put("username",
                AttributeValue.builder()
                        .s(username)
                        .build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName("Users")
                .key(key)
                .build();

        GetItemResponse response = dynamoDbClient.getItem(request);

        if (!response.hasItem()) {
            return null;
        }

        Map<String, AttributeValue> item = response.item();

        User user = new User();

        user.setUsername(item.get("username").s());
        user.setPassword(item.get("password").s());
        user.setEmail(item.get("email").s());

        return user;
    }
}
