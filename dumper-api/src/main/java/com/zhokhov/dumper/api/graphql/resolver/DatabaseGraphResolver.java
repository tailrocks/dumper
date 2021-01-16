package com.zhokhov.dumper.api.graphql.resolver;

import com.zhokhov.dumper.api.graphql.dataloader.DatabaseByMainDatabaseIdDataLoader;
import com.zhokhov.dumper.api.graphql.model.DatabaseGraph;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.schema.DataFetchingEnvironment;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class DatabaseGraphResolver implements GraphQLResolver<DatabaseGraph> {

    private final DatabaseByMainDatabaseIdDataLoader databaseByMainDatabaseIdDataLoader;

    public DatabaseGraphResolver(DatabaseByMainDatabaseIdDataLoader databaseByMainDatabaseIdDataLoader) {
        this.databaseByMainDatabaseIdDataLoader = databaseByMainDatabaseIdDataLoader;
    }

    public CompletableFuture<List<DatabaseGraph>> subDatabases(DatabaseGraph databaseGraph,
                                                               DataFetchingEnvironment env) {
        return databaseByMainDatabaseIdDataLoader
                .load(databaseGraph.getId(), env.getDataLoaderRegistry())
                .thenApply(result -> {
                    if (result == null) {
                        return new ArrayList<>();
                    }
                    return result.stream()
                            .map(DatabaseGraph::new)
                            .collect(Collectors.toList());
                });
    }

}
