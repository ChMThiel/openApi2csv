package io.gec.openapi.csv.openapi2csv.quarkus.extension.deployment;

import io.gec.openapi.csv.openapi2csv.quarkus.extension.runtime.OpenApiCsvHandler;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.vertx.http.deployment.FilterBuildItem;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.filters.Filter;
import io.vertx.ext.web.Route;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.microprofile.config.ConfigProvider;

class Openapi2csvQuarkusExtensionProcessor {

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("openapi2csv-quarkus-extension");
    }

    @BuildStep
    void handleHttpRequests(
            BuildProducer<RouteBuildItem> aRoutes,
            NonApplicationRootPathBuildItem aNonApplicationRootPathBuildItem,
            List<FilterBuildItem> aFilterBuildItems) {
        OpenApiCsvHandler handler = new OpenApiCsvHandler();
        Consumer<Route> corsFilter = null;
        // Add CORS filter if the path is not attached to main root
        // as 'http-vertx' only adds CORS filter to http route path
        if (!aNonApplicationRootPathBuildItem.isAttachedToMainRouter()) {
            for (FilterBuildItem filterBuildItem : aFilterBuildItems) {
                if (filterBuildItem.getPriority() == FilterBuildItem.CORS) {
                    corsFilter = corsFilter(filterBuildItem.toFilter());
                    break;
                }
            }
        }
        String rootPath = getRootPath();
        aRoutes.produce(aNonApplicationRootPathBuildItem.routeBuilder()
                .routeFunction(rootPath, corsFilter)
                .handler(handler)
                .displayOnNotFoundPage("OpenApi2CSV not found")
                .blockingRoute()
                .build());
        aRoutes.produce(aNonApplicationRootPathBuildItem.routeBuilder()
                .routeFunction(getOpenApiPath() + ".csv", corsFilter)
                .handler(handler)
                .build());
    }

    String getOpenApiPath() {
        String openApiPath = ConfigProvider
                .getConfig()
                .getOptionalValue("quarkus.smallrye-openapi.path", String.class)
                .orElse("openapi");
        return getRootPath() + "/q/" + openApiPath;
    }

    String getRootPath() {
        return ConfigProvider.getConfig().getValue("quarkus.http.root-path", String.class);
    }

    Consumer<Route> corsFilter(Filter filter) {
        //cors always enabled
        if (filter.getHandler() != null) {
            return (Route route) -> route.order(-1 * filter.getPriority()).handler(filter.getHandler());
        }
        return null;
    }

}
