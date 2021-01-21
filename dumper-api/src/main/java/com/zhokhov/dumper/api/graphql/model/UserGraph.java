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

import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import com.zhokhov.jambalaya.graphql.jooq.model.AbstractPersistentEntity;
import edu.umd.cs.findbugs.annotations.NonNull;

public class UserGraph extends AbstractPersistentEntity<Long, AccountRecord> {

    public UserGraph(@NonNull AccountRecord entity) {
        super(entity);
    }

    public String username() {
        return getEntity().getUsername();
    }

    public String email() {
        return getEntity().getEmail();
    }

    public String firstName() {
        return getEntity().getFirstName();
    }

    public String lastName() {
        return getEntity().getLastName();
    }

}
