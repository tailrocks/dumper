package com.zhokhov.dumper.api.graphql.payload;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.model.TableGraph;
import com.zhokhov.jambalaya.graphql.model.AbstractPayload;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

public class TableListPayload extends AbstractPayload<List<TableGraph>, SecurityError> {

    public TableListPayload(@NonNull List<TableGraph> data) {
        super(data);
    }

    public TableListPayload(@NonNull SecurityError error) {
        super(error);
    }

}
