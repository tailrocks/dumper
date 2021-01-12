package com.zhokhov.dumper.api.graphql.payload;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.model.DatabaseGraph;
import com.zhokhov.jambalaya.graphql.model.AbstractPayload;
import edu.umd.cs.findbugs.annotations.NonNull;

public class DatabaseCreatePayload extends AbstractPayload<DatabaseGraph, SecurityError> {

    public DatabaseCreatePayload(@NonNull DatabaseGraph data) {
        super(data);
    }

    public DatabaseCreatePayload(@NonNull SecurityError error) {
        super(error);
    }

}
