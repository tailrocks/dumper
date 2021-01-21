/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
