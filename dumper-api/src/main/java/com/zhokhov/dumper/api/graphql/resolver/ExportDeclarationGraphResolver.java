package com.zhokhov.dumper.api.graphql.resolver;

import com.zhokhov.dumper.api.graphql.dataloader.AccountByIdDataLoader;
import com.zhokhov.dumper.api.graphql.model.ExportDeclarationGraph;
import com.zhokhov.dumper.api.graphql.model.UserGraph;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.schema.DataFetchingEnvironment;

import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public class ExportDeclarationGraphResolver implements GraphQLResolver<ExportDeclarationGraph> {

    private final AccountByIdDataLoader accountByIdDataLoader;

    public ExportDeclarationGraphResolver(AccountByIdDataLoader accountByIdDataLoader) {
        this.accountByIdDataLoader = accountByIdDataLoader;
    }

    public CompletableFuture<UserGraph> exporter(ExportDeclarationGraph exportDeclarationGraph,
                                                 DataFetchingEnvironment env) {
        return accountByIdDataLoader
                .load(exportDeclarationGraph.getEntity().getExporterAccountId(), env.getDataLoaderRegistry())
                .thenApply(UserGraph::new);
    }

}
