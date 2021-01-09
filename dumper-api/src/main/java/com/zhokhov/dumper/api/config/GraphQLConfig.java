package com.zhokhov.dumper.api.config;

import com.zhokhov.graphql.datetime.GraphQLLocalDate;
import com.zhokhov.graphql.datetime.GraphQLLocalDateTime;
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

import static graphql.Scalars.GraphQLLong;

@Factory
public class GraphQLConfig {

    @Bean
    @Singleton
    public GraphQLExecutionInputCustomizer graphQLExecutionInputCustomizer() {
        return new CustomGraphQLExecutionInputCustomizer();
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
                .scalars(GraphQLLong)
                .resolvers(resolverList);

        GraphQLSchema graphQLSchema = builder.build().makeExecutableSchema();

        return GraphQL.newGraphQL(graphQLSchema).build();
    }

}
