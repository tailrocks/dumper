package com.zhokhov.dumper.api.graphql.query;

import com.zhokhov.dumper.api.graphql.payload.UserListPayload;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;

@Singleton
public class UserListServerQuery implements GraphQLQueryResolver {

    public UserListPayload userList() {
        throw new UnsupportedOperationException();
    }

}
