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
package io.gec.openapi.csv.openapi2csv.quarkus.extension.runtime;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.swagger.v3.oas.models.PathItem;
import java.util.Optional;
import java.util.Set;

/**
 * @since 07.09.2023
 */
@ConfigMapping(namingStrategy = ConfigMapping.NamingStrategy.VERBATIM, prefix = "io.gec.openapi.csv")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface OpenApiCsvConfiguration {

    /**
     * resolve all references in openApi (true) or just add the ref-pointer to csv (false)
     *
     * @return
     */
    @WithDefault("true")
    boolean resolveFully();

    /**
     * resolve combinators (onOf/allOf/anyOf)
     *
     * @return
     */
    @WithDefault("true")
    boolean resolveCombinators();

    /**
     * add only APIs to csv with path matching given regular expression (default null -> add all paths)
     * @return 
     */
    Optional<String> filterPathRegex();

    /**
     * add only APIs to csv with matching operation(s) (default null -> all all operations)
     * @return 
     */
    Optional<Set<PathItem.HttpMethod>> filterOperations();
}
