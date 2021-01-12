package com.zhokhov.dumper.api.graphql.payload;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.model.UserGraph;
import com.zhokhov.jambalaya.graphql.model.AbstractPayload;
import edu.umd.cs.findbugs.annotations.NonNull;

public class UserCreatePayload extends AbstractPayload<UserGraph, SecurityError> {

    public UserCreatePayload(@NonNull UserGraph data) {
        super(data);
    }

    public UserCreatePayload(@NonNull SecurityError error) {
        super(error);
    }

}
