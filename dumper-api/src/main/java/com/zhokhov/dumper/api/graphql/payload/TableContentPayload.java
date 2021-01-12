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
