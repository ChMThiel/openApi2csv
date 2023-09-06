package io.gec.openapi.csv;

import io.swagger.v3.oas.models.OpenAPI;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;

class CsvWriterTest {

    @Test
    void shouldReadOpenApiFromFileAndWriteAsCSV() throws Exception {
        //given
        OpenApiReader reader = new OpenApiReader();
        CsvWriter writer = new CsvWriter();
        Configuration configuration = new Configuration();
        File tempFile = File.createTempFile("test", "csv");
        FileWriter out = new FileWriter(tempFile);
        //when
        OpenAPI openApi = reader.fromFile(new File("src/test/resources/openapi.yaml"), true, true);
        writer.write(openApi, out, configuration);
        //then
        List<String> allLines = Files.readAllLines(tempFile.toPath());
        assertThat(allLines, hasSize(32858));
    }

    @Test
    void shouldReadOpenApiLocationFileAndWriteAsCSV() throws Exception {
        //given
        OpenApiReader reader = new OpenApiReader();
        CsvWriter writer = new CsvWriter();
        Configuration configuration = new Configuration();
        File tempFile = File.createTempFile("test", "csv");
        FileWriter out = new FileWriter(tempFile);
        //when
        OpenAPI openApi = reader.fromLocation("src/test/resources/openapi.yaml", true, true);
        writer.write(openApi, out, configuration);
        //then
        List<String> allLines = Files.readAllLines(tempFile.toPath());
        assertThat(allLines, hasSize(32858));
    }
}
