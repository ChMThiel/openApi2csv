package io.gec.openapi.csv;

import io.quarkus.runtime.QuarkusApplication;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @since 04.09.2023
 */
public class OpenApi2CSV implements QuarkusApplication {

    @Override
    public int run(String... args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder("h").longOpt("help").desc("print this message").build());
        options.addOption(Option.builder().longOpt("version").desc("print the version information and exit").build());
        Option logOption = Option.builder("l").longOpt("log")
                .hasArg().argName("level")
                .desc("use given level for log (TRACE, DEBUG, INFO, WARN, ERROR, OFF). Default: ERROR").build();
        options.addOption(logOption);
        Option csvFileNameOption = Option.builder().longOpt("csv")
                .hasArg().argName("file")
                .desc("export as csv in given file. Default " + CsvWriter.DEFAULT_FILE_NAME).build();
        options.addOption(csvFileNameOption);
        Option filterPathOption = Option.builder().longOpt("filterPath")
                .hasArg().argName("regex").desc("filter paths matching given regular expression").build();
        options.addOption(filterPathOption);
        Option filterOperationOption = Option.builder().longOpt("filterOperation")
                .hasArg().argName("operation")
                .desc(Arrays.toString(PathItem.HttpMethod.values()) + ". Multiple values sepratated by | are allowed.")
                .build();
        options.addOption(filterOperationOption);
        Option resolveCombinatorsOption = Option.builder().longOpt("resolveCombinators allOf/oneOf/etc.")
                .hasArg().argName("resolveCombinators")
                .desc("true or false (defautl false)")
                .build();
        options.addOption(resolveCombinatorsOption);
        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            Optional.ofNullable(line.getOptionValue(logOption))
                    .map(Level::parse)
                    .ifPresent(Main.logger::setLevel);
            String csvFileName = line.hasOption(csvFileNameOption) ? line.getOptionValue(csvFileNameOption) : null;
            String filterRegex = line.hasOption(filterPathOption) ? line.getOptionValue(filterPathOption) : null;
            Set<PathItem.HttpMethod> filterOperations = Optional.ofNullable(line.getOptionValue(filterOperationOption))
                    .map(o -> o.split("\\|"))
                    .map(s -> Arrays.stream(s).map(PathItem.HttpMethod::valueOf).collect(Collectors.toSet()))
                    .orElse(null);
            String path = line.getArgList().get(0);
            Boolean resolveCombinators = Optional.ofNullable(line.getOptionValue(resolveCombinatorsOption))
                    .map(Boolean::valueOf)
                    .orElse(false);
            OpenAPI openApi = OpenApiReader.fromLocation(path, resolveCombinators);
            CsvWriter.write(openApi, csvFileName, new Configuration(filterRegex, filterOperations));
        } catch (ParseException e) {
            // oops, something went wrong
            System.err.println("Parsing failed. Reason: " + e.getMessage());
            printHelp(options);
            System.exit(2);
        } catch (Exception e) {
            System.err.println("Unexpected exception. Reason: ");
            e.printStackTrace();
            System.exit(2);
        }
//        Quarkus.waitForExit();
        return 0;
    }

    void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("openapi2csv <openapi>", options);
    }
}
