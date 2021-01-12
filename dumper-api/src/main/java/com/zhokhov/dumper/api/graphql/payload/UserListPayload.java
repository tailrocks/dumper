package com.zhokhov.dumper.api.graphql.payload;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.model.UserGraph;
import com.zhokhov.jambalaya.graphql.model.AbstractPayload;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

public class UserListPayload extends AbstractPayload<List<UserGraph>, SecurityError> {

    public UserListPayload(@NonNull List<UserGraph> data) {
        super(data);
    }

    public UserListPayload(@NonNull SecurityError error) {
        super(error);
    }

}
