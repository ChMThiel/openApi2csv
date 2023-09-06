/*
 * Copyright 2023 christiant.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gec.openapi.csv;

import io.swagger.v3.oas.models.PathItem;

/**
 * @since 06.09.2023
 */
class Row {

    public static final String[] HEADER = {
        "path", "operation", "description", "direction", "parameter", "enumeration", "in", "type",
        "nullable", "readOnly", "example", "ref"};
    
    final String path;
    PathItem.HttpMethod operation;
    String description;
    Direction direction;
    String parameter;
    String enumeration;
    String in;
    String type;
    Boolean nullable;
    Boolean readOnly;
    String example;
    String ref;

    enum Direction {
        INPUT, OUTPUT
    }

    public Row(String aPath) {
        path = aPath;
    }

    public String[] asCsvRow() {
        return new String[]{
            path,
            operation.name(),
            description,
            direction != null ? direction.name().toLowerCase() : null,
            parameter,
            enumeration,
            in,
            type,
            nullable != null ? nullable.toString() : null,
            readOnly != null ? readOnly.toString() : null,
            example,
            ref};
    }

    @Override
    public String toString() {
        return "Row{"
                + "path=" + path + ", "
                + "operation=" + operation + ", "
                + "description=" + description + ", "
                + "direction=" + direction + ", "
                + "parameter=" + parameter + ", "
                + "enumeration=" + enumeration + ", "
                + "in=" + in + ", "
                + "type=" + type + ", "
                + "nullable=" + nullable + ", "
                + "readOnly=" + readOnly + ", "
                + "example=" + example + ", "
                + "ref=" + ref
                + '}';
    }

}
