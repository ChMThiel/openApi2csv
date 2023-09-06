package io.gec.openapi.csv;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.QuarkusMainTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

/**
 *
 * @since 04.09.2023
 */
@QuarkusMainTest
class MainTest {

    @Test
    @Launch({
        "src/test/resources/openapi.yaml",
        "--csv=mdata.csv",
        //                "--filterPath=.*partdata.*", 
        //                "--filterOperation=POST", 
        "--resolveCombinators=false"
    })
    void shouldRun() throws Exception {
        //given
        //when
        //then
    }
}
