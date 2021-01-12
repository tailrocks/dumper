package com.zhokhov.dumper.api.graphql.query;

import com.zhokhov.dumper.api.graphql.payload.DatabaseListPayload;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;

@Singleton
public class DatabaseListServerQuery implements GraphQLQueryResolver {

    public DatabaseListPayload databaseList() {
        throw new UnsupportedOperationException();
    }

}
