package io.gec.openapi.csv.openapi2csv.quarkus.extension.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class Openapi2csvQuarkusExtensionResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/openapi2csv-quarkus-extension")
                .then()
                .statusCode(200)
                .body(is("Hello openapi2csv-quarkus-extension"));
    }
}
