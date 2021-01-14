/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhokhov.dumper.api.config;

import com.zhokhov.dumper.api.graphql.error.LoginError;
import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.graphql.datetime.GraphQLLocalDate;
import com.zhokhov.graphql.datetime.GraphQLLocalDateTime;
import com.zhokhov.jambalaya.micronaut.graphql.HttpGraphQLExecutionInputCustomizer;
import graphql.GraphQL;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.kickstart.tools.SchemaParser;
import graphql.kickstart.tools.SchemaParserBuilder;
import graphql.schema.GraphQLSchema;
import io.micronaut.configuration.graphql.GraphQLExecutionInputCustomizer;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;
import java.util.List;

@Factory
public class GraphQLConfig {

    @Bean
    @Singleton
    public GraphQLExecutionInputCustomizer graphQLExecutionInputCustomizer() {
        return new HttpGraphQLExecutionInputCustomizer();
    }

    @Bean
    @Singleton
    public GraphQL graphQL(List<? extends GraphQLResolver<?>> resolverList) {
        SchemaParserBuilder builder = SchemaParser.newParser()
                .files(
                        "graphql/dumper.graphqls",
                        "graphql/include/dumper-enums.graphqls",
                        "graphql/include/dumper-errors.graphqls",
                        "graphql/include/dumper-inputs.graphqls",
                        "graphql/include/dumper-payloads.graphqls",
                        "graphql/include/dumper-types.graphqls"
                )
                .scalars(new GraphQLLocalDate())
                .scalars(new GraphQLLocalDateTime())
                .resolvers(resolverList)
                .dictionary(SecurityError.class)
                .dictionary(LoginError.class);

        GraphQLSchema graphQLSchema = builder.build().makeExecutableSchema();

        return GraphQL.newGraphQL(graphQLSchema).build();
    }

}
