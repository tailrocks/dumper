package com.zhokhov.dumper.api.graphql.query;

import com.zhokhov.dumper.api.graphql.input.TableContentInput;
import com.zhokhov.dumper.api.graphql.payload.TableContentPayload;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;

@Singleton
public class TableContentServerQuery implements GraphQLQueryResolver {

    public TableContentPayload tableContent(TableContentInput input) {
        throw new UnsupportedOperationException();
    }

}
