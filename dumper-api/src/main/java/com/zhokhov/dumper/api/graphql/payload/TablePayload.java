package com.zhokhov.dumper.api.graphql.payload;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.model.TableGraph;
import com.zhokhov.jambalaya.graphql.model.AbstractPayload;
import edu.umd.cs.findbugs.annotations.NonNull;

public class TablePayload extends AbstractPayload<TableGraph, SecurityError> {

    public TablePayload(@NonNull TableGraph data) {
        super(data);
    }

    public TablePayload(@NonNull SecurityError error) {
        super(error);
    }

}
