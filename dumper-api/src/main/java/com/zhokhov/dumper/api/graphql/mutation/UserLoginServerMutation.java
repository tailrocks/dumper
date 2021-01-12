package com.zhokhov.dumper.api.graphql.mutation;

import com.zhokhov.dumper.api.graphql.input.UserLoginInput;
import com.zhokhov.dumper.api.graphql.payload.UserLoginPayload;
import graphql.kickstart.tools.GraphQLMutationResolver;

import javax.inject.Singleton;

@Singleton
public class UserLoginServerMutation implements GraphQLMutationResolver {

    public UserLoginPayload userLogin(UserLoginInput input) {
        throw new UnsupportedOperationException();
    }

}
