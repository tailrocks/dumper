package com.zhokhov.dumper.api.graphql.mutation;

import com.zhokhov.dumper.api.graphql.payload.UserLogoutPayload;
import graphql.kickstart.tools.GraphQLMutationResolver;

import javax.inject.Singleton;

@Singleton
public class UserLogoutServerMutation implements GraphQLMutationResolver {

    public UserLogoutPayload userLogout() {
        throw new UnsupportedOperationException();
    }

}
