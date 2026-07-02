package com.harshit.login;
import com.harshit.common.JWTUtil;
import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harshit.common.DynamoDBService;
import com.harshit.common.User;

public class LoginHandler implements RequestHandler<
        APIGatewayProxyRequestEvent,
        APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context) {

        try {

            ObjectMapper mapper = new ObjectMapper();

            User loginUser = mapper.readValue(request.getBody(), User.class);

            DynamoDBService service = new DynamoDBService();

            User dbUser = service.getUser(loginUser.getUsername());

            if (dbUser == null) {

                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(401)
                        .withBody("User Not Found");
            }

            if (!dbUser.getPassword().equals(loginUser.getPassword())) {

                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(401)
                        .withBody("Invalid Password");
            }

            String token = JWTUtil.generateToken(dbUser.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("token", token);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(mapper.writeValueAsString(response));

        } catch (Exception e) {

            e.printStackTrace();

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody(e.getMessage());
        }
    }
}