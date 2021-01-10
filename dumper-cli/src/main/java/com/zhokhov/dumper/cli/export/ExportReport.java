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

public class ExportReport {

    // tables + ids

    // requested and successfully exported items
    private final Map<String, Set<String>> requestedItems = new LinkedHashMap<>();

    // not found requested items
    private final Map<String, Set<String>> notFoundItems = new LinkedHashMap<>();

    // additionally linked items, also exported
    private final Map<String, Set<String>> linkedItems = new LinkedHashMap<>();

    // linked items which not exported yet, but must be exported
    private final Map<String, Set<String>> pendingLinkedItems = new LinkedHashMap<>();

    public void addRequestedItem(@NonNull String tableName, @NonNull String id) {
        checkNotBlank(tableName, "tableName");
        checkNotBlank(id, "id");

        requestedItems.putIfAbsent(tableName, new HashSet<>());
        requestedItems.get(tableName).add(id);

        removeFromPendingLinkedItem(tableName, id);
    }

    public void addNotFoundItem(@NonNull String tableName, @NonNull String id) {
        checkNotBlank(tableName, "tableName");
        checkNotBlank(id, "id");

        notFoundItems.putIfAbsent(tableName, new HashSet<>());
        notFoundItems.get(tableName).add(id);

        removeFromPendingLinkedItem(tableName, id);
    }

    public void addLinkedItem(@NonNull String tableName, @NonNull String id) {
        checkNotBlank(tableName, "tableName");
        checkNotBlank(id, "id");

        linkedItems.putIfAbsent(tableName, new HashSet<>());
        linkedItems.get(tableName).add(id);

        removeFromPendingLinkedItem(tableName, id);
    }

    public void addPendingLinkedItem(@NonNull String tableName, @NonNull String id) {
        checkNotBlank(tableName, "tableName");
        checkNotBlank(id, "id");

        if (!isProcessed(tableName, id)) {
            pendingLinkedItems.putIfAbsent(tableName, new HashSet<>());
            pendingLinkedItems.get(tableName).add(id);
        }
    }

    public Set<String> getProcessedTables() {
        Set<String> result = new HashSet<>();
        result.addAll(requestedItems.keySet());
        result.addAll(notFoundItems.keySet());
        result.addAll(linkedItems.keySet());
        return result;
    }

    public boolean isProcessed(@NonNull String tableName, @NonNull String id) {
        checkNotBlank(tableName, "tableName");
        checkNotBlank(id, "id");

        Set<String> ids = requestedItems.get(tableName);

        if (ids != null && ids.contains(id)) {
            return true;
        }

        ids = notFoundItems.get(tableName);

        if (ids != null && ids.contains(id)) {
            return true;
        }

        ids = linkedItems.get(tableName);

        if (ids != null && ids.contains(id)) {
            return true;
        }

        return false;
    }

    public boolean isEmptyPendingLinkedItems() {
        return pendingLinkedItems.isEmpty();
    }

    public List<String> getPendingLinkedTables() {
        return List.copyOf(pendingLinkedItems.keySet());
    }

    public List<String> getPendingLinkedIds(@NonNull String tableName) {
        checkNotBlank(tableName, "tableName");

        return List.copyOf(pendingLinkedItems.get(tableName));
    }

    private void removeFromPendingLinkedItem(String tableName, String id) {
        if (pendingLinkedItems.get(tableName) != null) {
            pendingLinkedItems.get(tableName).remove(id);
            if (pendingLinkedItems.get(tableName).isEmpty()) {
                pendingLinkedItems.remove(tableName);
            }
        }
    }

}
