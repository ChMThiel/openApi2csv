package io.gec.openapi.csv.openapi2csv.quarkus.extension.runtime;

import io.gec.openapi.csv.Configuration;
import io.gec.openapi.csv.CsvWriter;
import io.gec.openapi.csv.OpenApiReader;
import io.smallrye.config.SmallRyeConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import java.io.IOException;
import java.util.logging.Logger;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import java.io.StringWriter;
import org.eclipse.microprofile.config.ConfigProvider;

public class OpenApiCsvHandler implements Handler<RoutingContext> {

    private static final Logger LOGGER = Logger.getLogger(OpenApiCsvHandler.class.getName());

    @Override
    public void handle(RoutingContext aRoutingContext) {
        OpenApiCsvConfiguration config = ConfigProvider
                .getConfig()
                .unwrap(SmallRyeConfig.class)
                .getConfigMapping(OpenApiCsvConfiguration.class);
        HttpServerResponse resp = aRoutingContext.response();
        resp.headers().set("Content-Type", "text/plain;charset=UTF-8");
        String yamlUrl = aRoutingContext.request().absoluteURI().replace(".csv", ".yaml");
        OpenAPI openAPI = new OpenApiReader().fromLocation(yamlUrl, config.resolveFully(), config.resolveCombinators());
        try {
            StringWriter writer = new StringWriter();
            new CsvWriter().write(openAPI, writer, new Configuration(
                    config.filterPathRegex().orElse(null),
                    config.filterOperations().orElse(null)));
            resp.end(Buffer.buffer(writer.toString()));
        } catch (IOException e) {
            resp.end(Buffer.buffer("Error " + e.getMessage()));
        }
    }
}
