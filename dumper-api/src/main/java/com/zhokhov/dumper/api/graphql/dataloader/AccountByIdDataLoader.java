package com.zhokhov.dumper.api.graphql.dataloader;

import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import com.zhokhov.dumper.data.repository.AccountRepository;
import com.zhokhov.jambalaya.graphql.dataloader.AbstractMappedDataLoaderAdapter;
import org.dataloader.MappedBatchLoaderWithContext;

import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Singleton
public class AccountByIdDataLoader extends AbstractMappedDataLoaderAdapter<Long, AccountRecord> {

    private final AccountRepository accountRepository;

    public AccountByIdDataLoader(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    protected MappedBatchLoaderWithContext<Long, AccountRecord> getBatchLoader() {
        return (keys, batchLoaderEnvironment) -> CompletableFuture.completedFuture(
                accountRepository
                        .findAllByIdIn(keys)
                        .stream()
                        .collect(toMap(AccountRecord::getId, Function.identity()))
        );
    }

}
