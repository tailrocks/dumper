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
package com.zhokhov.dumper.api.graphql.input;

import com.zhokhov.dumper.data.jooq.enums.ExportDeclarationDestination;

import java.util.List;

public class ExportDeclarationCreateInput {

    private String description;
    private ExportDeclarationDestination destination;
    private String sourceDatabaseName;
    private String targetDatabaseName;
    private List<ExportItemInput> items;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExportDeclarationDestination getDestination() {
        return destination;
    }

    public void setDestination(ExportDeclarationDestination destination) {
        this.destination = destination;
    }

    public String getSourceDatabaseName() {
        return sourceDatabaseName;
    }

    public void setSourceDatabaseName(String sourceDatabaseName) {
        this.sourceDatabaseName = sourceDatabaseName;
    }

    public String getTargetDatabaseName() {
        return targetDatabaseName;
    }

    public void setTargetDatabaseName(String targetDatabaseName) {
        this.targetDatabaseName = targetDatabaseName;
    }

    public List<ExportItemInput> getItems() {
        return items;
    }

    public void setItems(List<ExportItemInput> items) {
        this.items = items;
    }

}
