package io.gec.openapi.csv;

import io.swagger.v3.oas.models.PathItem;
import java.util.Set;

public record Configuration(String pathRegex, Set<PathItem.HttpMethod> operations) {

    public Configuration() {
        this(null, null);
    }

    public boolean isMatchingPathRegex(String aPath) {
        return pathRegex == null || aPath.matches(pathRegex);
    }

    public boolean isMatchingOperation(PathItem.HttpMethod aHttpMethod) {
        return operations == null || operations.contains(aHttpMethod);
    }
}
