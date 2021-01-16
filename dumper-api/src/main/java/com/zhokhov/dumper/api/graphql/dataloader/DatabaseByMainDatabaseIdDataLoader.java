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
        return (keys, batchLoaderEnvironment) -> CompletableFuture.completedFuture(
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
