package io.gec.openapi.csv;

import com.opencsv.CSVWriter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.enterprise.context.Dependent;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Dependent
public class CsvWriter {

    private final static Set<String> GOOD_RESPONSE_CODES = Set.of("200", "201", "204");

    public void write(OpenAPI aOpenApi, Writer aWriter, Configuration aConfiguration) throws IOException {
        try (CSVWriter csv = new CSVWriter(aWriter)) {
            csv.writeNext(Row.HEADER);
            aOpenApi.getPaths().entrySet().stream()
                    .filter(e -> aConfiguration.isMatchingPathRegex(e.getKey()))
                    .forEach(pathEntry -> {
                        pathEntry.getValue().readOperationsMap().entrySet().stream()
                                .filter(e -> aConfiguration.isMatchingOperation(e.getKey()))
                                .forEach(operationEntry -> {
                                    Row row = new Row(pathEntry.getKey());
                                    row.operation = operationEntry.getKey();
                                    Operation operation = operationEntry.getValue();
                                    row.description = operation.getSummary();
                                    csv.writeNext(row.asCsvRow());
                                    row.direction = Row.Direction.INPUT;
                                    if (operation.getParameters() != null) {
                                        for (Parameter parameter : operation.getParameters()) {
                                            row.parameter = parameter.getName();
                                            row.in = parameter.getIn();
                                            Schema schema = parameter.getSchema();
                                            if (schema != null) {
                                                writeSchema(csv, row, schema, parameter.getName());
                                            }
                                            row.ref = parameter.get$ref();
                                            row.description = parameter.getDescription();
                                            row.nullable = Optional.ofNullable(parameter.getRequired()).map(b -> !b).orElse(null);
                                            row.readOnly = null;
                                            row.example = Optional.ofNullable(parameter.getExample()).map(String::valueOf).orElse(null);
                                            csv.writeNext(row.asCsvRow());
                                        }
                                    }
                                    row.ref = null;
                                    row.parameter = null;
                                    if (operation.getRequestBody() != null && operation.getRequestBody().getContent().get("application/json") != null) {
                                        row.in = "body";
                                        Schema requestBodySchema = operation.getRequestBody().getContent().get("application/json").getSchema();
                                        writeSchema(csv, row, requestBodySchema, "body");
                                    }
                                    row.in = "body";
                                    row.direction = Row.Direction.OUTPUT;
                                    operation.getResponses().entrySet().stream()
                                            .filter(e -> GOOD_RESPONSE_CODES.contains(e.getKey()))
                                            .map(Map.Entry::getValue)
                                            .findFirst()
                                            .map(ApiResponse::getContent)
                                            .map(content -> content.get("application/json"))
                                            .map(MediaType::getSchema)
                                            .ifPresent(schema -> writeSchema(csv, row, schema, "body"));
                                });
                    });
            csv.flush();
        }
    }

    void writeSchema(CSVWriter aCsvWriter, Row aRow, Schema aSchema, String aName) {
        if ("array".equalsIgnoreCase(aSchema.getType())) {
            writeSchema(aCsvWriter, aRow, aSchema.getItems(), concat(aName, aSchema.getItems().getName()));
        } else if (aSchema.getProperties() != null && !aSchema.getProperties().isEmpty()) {
            Map<String, Schema> properties = aSchema.getProperties();
            properties.entrySet().forEach(e -> writeSchema(aCsvWriter, aRow, e.getValue(), concat(aName, e.getKey())));
        } else if (aSchema.getOneOf() != null && !aSchema.getOneOf().isEmpty()) {
            List<Schema> oneOf = aSchema.getOneOf();
            oneOf.forEach(x -> writeSchema(aCsvWriter, aRow, x, concat(aName, x.getName())));
        } else if (aSchema.getAllOf() != null && !aSchema.getAllOf().isEmpty()) {
            List<Schema> allOf = aSchema.getAllOf();
            allOf.forEach(x -> writeSchema(aCsvWriter, aRow, x, concat(aName, x.getName())));
        } else {
            aRow.type = aSchema.getType();
            aRow.description = aSchema.getDescription();
            aRow.parameter = aName;
            aRow.enumeration = Optional
                    .ofNullable(aSchema.getEnum())
                    .map(List::toString)
                    .orElse(null);
            aRow.nullable = aSchema.getNullable();
            aRow.readOnly = aSchema.getReadOnly();
            aRow.example = Optional.ofNullable(aSchema.getExample()).map(String::valueOf).orElse(null);
            aRow.ref = aSchema.get$ref();
            aCsvWriter.writeNext(aRow.asCsvRow());
        }
    }

    String concat(String a, String b) {
        if (a != null && b != null) {
            return a + "." + b;
        }
        if (a != null) {
            return a;
        }
        if (b != null) {
            return b;
        }
        return null;
    }

}
