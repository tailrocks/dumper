package com.zhokhov.dumper.cli.command;

import com.zhokhov.dumper.cli.export.Dumper;
import com.zhokhov.dumper.cli.export.ExportReport;
import com.zhokhov.dumper.cli.export.ExportRequest;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.core.annotation.ReflectiveAccess;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Path;

@Command(
        name = "export",
        // TODO docs
        description = "TODO",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
@Prototype
public class ExportCommand implements Runnable {

    @Option(names = {"--host"}, description = "PostgreSQL server host", defaultValue = "localhost")
    @ReflectiveAccess
    String host;

    @Option(names = {"--port"}, description = "PostgreSQL server port", defaultValue = "5432")
    @ReflectiveAccess
    int port;

    @Option(names = {"--dbname"}, description = "PostgreSQL database to dump", defaultValue = "postgres")
    @ReflectiveAccess
    String dbname;

    @Option(names = {"--username"}, description = "PostgreSQL username", defaultValue = "postgres")
    @ReflectiveAccess
    String username;

    @Option(names = {"--password"}, description = "PostgreSQL password", defaultValue = "")
    @ReflectiveAccess
    String password;

    @Parameters(index = "0", description = "Path to the folder where to export data")
    @ReflectiveAccess
    String exportDir;

    @Parameters(
            index = "1..*", arity = "1..*",
            description = "List of exported items in format <table_name>=<primary_key_value>"
    )
    @ReflectiveAccess
    String[] items;

    @Override
    public void run() {
        String dataSourceUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?ApplicationName=dumper";

        System.out.println("Exporting data");

        ExportRequest exportRequest = new ExportRequest();

        for (String item : items) {
            String[] parts = item.split("=");

            if (parts.length != 2) {
                throw new RuntimeException("Wrong value: " + item);
            }

            String tableName = parts[0];
            String id = parts[1];

            exportRequest.addItem(tableName, id);
        }

        Path exportPath = Path.of(exportDir).toAbsolutePath();

        System.out.println("Export path: " + exportPath);

        try {
            ExportReport exportReport = Dumper.exportToDir(
                    dataSourceUrl, username, password, exportRequest, exportPath
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Export finished");
    }

}
