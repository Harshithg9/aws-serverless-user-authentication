package com.harshit.register;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harshit.common.DynamoDBService;
import com.harshit.common.User;

public class RegisterHandler implements RequestHandler<
        APIGatewayProxyRequestEvent,
        APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context) {

        try {

            context.getLogger().log("========== REGISTER FUNCTION START ==========\n");

            context.getLogger().log("Step 1 : Request received\n");

            String body = request.getBody();

            context.getLogger().log("Request Body:\n" + body + "\n");

            context.getLogger().log("Step 2 : Parsing JSON\n");

            ObjectMapper mapper = new ObjectMapper();

            User user = mapper.readValue(body, User.class);

            context.getLogger().log("JSON Parsed Successfully\n");
            context.getLogger().log("Username : " + user.getUsername() + "\n");
            context.getLogger().log("Email : " + user.getEmail() + "\n");

            context.getLogger().log("Step 3 : Creating DynamoDB Client\n");

            DynamoDBService service = new DynamoDBService();

            context.getLogger().log("DynamoDB Client Created\n");

            context.getLogger().log("Step 4 : Saving User\n");

            service.saveUser(user);

            context.getLogger().log("User Saved Successfully\n");

            context.getLogger().log("========== REGISTER FUNCTION END ==========\n");

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("User Registered Successfully");

        } catch (Exception e) {

            context.getLogger().log("========== EXCEPTION ==========\n");

            context.getLogger().log(e.toString() + "\n");

            for (StackTraceElement element : e.getStackTrace()) {
                context.getLogger().log(element.toString() + "\n");
            }

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("Registration Failed : " + e.getMessage());
        }
    }
}