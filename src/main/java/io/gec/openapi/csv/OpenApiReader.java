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

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import java.io.File;
import java.util.List;

/**
 * @since 04.09.2023
 */
public class OpenApiReader {

    private static final OpenAPIParser PARSER = new OpenAPIParser();

    static ParseOptions getParseOptions(boolean aResolveCombinators) {
        ParseOptions options = new ParseOptions();
        options.setResolveFully(true);
        options.setResolveCombinators(aResolveCombinators);
        return options;
    }

    public static OpenAPI fromFile(File aFile, boolean aResolveCombinators) {
        return fromFile(aFile, aResolveCombinators);
    }

    public static OpenAPI fromFile(File aFile, List<AuthorizationValue> auths, boolean aResolveCombinators) {
        return fromLocation(aFile.getAbsolutePath(), auths, aResolveCombinators);
    }

    public static OpenAPI fromLocation(String aLocation, boolean aResolveCombinators) {
        return fromLocation(aLocation, null, aResolveCombinators);
    }

    public static OpenAPI fromLocation(String aLocation, List<AuthorizationValue> auths, boolean aResolveCombinators) {
        return readLocation(aLocation, auths, aResolveCombinators);
    }

    private static OpenAPI readContent(String content, List<AuthorizationValue> auths, boolean aResolveCombinators) {
        return PARSER.readContents(content, auths, getParseOptions(aResolveCombinators)).getOpenAPI();
    }

    private static OpenAPI readLocation(String location, List<AuthorizationValue> auths, boolean aResolveCombinators) {
        return PARSER.readLocation(location, auths, getParseOptions(aResolveCombinators)).getOpenAPI();
    }

}
