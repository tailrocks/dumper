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
package com.zhokhov.dumper.api.graphql.dataloader;

import com.zhokhov.dumper.data.jooq.tables.records.DatabaseRecord;
import com.zhokhov.dumper.data.repository.DatabaseRepository;
import com.zhokhov.jambalaya.graphql.dataloader.AbstractMappedDataLoaderAdapter;
import org.dataloader.MappedBatchLoaderWithContext;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

@Singleton
public class DatabaseByMainDatabaseIdDataLoader extends AbstractMappedDataLoaderAdapter<Long, List<DatabaseRecord>> {

    private final DatabaseRepository databaseRepository;

    public DatabaseByMainDatabaseIdDataLoader(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    @Override
    protected MappedBatchLoaderWithContext<Long, List<DatabaseRecord>> getBatchLoader() {
        return (keys, environment) -> CompletableFuture.completedFuture(
                databaseRepository
                        .findAllByMainDatabaseId(new ArrayList<>(keys))
                        .stream()
                        .collect(
                                groupingBy(
                                        DatabaseRecord::getMainDatabaseId,
                                        mapping(it -> it, Collectors.toList())
                                )
                        )
        );
    }

}
