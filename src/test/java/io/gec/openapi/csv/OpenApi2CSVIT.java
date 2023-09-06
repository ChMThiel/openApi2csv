
package io.gec.openapi.csv;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
 
/**
 *
 * @since 04.09.2023
 */
@QuarkusTest
class OpenApi2CSVIT {

    @Inject
    OpenApi2CSV testee;

    @Test
    void shouldRun() throws Exception {
        //given
        //when
        testee.run("src/test/resources/openapi.yaml", 
                "--csv=mdata.csv", 
//                "--filterPath=.*partdata.*", 
//                "--filterOperation=POST", 
                "--resolveCombinators=false");
        //then
    }
}