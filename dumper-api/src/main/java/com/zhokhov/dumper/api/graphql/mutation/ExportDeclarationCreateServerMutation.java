package com.zhokhov.dumper.api.graphql.mutation;

import com.zhokhov.dumper.api.graphql.input.ExportDeclarationCreateInput;
import com.zhokhov.dumper.api.graphql.payload.ExportDeclarationCreatePayload;
import graphql.kickstart.tools.GraphQLMutationResolver;

import javax.inject.Singleton;

@Singleton
public class ExportDeclarationCreateServerMutation implements GraphQLMutationResolver {

    public ExportDeclarationCreatePayload exportDeclarationCreate(ExportDeclarationCreateInput input) {
        throw new UnsupportedOperationException();
    }

}
