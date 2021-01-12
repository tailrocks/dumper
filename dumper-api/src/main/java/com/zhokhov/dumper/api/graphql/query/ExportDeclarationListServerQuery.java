package com.zhokhov.dumper.api.graphql.query;

import com.zhokhov.dumper.api.graphql.payload.ExportDeclarationListPayload;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;

@Singleton
public class ExportDeclarationListServerQuery implements GraphQLQueryResolver {

    public ExportDeclarationListPayload exportDeclarationList() {
        throw new UnsupportedOperationException();
    }

}
