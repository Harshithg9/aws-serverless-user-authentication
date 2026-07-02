package com.harshit.common;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class SecretManagerService {

    private final SecretsManagerClient client =
            SecretsManagerClient.builder()
                    .region(Region.AP_SOUTH_1)
                    .build();

    public String getSecret(String secretName) {

        GetSecretValueRequest request =
                GetSecretValueRequest.builder()
                        .secretId(secretName)
                        .build();

        GetSecretValueResponse response =
                client.getSecretValue(request);

        return response.secretString();
    }
}