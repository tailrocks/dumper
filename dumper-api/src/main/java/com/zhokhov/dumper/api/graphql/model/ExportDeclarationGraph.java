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
package com.zhokhov.dumper.api.graphql.model;

import com.zhokhov.dumper.data.jooq.enums.ExportDeclarationDestination;
import com.zhokhov.dumper.data.jooq.enums.ExportDeclarationStatus;
import com.zhokhov.dumper.data.jooq.tables.records.ExportDeclarationRecord;
import com.zhokhov.jambalaya.graphql.jooq.model.AbstractPersistentEntity;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.time.LocalDateTime;

public class ExportDeclarationGraph extends AbstractPersistentEntity<Long, ExportDeclarationRecord> {

    public ExportDeclarationGraph(@NonNull ExportDeclarationRecord entity) {
        super(entity);
    }

    public LocalDateTime date() {
        return getEntity().getDate();
    }

    public ExportDeclarationStatus status() {
        return getEntity().getStatus();
    }

    public String description() {
        return getEntity().getDescription();
    }

    public ExportDeclarationDestination destination() {
        return getEntity().getDestination();
    }

}
