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
