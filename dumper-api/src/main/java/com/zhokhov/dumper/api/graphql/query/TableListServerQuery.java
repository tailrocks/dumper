package com.zhokhov.dumper.api.graphql.query;

import com.zhokhov.dumper.api.graphql.input.TableListInput;
import com.zhokhov.dumper.api.graphql.payload.TableListPayload;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;

@Singleton
public class TableListServerQuery implements GraphQLQueryResolver {

    public TableListPayload tableList(TableListInput input) {
        throw new UnsupportedOperationException();
    }

}
