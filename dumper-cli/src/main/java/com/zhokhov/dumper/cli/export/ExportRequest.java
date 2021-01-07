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
