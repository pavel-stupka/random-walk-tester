// File: ProgramOptions.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Tato trida obsahuje definice parametru prikazove radky
 * pro program. Vyuziva balik <code>org.apache.commons.cli</code>,
 * tedy Jakarta CLI.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class ProgramOptions {

    private Options options;
    private CommandLine commandLine;

    /**
     * Vytvori novou instanci tridy ProgramOptions.
     * @param args parametry prikazove radky
     */
    public ProgramOptions(String[] args) {
        buildOptions();
        try {
            // parsuje parametry prikazove radky
            CommandLineParser parser = new GnuParser();
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Vraci informaci o tom, zda-li je dana volba zadana
     *
     * @param option pozadovana volba
     * @return <code>true</code> pokud je volba zadana, jinak <code>false</code>
     */
    public boolean hasOption(String option) {
        return commandLine.hasOption(option);
    }
    
    /** 
     * Vraci hodnotu pozadovane volby
     *
     * @param option pozadovana volba
     * @return hodnota volby
     */
    public String getOptionValue(String option) {
        return commandLine.getOptionValue(option);
    }
    
    /**
     * Vraci seznam voleb
     *
     * @return seznam voleb
     */
    public Options getOptions() {
        return options;
    }
    
    /**
     * Vraci pouzitou instanci tridy CommandLine
     *
     * @return pouzita instance tridy CommandLine
     */
    public CommandLine getCommandLine() {
        return commandLine;
    }

    /**
     * Vytvori seznam voleb prikazove radky
     */
    private void buildOptions() {        
        options = new Options();        
        
        Option help = OptionBuilder.withDescription("print this message")
                                   .create("help");
        options.addOption(help);
        
        Option version = OptionBuilder.withDescription("print the version information and exit")
                                      .create("version");
        options.addOption(version);
        
        Option input = OptionBuilder.withArgName("graph")
                                    .hasArg()
                                    .withDescription("input graph file")
                                    .create("input");        
        options.addOption(input);
        
        Option template = OptionBuilder.withArgName("string")
                                    .hasArg()
                                    .withDescription("file template for saving results of the testing")
                                    .create("template");        
        options.addOption(template);
        
        Option mode = OptionBuilder.withArgName("mode")
                                    .hasArg()
                                    .withDescription("possible modes: analyze, cover, path")
                                    .create("mode");        
        options.addOption(mode);
        
        Option coverage = OptionBuilder.withArgName("value")
                                    .hasArg()
                                    .withDescription("required percentual graph coverage in a graph cover mode")
                                    .create("coverage");        
        options.addOption(coverage);
        
        Option loop = OptionBuilder.withArgName("runs")
                                    .hasArg()
                                    .withDescription("how many times the random walk should be run")
                                    .create("loop");        
        options.addOption(loop);
        
        Option start = OptionBuilder.withArgName("vertex")
                                    .hasArg()
                                    .withDescription("specify the start vertex of the random walk")
                                    .create("start");        
        options.addOption(start);
        
        Option target = OptionBuilder.withArgName("vertex")
                                    .hasArg()
                                    .withDescription("specify the target vertex of the random walk (path mode only)")
                                    .create("target");
        options.addOption(target);
        
        Option generate = OptionBuilder.withArgName("regexp")
                                    .hasArg()
                                    .withDescription("generate a new graph according to the given regular expression (examples: SF2-100, K50, T2-10, R100-500)")
                                    .create("generate");
        options.addOption(generate);
        
        Option rwmode = OptionBuilder.withArgName("mode")
                                    .hasArg()
                                    .withDescription("mode of the Random walk (classic, outdegree, routdegree, indegree, rindegree)")
                                    .create("rwmode");        
        options.addOption(rwmode);
        
        Option convert = OptionBuilder.withArgName("type")
                                    .hasArg()
                                    .withDescription("convert the input graph to a given format (supported formats: gml)")
                                    .create("convert");        
        options.addOption(convert);
        
        Option discover = OptionBuilder.withDescription("discover mode will be used")
                                   .create("discover");
        options.addOption(discover);
        
        Option gml = OptionBuilder.withDescription("generate gml info files")
                                   .create("gml");
        options.addOption(gml);
    }
}
