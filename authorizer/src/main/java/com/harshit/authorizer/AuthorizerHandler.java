package com.harshit.authorizer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;
import com.amazonaws.services.lambda.runtime.events.IamPolicyResponse;
import com.harshit.common.DynamoDBService;
import com.harshit.common.JWTUtil;
import com.harshit.common.User;

import java.util.Collections;

public class AuthorizerHandler implements RequestHandler<APIGatewayCustomAuthorizerEvent, IamPolicyResponse> {

    @Override
    public IamPolicyResponse handleRequest(APIGatewayCustomAuthorizerEvent event,
                                           Context context) {

        try {

            String token = event.getAuthorizationToken();

            if (token == null || token.isEmpty()) {
                return deny(event);
            }

            token = token.replace("Bearer ", "");

            if (!JWTUtil.validateToken(token)) {
                return deny(event);
            }

            String username = JWTUtil.getUsername(token);

            DynamoDBService service = new DynamoDBService();

            User user = service.getUser(username);

            if (user == null) {
                return deny(event);
            }

            return allow(event, username);

        } catch (Exception e) {
            e.printStackTrace();
            return deny(event);
        }
    }

    private IamPolicyResponse allow(APIGatewayCustomAuthorizerEvent event,
                                    String username) {

        return IamPolicyResponse.builder()
                .withPrincipalId(username)
                .withPolicyDocument(
                        IamPolicyResponse.PolicyDocument.builder()
                                .withVersion("2012-10-17")
                                .withStatement(Collections.singletonList(
                                        IamPolicyResponse.Statement.builder()
                                                .withAction("execute-api:Invoke")
                                                .withEffect("Allow")
                                                .withResource(Collections.singletonList(event.getMethodArn()))
                                                .build()))
                                .build())
                .build();
    }

    private IamPolicyResponse deny(APIGatewayCustomAuthorizerEvent event) {

        return IamPolicyResponse.builder()
                .withPrincipalId("anonymous")
                .withPolicyDocument(
                        IamPolicyResponse.PolicyDocument.builder()
                                .withVersion("2012-10-17")
                                .withStatement(Collections.singletonList(
                                        IamPolicyResponse.Statement.builder()
                                                .withAction("execute-api:Invoke")
                                                .withEffect("Deny")
                                                .withResource(Collections.singletonList(event.getMethodArn()))
                                                .build()))
                                .build())
                .build();
    }
}