package com.zhokhov.dumper.cli.export;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

import static com.zhokhov.jambalaya.checks.Preconditions.checkNotNull;

public final class SchemaReader {

    private SchemaReader() {
    }

    public static Schema read(@NonNull Connection connection) throws SQLException, IOException {
        checkNotNull(connection, "connection");

        Statement statement = connection.createStatement();

        Schema schema = new Schema();

        String sql = getSql("/get-columns.sql");

        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            String tableName = rs.getString("table_name");
            String columnName = rs.getString("column_name");
            String dataType = rs.getString("data_type");
            String udtName = rs.getString("udt_name");

            boolean userDefined = dataType.equalsIgnoreCase("USER-DEFINED");

            schema.addColumn(tableName, columnName, udtName, userDefined);
        }

        sql = getSql("/get-primary-keys.sql");

        rs = statement.executeQuery(sql);

        while (rs.next()) {
            String tableName = rs.getString("table_name");
            String columnName = rs.getString("column_name");
            String constraintName = rs.getString("constraint_name");

            schema.setPrimaryKey(tableName, columnName, constraintName);
        }

        sql = getSql("/get-foreign-keys.sql");

        rs = statement.executeQuery(sql);

        while (rs.next()) {
            String tableName = rs.getString("table_name");
            String columnName = rs.getString("column_name");
            String foreignTableName = rs.getString("foreign_table_name");
            String foreignColumnName = rs.getString("foreign_column_name");

            schema.addForeignKey(tableName, columnName, foreignTableName, foreignColumnName);
        }

        return schema;
    }

    // TODO move to jambalaya
    private static String getSql(String resource) throws IOException {
        try (InputStream inputStream = SchemaReader.class.getResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new FileNotFoundException(resource + " not found");
            }
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
    }

}
