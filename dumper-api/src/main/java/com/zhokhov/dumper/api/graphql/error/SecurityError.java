package com.zhokhov.dumper.api.graphql.error;

import com.zhokhov.dumper.api.graphql.error.code.SecurityErrorCode;
import com.zhokhov.jambalaya.graphql.model.AbstractError;
import edu.umd.cs.findbugs.annotations.NonNull;

public class SecurityError extends AbstractError<SecurityErrorCode> {

    public SecurityError(@NonNull SecurityErrorCode code, @NonNull String message) {
        super(code, message);
    }

}
