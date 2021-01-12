package com.zhokhov.dumper.api.graphql.payload;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.model.ExportDeclarationGraph;
import com.zhokhov.jambalaya.graphql.model.AbstractPayload;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

public class ExportDeclarationListPayload extends AbstractPayload<List<ExportDeclarationGraph>, SecurityError> {

    public ExportDeclarationListPayload(@NonNull List<ExportDeclarationGraph> data) {
        super(data);
    }

    public ExportDeclarationListPayload(@NonNull SecurityError error) {
        super(error);
    }

}
