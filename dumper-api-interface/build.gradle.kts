plugins {
    `java-library`
    id("io.github.kobylynskyi.graphql.codegen") version "4.1.5"
}

tasks.named<io.github.kobylynskyi.graphql.codegen.gradle.GraphQLCodegenGradleTask>("graphqlCodegen") {
    graphqlSchemaPaths = listOf(
        "$projectDir/../dumper-api/src/main/resources/graphql/dumper.graphqls",
        "$projectDir/../dumper-api/src/main/resources/graphql/include/dumper-enums.graphqls",
        "$projectDir/../dumper-api/src/main/resources/graphql/include/dumper-errors.graphqls",
        "$projectDir/../dumper-api/src/main/resources/graphql/include/dumper-inputs.graphqls",
        "$projectDir/../dumper-api/src/main/resources/graphql/include/dumper-payloads.graphqls",
        "$projectDir/../dumper-api/src/main/resources/graphql/include/dumper-types.graphqls"
    )
    outputDir = File("$buildDir/generated")
    packageName = "com.example.graphql.model"
}

sourceSets {
    getByName("main").java.srcDirs("$buildDir/generated")
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn("graphqlCodegen")
}
