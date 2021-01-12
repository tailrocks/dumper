package com.zhokhov.dumper.api.graphql.payload;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.model.DatabaseGraph;
import com.zhokhov.jambalaya.graphql.model.AbstractPayload;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

public class DatabaseListPayload extends AbstractPayload<List<DatabaseGraph>, SecurityError> {

    public DatabaseListPayload(@NonNull List<DatabaseGraph> data) {
        super(data);
    }

    public DatabaseListPayload(@NonNull SecurityError error) {
        super(error);
    }

}
