package com.zhokhov.dumper.api.graphql.payload;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import edu.umd.cs.findbugs.annotations.NonNull;

import static com.zhokhov.jambalaya.checks.Preconditions.checkNotNull;

public class UserLogoutPayload {

    private final SecurityError error;

    public UserLogoutPayload() {
        this.error = null;
    }

    public UserLogoutPayload(@NonNull SecurityError error) {
        checkNotNull(error, "error");

        this.error = error;
    }

    public SecurityError getError() {
        return error;
    }

}
