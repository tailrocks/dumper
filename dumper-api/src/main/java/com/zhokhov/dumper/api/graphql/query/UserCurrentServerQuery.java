package com.zhokhov.dumper.api.graphql.query;

import com.zhokhov.dumper.api.graphql.payload.UserCurrentPayload;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;

@Singleton
public class UserCurrentServerQuery implements GraphQLQueryResolver {

    public UserCurrentPayload userCurrent() {
        throw new UnsupportedOperationException();
    }

}
