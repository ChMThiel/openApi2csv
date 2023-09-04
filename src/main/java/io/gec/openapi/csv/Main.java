package io.gec.openapi.csv;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import java.util.logging.Logger;

@QuarkusMain
public class Main {

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String... args) {
        Quarkus.run(OpenApi2CSV.class, args);
    }

}
