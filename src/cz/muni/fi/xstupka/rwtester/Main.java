// File: Main.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester;

import cz.muni.fi.xstupka.rwtester.graph.CompleteGraphGenerator;
import cz.muni.fi.xstupka.rwtester.graph.Graph;
import cz.muni.fi.xstupka.rwtester.graph.GraphException;
import cz.muni.fi.xstupka.rwtester.graph.GraphFactory;
import cz.muni.fi.xstupka.rwtester.graph.GraphGMLTimeWriter;
import cz.muni.fi.xstupka.rwtester.graph.GraphGMLVisitedWriter;
import cz.muni.fi.xstupka.rwtester.graph.GraphGMLWriter;
import cz.muni.fi.xstupka.rwtester.graph.GraphLoader;
import cz.muni.fi.xstupka.rwtester.graph.GraphLoaderException;
import cz.muni.fi.xstupka.rwtester.graph.GraphTextWriter;
import cz.muni.fi.xstupka.rwtester.graph.GraphWriter;
import cz.muni.fi.xstupka.rwtester.graph.RandomGraphGenerator;
import cz.muni.fi.xstupka.rwtester.graph.ScaleFreeGraphGenerator;
import cz.muni.fi.xstupka.rwtester.graph.TreeGraphGenerator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * Hlavni trida programu obsahujici vstupni bod programu - metodu main
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class Main {
        
    // definice konstant
    public static final String VERSION = "1.5";
    public static final String DEFAULT_START_VERTEX = "0";
    public static final String DEFAULT_TARGET_VERTEX = "0";
    public static final int DEFAULT_LOOP = 10;
    public static final int DEFAULT_COVERAGE = 100;
    public static final String DEFAULT_RW_MODE = "classic";
    
    
    private ProgramOptions options;
    
    /**
     * Hlavni metoda main, vstupni bod aplikace
     *
     * @args parametry prikazove radky
     */
    public static void main(String[] args) {
        Main program = new Main();
        program.run(args);
    }
    
    /**
     * Tato metoda je zacatkem vlastniho programu
     *
     * @param args parametry prikazove radky
     */
     public void run(String[] args) {         
         options = new ProgramOptions(args);
         
         // pokud je zadana volba "version", program vypise informace o verzi a skonci
         if (options.hasOption("version")) {
             printVersionAndExit();
         }
        
         // pokud je zadana volba "help", program vypise napovedu a skonci
         if (options.hasOption("help")) {
             printHelpAndExit(options.getOptions());
         }
         
         // pokud je zadana volba "generate", program vygeneruje pozadovany graf a skonci
         if (options.hasOption("generate")) {
             generateGraphAndExit();
         }
         
         // pokud je zadana volba "convert", program zkonvertuje graf do daneho
         // formatu a skonci
         if (options.hasOption("convert")) {
             convertGraphAndExit();
         }
         
         // vybere jednu z moznosti volby "mode" a provede prislusnou akci
         if (options.hasOption("mode")) {
             String mode = options.getOptionValue("mode");
             
             if (mode.equals("analyze")) { // analyza grafu 
                 analyzeGraphAndExit();
             } else if (mode.equals("cover")) { // nahodna prochazka pro pokryti grafu
                 runCoverAndExit();
             } else if (mode.equals("path")) { // nahodna prochazka pro nalezeni cesty k vrcholu
                 runPathAndExit();
             } else { // neznamy mod pro volbu "mode"
                 System.out.println("Error: '" + mode + "' unknown mode");
                 System.exit(0);
             }
         }

          // implicitne (t.j. program bez parametru) vypise napovedu a skonci
          printHelpAndExit(options.getOptions());
     }
     
     
     /**
      * Testovani nahodne prochazky v rezimu pokryti grafu
      */
     public void runCoverAndExit() {
         
         String foo = getRWMode(DEFAULT_RW_MODE);
         int rwmode = RandomWalk.CLASSIC_MODE;
         if (foo.equals("classic")) {
             rwmode = RandomWalk.CLASSIC_MODE;
         } else if (foo.equals("outdegree")) {
             rwmode = RandomWalk.OUT_DEGREE_MODE;
         } else if (foo.equals("routdegree")) {
             rwmode = RandomWalk.REVERSE_OUT_DEGREE_MODE;
         } else if (foo.equals("indegree")) {
             rwmode = RandomWalk.IN_DEGREE_MODE;
         } else if (foo.equals("rindegree")) {
             rwmode = RandomWalk.REVERSE_IN_DEGREE_MODE;
         }
         
         String graphName = getGraphName();
         String template = getTemplate(graphName);
         String startVertex = getStart(DEFAULT_START_VERTEX);
         int coverage = getCoverage(DEFAULT_COVERAGE);
         int loop = getLoop(DEFAULT_LOOP);
         
         // nacte graf
         Graph graph = loadGraph(graphName);
         RWManager manager = new RWManager(graph);
         manager.setRandomWalkMode(rwmode);
         
         // pokud je zapnuta volba pro discover mod, bude tento mod pouzit
         if (options.hasOption("discover")) {
             manager.setDiscoverMode(true);
         }
         
         // kontrola modu
         if (foo.equals("indegree") || foo.equals("rindegree")) {
             if (!graph.isDirected()) {
                 System.out.println("Error: " + foo + " rwmode selected but the graph isn't directed");
                 System.exit(0);
             }
         }
         
         try {
            // spusti testovani nahodne prochazky
            RWResult result = manager.testCover(loop, startVertex, coverage);
            if (result == null) {
                System.exit(0); // doslo k chybe
            }
            
            // pouzije tridu ResultWriter k vypisu vysledku analyzy nahodne prochazky
            System.out.println("\nSaving results");            
            ResultWriter resultWriter = new ResultWriter(template);
            resultWriter.write(result);
            // vypise informace do gml souboru
            if (options.hasOption("gml")) {
                // vypise graf s oznacenim vrcholu podle poctu navstiveni
                GraphWriter writer = new GraphGMLVisitedWriter(manager.getAverageGraph());
                writer.write(new File(template + "_coverage.gml"));
                
                // vypise graf s oznacenim vrcholu podle casu prvniho pristupu
                GraphWriter writerTime = new GraphGMLTimeWriter(manager.getAverageGraph());
                writerTime.write(new File(template + "_time.gml"));
            }
            
            System.out.println("\nDONE");
            
         } catch (GraphException ex) {
            System.out.println("Error (" + ex.getMessage() + ")");
         } catch (IOException ex) {
             System.out.print("IO Error: ");
             System.out.println(ex.getMessage());
             System.exit(0);
         }
         
         // generuje konfiguracni soubor pro program rwreport
         String loopStr = loop + "";
         String directedStr = graph.isDirected() + "";
         String coverageStr = coverage + "";
         writeReportConfig(template, "cover", foo, loopStr, directedStr, coverageStr);
         
         System.exit(0);
     }
     
     /**
      * Testovani nahodne prochazky v rezimu hledani cesty k danemu vrcholu
      */
     public void runPathAndExit() {
         
         String foo = getRWMode(DEFAULT_RW_MODE);
         int rwmode = RandomWalk.CLASSIC_MODE;
         if (foo.equals("classic")) {
             rwmode = RandomWalk.CLASSIC_MODE;
         } else if (foo.equals("outdegree")) {
             rwmode = RandomWalk.OUT_DEGREE_MODE;
         } else if (foo.equals("routdegree")) {
             rwmode = RandomWalk.REVERSE_OUT_DEGREE_MODE;
         } else if (foo.equals("indegree")) {
             rwmode = RandomWalk.IN_DEGREE_MODE;
         } else if (foo.equals("rindegree")) {
             rwmode = RandomWalk.REVERSE_IN_DEGREE_MODE;
         }
         
         String graphName = getGraphName();
         String template = getTemplate(graphName);
         String startVertex = getStart(DEFAULT_START_VERTEX);
         String endVertex = getTarget(DEFAULT_TARGET_VERTEX);
         int loop = getLoop(DEFAULT_LOOP);
         
         // nacte graf
         Graph graph = loadGraph(graphName);
         RWManager manager = new RWManager(graph);
         manager.setRandomWalkMode(rwmode);
         
         // pokud je zapnuta volba pro discover mod, bude tento mod pouzit
         if (options.hasOption("discover")) {
             manager.setDiscoverMode(true);
         }
         
         // kontrola modu
         if (foo.equals("indegree") || foo.equals("rindegree")) {
             if (!graph.isDirected()) {
                 System.out.println("Error: " + foo + " rwmode selected but the graph isn't directed");
                 System.exit(0);
             }
         }
         
         try {
            // spusti testovani nahodne prochazky
            RWResult result = manager.testFindPath(loop, startVertex, endVertex);
            if (result == null) {
                System.exit(0); // doslo k chybe
            }
            
            // pouzije tridu ResultWriter k vypisu vysledku analyzy nahodne prochazky
            System.out.println("\nSaving results");            
            ResultWriter resultWriter = new ResultWriter(template);
            resultWriter.write(result);
            // vypise informace do gml souboru
            if (options.hasOption("gml")) {
                // vypise graf s oznacenim vrcholu podle poctu navstiveni
                GraphWriter writerVisited = new GraphGMLVisitedWriter(manager.getAverageGraph());
                writerVisited.write(new File(template + "_coverage.gml"));
                
                // vypise graf s oznacenim vrcholu podle casu prvniho pristupu
                GraphWriter writerTime = new GraphGMLTimeWriter(manager.getAverageGraph());
                writerTime.write(new File(template + "_time.gml"));
            }
            
            System.out.println("\nDONE");
            
         } catch (GraphException ex) {
            System.out.println("Error (" + ex.getMessage() + ")");
         } catch (IOException ex) {
             System.out.print("IO Error: ");
             System.out.println(ex.getMessage());
             System.exit(0);
         }
         
         // generuje konfiguracni soubor pro program rwreport
         String loopStr = loop + "";
         String directedStr = graph.isDirected() + "";
         writeReportConfig(template, "path", foo, loopStr, directedStr, null);
         
         System.exit(0);
     }
     
     /**
      * Vygeneruje textovy soubor s konfiguracnim soubrem pro program
      * RWReport, ktery z vysledku testovani vytvari HTML zpravu
      * 
      * @param template sablona po
      */
     public void writeReportConfig(String template, 
                                   String mode, 
                                   String rwmode, 
                                   String loop, 
                                   String directed, 
                                   String coverage) {
         System.out.println("\nGenerating '" + template + "_config.txt' config file for rwreport\n");
         
         try {
            File file = new File(template + "_config.txt");
            FileWriter out = new FileWriter(file);
            
            out.write("template=" + template + "\n");
            out.write("mode=" + mode + "\n");
            out.write("rwmode=" + rwmode + "\n");
            out.write("loop=" + loop + "\n");
            out.write("directed=" + directed + "\n");
            if (coverage != null) {
                out.write("coverage=" + coverage + "\n");
            }
         
            out.close();
         } catch (IOException ex) {
             System.out.print("IO Error: ");
             System.out.println(ex.getMessage());
             System.exit(0);
         }

         System.out.println("ALL DONE\n");
     }

     /**
      * Provede analyzu zadaneho grafu a skonci
      */
     public void analyzeGraphAndExit() {
         String graphName = getGraphName();
         Graph graph = loadGraph(graphName);
         RWManager manager = new RWManager(graph);
         String template = getTemplate(graphName);
             
         // analyza grafu a zapis vysledku do textovych souboru podle sablony
         manager.analyzeGraph(template);

         System.exit(0);
     }
    
     /**
      * Vypise informace o verzi programu a skonci
      */
     private void printVersionAndExit() {
         System.out.println("RWTester version " + Main.VERSION);
         System.out.println("Copyricht (C) 2006-2008 Pavel Stupka");
         System.out.println("GNU GPL version 2");
         System.exit(0);
     }

     /** 
      * Vypise napovedu a skonci
      *
      * @param options pouzita instance tridy Options (volby programu)
      */
     private void printHelpAndExit(Options options) {
         HelpFormatter formatter = new HelpFormatter();
         formatter.printHelp("rwtester", options, true);
         System.exit(0);
     }
     
     /**
      * Zkonvertuje graf do pozadovaneho formatu a skonci
      */
     private void convertGraphAndExit() {
         String graphName = getGraphName();
         String template = getTemplate(graphName);
         String format = getConvertFormat();
         Graph graph = loadGraph(graphName);         
         
         try {
             String newGraphName = template;
             GraphWriter writer = null;

             // podle vybraneho formatu pro zkonvertovani zvoli prislusnou tridu
             if (format.equals("gml")) {
                 System.out.println("Converting to GML - Graph Modelling Language");
                 writer = new GraphGMLWriter(graph);
                 newGraphName += ".gml";
             }
             
             System.out.println("Saving graph to " + newGraphName);
             writer.write(new File(newGraphName));
         } catch (IOException ex) {
             System.out.print("IO Error: ");
             System.out.println(ex.getMessage());
             System.exit(1);
         }
         
         System.out.println("DONE");
         System.exit(0);
     }
     
     /**
      * Vygeneruje pozadovany graf a skonci
      */
     private void generateGraphAndExit() {
         String regexp = options.getOptionValue("generate");
         String graphName = getTemplate(regexp) + ".graph";
         
         // regularni vyrazy pro rozpoznani jaky graf vygenerovat
         String regexpSF = "SF\\d+-\\d+"; // bezskalovy graf
         String regexpT = "T\\d+-\\d+"; // n-arni strom
         String regexpK = "K\\d+";      // uplny graf
         String regexpR = "R\\d+-\\d+"; // nahodny graf

         /* String [] data = regexp.split(..) oddeli od retezce
          * regexp pocatecni pismeno => zbyde pouze retezec cisel
          * v promenne data[1], ktery je vstupem do nasledne volane
          * metody
          */
         
         Graph graph = null;
         
         if (regexp.matches(regexpSF)) {
             String [] data = regexp.split("SF");
             graph = generateScaleFreeGraph(data[1]);
         } else if (regexp.matches(regexpT)) {
             String [] data = regexp.split("T");
             graph = generateTreeGraph(data[1]);
         } else if (regexp.matches(regexpK)) {
             String [] data = regexp.split("K");
             graph = generateCompleteGraph(data[1]);
         } else if (regexp.matches(regexpR)) {
             String [] data = regexp.split("R");
             graph = generateRandomGraph(data[1]);
         } else {
             System.out.println("Error: unrecognized value '" + regexp + "' for generate option");
             System.exit(0);
         }
         
         System.out.println("Saving graph to '" + graphName + "'");
         
         try {
             GraphWriter writer = new GraphTextWriter(graph);
             writer.write(new File(graphName));
         } catch (IOException ex) {
             System.out.print("IO Error: ");
             System.out.println(ex.getMessage());
             System.exit(0);
         }
         
         System.out.println("DONE");
         
         System.exit(0);
     }
     
     /**
      * Vygeneruje nahodny graf
      *
      * @param data retezec popisujici typ grafu
      */
     private Graph generateRandomGraph(String data) {
         String [] foo = data.split("-");
         int vertices = Integer.parseInt(foo[0]);
         int edges = Integer.parseInt(foo[1]);
         
         System.out.println("Generating: random graph (vertices = " + vertices
                 + ", edges = "  + edges + ")");

         try {
             GraphFactory generator = new RandomGraphGenerator(vertices, edges);
             return generator.getGraph();
         } catch (IllegalArgumentException ex) {
             System.out.println("Error: wrong parameters for generating a random graph (in 'R" + data + "')");
             System.exit(0);
         }
         
         return null; // sem se program nikdy nedostane
     }
     
     /**
      * Vygeneruje bezskalovy graf
      *
      * @param data retezec popisujici typ grafu
      */
     private Graph generateScaleFreeGraph(String data) {
         String [] foo = data.split("-");
         int connect = Integer.parseInt(foo[0]);
         int vertices = Integer.parseInt(foo[1]);
         
         System.out.println("Generating: scale-free graph (connect = " + connect
                 + ", vertices = "  + vertices + ")");

         try {
             GraphFactory generator = new ScaleFreeGraphGenerator(vertices, connect);
             return generator.getGraph();
         } catch (IllegalArgumentException ex) {
             System.out.println("Error: wrong parameters for generating a scale-free graph (in 'SF" + data + "')");
             System.exit(0);
         }
         
         return null; // sem se program nikdy nedostane
     }
     
    /**
      * Vygeneruje n-arni strom
      *
      * @param data retezec popisujici typ grafu
      */
     private Graph generateTreeGraph(String data) {
         String [] foo = data.split("-");
         int arity = Integer.parseInt(foo[0]);
         int depth = Integer.parseInt(foo[1]);
         
         System.out.println("Generating: tree (arity = " + arity 
                 + ", depth = "  + depth + ")");

         try {
             GraphFactory generator = new TreeGraphGenerator(depth, arity);
             return generator.getGraph();
         } catch (IllegalArgumentException ex) {
             System.out.println("Error: wrong parameters for generating a tree (in 'T" + data + "')");
             System.exit(0);
         }
         
         return null; // sem se program nikdy nedostane
     }
     
     /**
      * Vygeneruje uplny graf
      *
      * @param data retezec popisujici typ grafu
      */
     private Graph generateCompleteGraph(String data) {
         int vertices = Integer.parseInt(data);
         
         System.out.println("Generating: complete graph (vertices = " + vertices + ")");

         try {
             GraphFactory generator = new CompleteGraphGenerator(vertices);
             return generator.getGraph();
         } catch (IllegalArgumentException ex) {
             System.out.println("Error: wrong parameters for generating a complete graph (in 'K" + data + "')");
             System.exit(0);
         }
         
         return null; // sem se program nikdy nedostane
     }
     
     /**
      * Nacte pozadovany graf
      *
      * @param graphName jmeno souboru s grafem
      * @return nacteny graf
      */
     private Graph loadGraph(String graphName) {
         Graph graph = null;
         try {
             System.out.println("Loading " + graphName);
             GraphFactory loader = new GraphLoader(new File(graphName));
             graph = loader.getGraph();             
         } catch (GraphLoaderException ex) {
             System.out.print("Error: ");
             System.out.println(ex.getMessage());
             System.exit(0);
         } catch (IOException ex) {
             System.out.print("IO Error: ");
             System.out.println(ex.getMessage());
             System.exit(0);
         }
         return graph;
     }
     
     /**
      * Vraci pozadovany vstupni graf.
      * Tato volba je pro nektere mody povinna.
      *
      * @return pozadovany vstupni graf
      */
     private String getGraphName() {
         if (options.hasOption("input")) {
             return options.getOptionValue("input");
         }
         
         System.out.println("Error: no input graph (use -input argument)");
         System.exit(1);
         
         return null;
     }
     
     /**
      * Vraci volbu pro "convert"
      * 
      * @return volba pro convert
      */
     private String getConvertFormat() {
         String format = options.getOptionValue("convert");
         
         if (format.equals("gml")) {
             return format;
         }
         
         System.out.println("Error: unknown convert format '" + format + "'");
         System.exit(1);
         
         return null;
     }
     
     /**
      * Vraci pozadovanou sablonu pro vystup textovych souboru
      *
      * @param defaultValue co se ma vratit v pripade, ze neni dana volba zadana
      * @return pozadovanou sablonu pro vystup textovych souboru
      */
     private String getTemplate(String defaultValue) {
         if (options.hasOption("template")) {
             return options.getOptionValue("template");
         }
         return defaultValue.replaceAll(".graph", "");
     }
     
     /**
      * Vraci pozadovany pocatecni vrchol
      *
      * @param defaultValue co se ma vratit v pripade, ze neni dana volba zadana
      * @return pozadovany pocatecni vrchol
      */
     private String getStart(String defaultValue) {
         if (options.hasOption("start")) {
             return options.getOptionValue("start");
         }
         return defaultValue;
     }
     
     /**
      * Vraci pozadovany mod nahodne prochazky
      *
      * @param defaultValue co se ma vratit v pripade, ze neni dana volba zadana
      * @return pozadovany mod nahodne prochazky
      */
     private String getRWMode(String defaultValue) {
         if (options.hasOption("rwmode")) {
             String foo = options.getOptionValue("rwmode");
             if (!foo.equals("classic") && !foo.equals("outdegree") && !foo.equals("routdegree") && !foo.equals("indegree") && !foo.equals("rindegree")) {
                 System.out.println("Error: unknown value for -rwmode argument");
                 System.exit(0);
             }
             return foo;
         }
         return defaultValue;
     }    
     
     /**
      * Vraci pozadovany cilovy vrchol
      *
      * @param defaultValue co se ma vratit v pripade, ze neni dana volba zadana
      * @return pozadovany cilovy vrchol
      */
     private String getTarget(String defaultValue) {
         if (options.hasOption("target")) {
             return options.getOptionValue("target");
         }
         return defaultValue;
     }
     
     /**
      * Vraci pozadovany pocet opakovani nahodne prochazky
      *
      * @param defaultValue co se ma vratit v pripade, ze neni dana volba zadana
      * @return pozadovany pocet opakovani nahodne prochazky
      */
     private int getLoop(int defaultValue) {
         if (options.hasOption("loop")) {
             try {
                String foo = options.getOptionValue("loop");
                int bar = Integer.parseInt(foo);
                return bar;
             } catch (NumberFormatException ex) {
                 System.out.println("Error: can't parse integer value for -loop argument");
                 System.exit(0);
             }
         }
         return defaultValue;
     }

     /**
      * Vraci pozadovane pokryti grafu
      *
      * @param defaultValue co se ma vratit v pripade, ze neni dana volba zadana
      * @return pozadovane pokryti grafu
      */
     private int getCoverage(int defaultValue) {
         if (options.hasOption("coverage")) {
             try {
                String foo = options.getOptionValue("coverage");
                int bar = Integer.parseInt(foo);
                return bar;
             } catch (NumberFormatException ex) {
                 System.out.println("Error: can't parse integer value for -coverage argument");
                 System.exit(0);
             }
         }
         return defaultValue;
     }
}
