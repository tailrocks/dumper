package com.zhokhov.dumper.api.graphql.mutation;

import com.zhokhov.dumper.api.graphql.input.DatabaseCreateInput;
import com.zhokhov.dumper.api.graphql.payload.DatabaseCreatePayload;
import graphql.kickstart.tools.GraphQLMutationResolver;

import javax.inject.Singleton;

@Singleton
public class DatabaseCreateServerMutation implements GraphQLMutationResolver {

    public DatabaseCreatePayload databaseCreate(DatabaseCreateInput input) {
        throw new UnsupportedOperationException();
    }

}
