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
        Option logOption = Option.builder("l").longOpt("log")
                .hasArg().argName("level")
                .desc("use given level for log (TRACE, DEBUG, INFO, WARN, ERROR, OFF). Default: ERROR")
                .build();
        options.addOption(logOption);
        Option csvFileNameOption = Option.builder().longOpt("csv")
                .hasArg().argName("file")
                .desc("export as csv in given file. Default " + CsvWriter.DEFAULT_FILE_NAME).build();
        options.addOption(csvFileNameOption);
        Option filterPathOption = Option.builder("p").longOpt("filterPath")
                .hasArg().argName("regex")
                .desc("filter paths matching given regular expression").build();
        options.addOption(filterPathOption);
        Option filterOperationOption = Option.builder("o").longOpt("filterOperation")
                .hasArg().argName("operation")
                .desc(Arrays.toString(PathItem.HttpMethod.values()) + ". Multiple values sepratated by | are allowed.")
                .build();
        options.addOption(filterOperationOption);
        Option resolveFullyOption = Option.builder("r").longOpt("resolve")
                .hasArg().argName("resolve")
                .desc("resolve all references. true or false (default true)")
                .build();
        options.addOption(resolveFullyOption);
        Option resolveCombinatorsOption = Option.builder("rc").longOpt("resolveCombinators")
                .hasArg().argName("resolveCombinators")
                .desc("resolveCombinators allOf/oneOf/etc. true or false (default true)")
                .build();
        options.addOption(resolveCombinatorsOption);
        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            String path = line.getArgList().get(0);
            Optional.ofNullable(line.getOptionValue(logOption))
                    .map(Level::parse)
                    .ifPresent(Main.logger::setLevel);
            String csvFileName = line.getOptionValue(csvFileNameOption);
            String filterRegex = line.getOptionValue(filterPathOption);
            Set<PathItem.HttpMethod> filterOperations = Optional
                    .ofNullable(line.getOptionValue(filterOperationOption))
                    .map(o -> o.split("\\|"))
                    .map(s -> Arrays.stream(s).map(PathItem.HttpMethod::valueOf).collect(Collectors.toSet()))
                    .orElse(null);
            Boolean resolveFully = Optional
                    .ofNullable(line.getOptionValue(resolveFullyOption))
                    .map(Boolean::valueOf)
                    .orElse(true);
            Boolean resolveCombinators = Optional
                    .ofNullable(line.getOptionValue(resolveCombinatorsOption))
                    .map(Boolean::valueOf)
                    .orElse(true);
            OpenAPI openApi = OpenApiReader.fromLocation(path, resolveFully, resolveCombinators);
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
