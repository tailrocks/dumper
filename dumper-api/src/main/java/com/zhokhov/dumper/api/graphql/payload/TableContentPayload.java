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
package com.zhokhov.dumper.api.graphql.payload;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.model.TableContentPayloadDataGraph;
import com.zhokhov.jambalaya.graphql.model.AbstractPayload;
import edu.umd.cs.findbugs.annotations.NonNull;

public class TableContentPayload extends AbstractPayload<TableContentPayloadDataGraph, SecurityError> {

    public TableContentPayload(@NonNull TableContentPayloadDataGraph data) {
        super(data);
    }

    public TableContentPayload(@NonNull SecurityError error) {
        super(error);
    }

}
