package com.zhokhov.dumper.api.graphql.error;

import com.zhokhov.dumper.api.graphql.error.code.LoginErrorCode;
import com.zhokhov.jambalaya.graphql.model.AbstractError;
import edu.umd.cs.findbugs.annotations.NonNull;

public class LoginError extends AbstractError<LoginErrorCode> {

    public LoginError(@NonNull LoginErrorCode code, @NonNull String message) {
        super(code, message);
    }

}
