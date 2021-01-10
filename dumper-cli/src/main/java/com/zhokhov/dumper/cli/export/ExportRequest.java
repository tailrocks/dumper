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
package com.zhokhov.dumper.cli.export;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.zhokhov.jambalaya.checks.Preconditions.checkNotBlank;

public final class ExportRequest {

    // tables + ids
    private final Map<String, Set<String>> items = new LinkedHashMap<>();

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public ExportRequest addItem(@NonNull String tableName, @NonNull String id) {
        checkNotBlank(tableName, "tableName");
        checkNotBlank(id, "id");

        items.putIfAbsent(tableName, new HashSet<>());
        items.get(tableName).add(id);

        return this;
    }

    public List<String> getExportTables() {
        return List.copyOf(items.keySet());
    }

    public List<String> getExportIds(@NonNull String tableName) {
        checkNotBlank(tableName, "tableName");

        return List.copyOf(items.get(tableName));
    }

}
