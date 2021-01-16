package com.zhokhov.dumper.api.graphql.input;

public class DatabaseCreateInput {

    private String name;
    private String mainDatabaseName;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String dbname;
    private String environment;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainDatabaseName() {
        return mainDatabaseName;
    }

    public void setMainDatabaseName(String mainDatabaseName) {
        this.mainDatabaseName = mainDatabaseName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
