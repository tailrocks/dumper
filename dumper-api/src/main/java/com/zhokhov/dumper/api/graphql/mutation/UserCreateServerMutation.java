package com.zhokhov.dumper.api.graphql.mutation;

import com.zhokhov.dumper.api.graphql.input.UserCreateInput;
import com.zhokhov.dumper.api.graphql.payload.UserCreatePayload;
import graphql.kickstart.tools.GraphQLMutationResolver;

import javax.inject.Singleton;

@Singleton
public class UserCreateServerMutation implements GraphQLMutationResolver {

    public UserCreatePayload userCreate(UserCreateInput input) {
        throw new UnsupportedOperationException();
    }

}
