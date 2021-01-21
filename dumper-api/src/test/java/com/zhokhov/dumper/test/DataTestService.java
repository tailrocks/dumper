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
package com.zhokhov.dumper.test;

import org.jooq.DSLContext;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.zhokhov.dumper.data.jooq.tables.Account.ACCOUNT;
import static com.zhokhov.dumper.data.jooq.tables.Database.DATABASE;
import static com.zhokhov.dumper.data.jooq.tables.ExportDeclaration.EXPORT_DECLARATION;
import static com.zhokhov.dumper.data.jooq.tables.ExportItem.EXPORT_ITEM;

@Singleton
public class DataTestService {

    private final DSLContext dslContext;

    public DataTestService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional
    public void clean() {
        dslContext.deleteFrom(EXPORT_ITEM).execute();
        dslContext.deleteFrom(EXPORT_DECLARATION).execute();
        dslContext.deleteFrom(ACCOUNT).execute();
        dslContext.deleteFrom(DATABASE).execute();
    }

}
