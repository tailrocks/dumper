package com.zhokhov.dumper.api.graphql.payload;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.model.ExportDeclarationGraph;
import com.zhokhov.jambalaya.graphql.model.AbstractPayload;
import edu.umd.cs.findbugs.annotations.NonNull;

public class ExportDeclarationCreatePayload extends AbstractPayload<ExportDeclarationGraph, SecurityError> {

    public ExportDeclarationCreatePayload(@NonNull ExportDeclarationGraph data) {
        super(data);
    }

    public ExportDeclarationCreatePayload(@NonNull SecurityError error) {
        super(error);
    }

}
