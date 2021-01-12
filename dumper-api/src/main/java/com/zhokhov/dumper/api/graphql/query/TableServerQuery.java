package com.zhokhov.dumper.api.graphql.query;

import com.zhokhov.dumper.api.graphql.input.TableInput;
import com.zhokhov.dumper.api.graphql.payload.TablePayload;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;

@Singleton
public class TableServerQuery implements GraphQLQueryResolver {

    public TablePayload table(TableInput input) {
        throw new UnsupportedOperationException();
    }

}
