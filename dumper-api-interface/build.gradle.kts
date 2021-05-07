plugins {
    `java-library`
    id("io.github.kobylynskyi.graphql.codegen") version Versions.gradleGraphqlCodegenPlugin
}

dependencies {
    implementation(platform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    implementation("com.github.spotbugs:spotbugs-annotations")

    api("com.graphql-java-kickstart:graphql-java-tools:${Versions.graphQlTools}")
}

tasks.named<io.github.kobylynskyi.graphql.codegen.gradle.GraphQLCodegenGradleTask>("graphqlCodegen") {
    graphqlSchemaPaths = listOf(
        "$projectDir/src/main/resources/graphql/dumper.graphqls",
        "$projectDir/src/main/resources/graphql/include/dumper-enums.graphqls",
        "$projectDir/src/main/resources/graphql/include/dumper-errors.graphqls",
        "$projectDir/src/main/resources/graphql/include/dumper-inputs.graphqls",
        "$projectDir/src/main/resources/graphql/include/dumper-payloads.graphqls",
        "$projectDir/src/main/resources/graphql/include/dumper-types.graphqls"
    )
    outputDir = File("$buildDir/generated")
    apiPackageName = "com.zhokhov.dumper.graphql.api"
    modelPackageName = "com.zhokhov.dumper.graphql.model"
    customTypesMapping = mutableMapOf(Pair("LocalDateTime", "java.time.LocalDateTime"))
    parentInterfaces {
        queryResolver = "graphql.kickstart.tools.GraphQLQueryResolver"
        mutationResolver = "graphql.kickstart.tools.GraphQLMutationResolver"
        subscriptionResolver = "graphql.kickstart.tools.GraphQLSubscriptionResolver"
        resolver = "graphql.kickstart.tools.GraphQLResolver<{{TYPE}}>"
    }
    generateApis = true
    generateBuilder = false
    generateEqualsAndHashCode = false
    generateDataFetchingEnvironmentArgumentInApis = true
    generateToString = true
    fieldsWithResolvers = setOf("@resolver")
    modelValidationAnnotation = "edu.umd.cs.findbugs.annotations.NonNull"
    apiRootInterfaceStrategy = com.kobylynskyi.graphql.codegen.model.ApiRootInterfaceStrategy.DO_NOT_GENERATE
    apiReturnType = "java.util.concurrent.CompletionStage"
}

sourceSets {
    getByName("main").java.srcDirs("$buildDir/generated")
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn("graphqlCodegen")
}
