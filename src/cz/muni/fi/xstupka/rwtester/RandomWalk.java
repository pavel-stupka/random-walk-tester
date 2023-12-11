// File: RandomWalk.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester;

import cz.muni.fi.xstupka.rwtester.graph.Graph;
import cz.muni.fi.xstupka.rwtester.graph.GraphException;
import cz.muni.fi.xstupka.rwtester.graph.Vertex;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Abstraktni trida reprezentujici nahodnou prochazku.
 * Trida je predkem pro <code>DirectedRandomWalk</code> realizujici nahodnou
 * prochazku na orientovanych grafech a pro <code>UndirectedRandomWalk</code>
 * realizujici nahodnou prochazku na neorientovancyh grafech.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public abstract class RandomWalk {

    // definice nekonecna
    public static final long INFINITY = -1;
            
    // mody nahodne prochazky
    public static final int CLASSIC_MODE = 1;
    public static final int OUT_DEGREE_MODE = 2;
    public static final int REVERSE_OUT_DEGREE_MODE = 3;
    public static final int IN_DEGREE_MODE = 4;
    public static final int REVERSE_IN_DEGREE_MODE = 5;
    
    
    // cas prubezneho vypisu prubehu nahodne prochazky
    public static final long DEFAULT_VERBOSE_TIME = 1000000;

    private boolean verbose;
    private long verboseTime;
    private int mode;
    private boolean discoverMode;
    
    private Graph graph;
    protected Random random;
    protected long time;
    protected int vertices;
    protected int visitedVertices;
    protected long[] percentageCover;
    protected String type;
    protected int run;

    /**
     * Vytvori novou instanci tridy RandomWalk
     *
     * @param graph graf, ktery ma byt pro nahodnou prochazku pouzit
     * @throws NullPointerException pokud je parametr <code>graph</code> null
     */
    public RandomWalk(Graph graph) {
        if (graph == null) {
            throw new NullPointerException();
        }
        discoverMode = false;
        this.graph = graph;
        vertices = graph.getNumberOfVertices();
        random = new Random();
        random.setSeed(hashCode() + System.nanoTime());
        percentageCover = new long[101];
        setVerbose(false);
        setVerboseTime(DEFAULT_VERBOSE_TIME);
        setMode(CLASSIC_MODE);
    }
    
    /**
     * Vraci celkovy cas, ktery byl potreba pro realizaci nahodne prochazky
     *
     * @return celkovy cas, ktery byl potreba pro realizaci nahodne prochazky
     */
    public long getTime() {
        return time;
    }

    /**
     * Vraci procentualni pokryti grafu.
     *
     * @return seznam dlouhy 101 prvku (indexy 0 .. 100), kde index udava procentualni
     *     pokryti grafu a hodnota daneho indexu pak cas, ve kterem bylo tohoto pokryti
     *     dosazeno
     */
    public long[] getPercentageCover() {
        return percentageCover;
    }

    /**
     * Spousti nahodnou prochazku pro pokryti grafu.
     * Mod pokryti grafu znamena, ze nahodna prochazka bude aktivni do te doby,
     * nez bude aspon jednou navstiven urcity pocet vrcholu. Procentualni pokryti
     * pak urcuje tuto hranici zastaveni nahodne prochazky.
     *
     * @param startVertex pocatecni vrchol, ze ktereho ma byt nahodna prochazka spustena
     * @param coverage procentualni pokryti, ktere ma byt dosazeno (v procentech)
     * @throws GraphException pokud vrchol zadaneho jmena v grafu neexistuje
     */
    public abstract void runCover(String startVertex, int coverage) throws GraphException;

    /**
     * Spousti nahodnou prochazku k nalezeni cesty k zadanemu vrcholu.
     * Princip tohoto modu nahodne prochazky spociva v tom, ze nahodna prochazka je
     * aktivni do te doby, nez je objeven zadany cilovy vrchol.
     *
     * @param startVertex pocatecni vrchol, ze ktereho ma byt nahodna prochazka spustena
     * @param endVertex koncovy vrchol, ktereho ma nahodna prochazka dosahnout
     * @throws GraphException pokud aspon jeden z vrcholu zadaneho jmena v grafu neexistuje
     */
    public abstract void runFindPath(String startVertex, String endVertex) throws GraphException;

    /**
     * Inicializuje grap pred spustenim algoritmu
     */
    protected void initGraph() {
        Collection<Vertex> vertices = getGraph().getVertices();
        for (Vertex v : vertices) {
            v.setValueA(0);          // pocet navstiveni
            v.setValueB(INFINITY);   // cas prvniho pristupu
            v.setParent(null);       // predek vrcholu
        }
        visitedVertices = 0;
        time = 0;
        random.setSeed(hashCode() + System.nanoTime());
        for (int p = 0; p < 101; p++) {
            percentageCover[p] = 0;
        }
        run = 1;
    }
    
    /**
     * Tato metoda vraci nahodneho naslednika vrcholu
     *
     * @param u vrchol, jehoz nahodneho naslednika chceme ziskat
     * @return nahodny naslednik zadaneho vrcholu nebo null v pripade, ze zadny
     *     nasledni neexistuje
     */
    protected Vertex getRandomNeighbour(Vertex u) {
        switch(mode) {
            case CLASSIC_MODE:
                return getRandomNeighbourAllSameProbability(u);
                
            case OUT_DEGREE_MODE:            
                return getRandomNeighbourOutDegreePropability(u);
                
            case REVERSE_OUT_DEGREE_MODE:
                return getRandomNeighbourReverseOutDegreePropability(u);
                
            case IN_DEGREE_MODE:            
                return getRandomNeighbourInDegreePropability(u);
                
            case REVERSE_IN_DEGREE_MODE:
                return getRandomNeighbourReverseInDegreePropability(u);
        }

        return null;
    }
    
    /**
     * Tato metoda vraci nahodneho naslednika vrcholu. Vsichni naslednici
     * maji stejnou pravdepodobnost, ze budou vybrani.
     *
     * @param u vrchol, jehoz nahodneho naslednika chceme ziskat
     * @return nahodny naslednik zadaneho vrcholu nebo null v pripade, ze zadny
     *     nasledni neexistuje
     */
    private Vertex getRandomNeighbourAllSameProbability(Vertex u) {
        if (u.getNumberOfNeighbours() == 0) {
            return null;
        }
        List<Vertex> list = u.getNeighbours();
        if (list == null) {
            return null;
        }
        int rnd = random.nextInt(u.getNumberOfNeighbours());
        return list.get(rnd);
    }
    
    /**
     * Tato metoda vraci nahodneho naslednika vrcholu. Kazdy naslednik
     * vrcholu ma sanci na vyber primoumernou svemu stupni (out degree).
     * Pravdepodobnost vyberu je stanovena nasledovne. Kazdemu nasledniku
     * je prirazeno cislo <i>base</i>, ktere se rovna vystupni vrcholu (out degree)
     * inkrementovano o jedna. Cislo <i>sum</i> je soucet <i>base</i> cisel vsech 
     * nasledniku. Pravdepodobnost vyberu daneho naslednika je pak jeho <i>base</i>
     * cislo vydelene cislem <i>sum</i>.
     *
     * @param u vrchol, jehoz nahodneho naslednika chceme ziskat
     * @return nahodny naslednik zadaneho vrcholu nebo null v pripade, ze zadny
     *     nasledni neexistuje
     */
    private Vertex getRandomNeighbourOutDegreePropability(Vertex u) {
        if (u.getNumberOfNeighbours() == 0) {
            return null;
        }
        List<Vertex> list = u.getNeighbours();
        if (list == null) {
            return null;
        }
        
        // spocitame hodnotu sum = soucet stupnu (out degree) vrcholu nasledniku
        // + celkovy pocet nasledniku
        double sum = 0;
        for (Vertex v : list) {
            sum += (v.getDegree() + 1);
        }
        
        // zvolime nahodne cislo mezi 0.0 - 1.0
        double rnd = random.nextDouble();
        
        // prochazime seznam nasledniku a pokud nahodne zvolene cislo
        // padne do intervalu daneho vrcholu, vratime tento vrchol.
        double increment = 0.0;
        for (Vertex v : list) {
            double vertexProbability = (v.getDegree() + 1) / sum;
            increment += vertexProbability;
            if (rnd <= increment) {
                return v;
            }
        }

        return null;
    }
    
    
    /**
     * Stejny princip jako metoda getRandomNeighbourOutDegreePropability s tim
     * rozdilem, ze "prednost" maji vrcholy s mensim vystupnim stupnem.
     * Tedy opet je pro kazdy vrchol spocitano cislo <i>base</i> ( = vystupni
     * stupen vrcholu inkrementovany o 1). Dale je spocitano cislo <i>sum</i>,
     * coz je soucet vsech cisel <i>base</i>. Pote je pro kazdy vrchol
     * spocitano cislo <i>reverse</i>, ktere je rovno <i>sum</i> - <i>base</i>
     * daneho vrcholu. Nakonec je spocitano cislo <i>reverseSum</i>, ktere
     * je rovno souctu vsech cisel <i>reverse</i>. Pravdepodobnost, ze bude
     * dany vrchol vybran je rovna <i>reverse</i> deleno <i>reverseSum</i>.
     *
     * @param u vrchol, jehoz nahodneho naslednika chceme ziskat
     * @return nahodny naslednik zadaneho vrcholu nebo null v pripade, ze zadny
     *     nasledni neexistuje
     */
    private Vertex getRandomNeighbourReverseOutDegreePropability(Vertex u) {
        if (u.getNumberOfNeighbours() == 0) {
            return null;
        }
        List<Vertex> list = u.getNeighbours();
        if (list == null) {
            return null;
        }
        
        // pokud existuje pouze jeden soused
        if (u.getNumberOfNeighbours() == 1) {
            return list.get(0);
        }
        
        // spocitame hodnotu sum = soucet stupnu (out degree) vrcholu nasledniku
        // + celkovy pocet nasledniku
        double sum = 0;
        for (Vertex v : list) {
            sum += (v.getDegree() + 1);
        }
        
        // spocitame hodnotu reverseSum
        double reverseSum = 0;
        for (Vertex v : list) {
            reverseSum += (sum - (v.getDegree() + 1)); 
                          // reverse = sum - base
        }
        
        // zvolime nahodne cislo mezi 0.0 - 1.0
        double rnd = random.nextDouble();
        
        // prochazime seznam nasledniku a pokud nahodne zvolene cislo
        // padne do intervalu daneho vrcholu, vratime tento vrchol.
        double increment = 0.0;
        for (Vertex v : list) {
            double vertexProbability = (sum - (v.getDegree() + 1)) / reverseSum; // = reverse / reverseSum
            increment += vertexProbability;
            if (rnd <= increment) {
                return v;
            }
        }

        return null;
    }
    
    /**
     * Tato metoda vraci nahodneho naslednika vrcholu. Kazdy naslednik
     * vrcholu ma sanci na vyber primoumernou svemu stupni (in degree).
     * Pravdepodobnost vyberu je stanovena nasledovne. Kazdemu nasledniku
     * je prirazeno cislo <i>base</i>, ktere se rovna vystupni vrcholu (in degree)
     * inkrementovano o jedna. Cislo <i>sum</i> je soucet <i>base</i> cisel vsech 
     * nasledniku. Pravdepodobnost vyberu daneho naslednika je pak jeho <i>base</i>
     * cislo vydelene cislem <i>sum</i>.
     *
     * @param u vrchol, jehoz nahodneho naslednika chceme ziskat
     * @return nahodny naslednik zadaneho vrcholu nebo null v pripade, ze zadny
     *     nasledni neexistuje
     */
    private Vertex getRandomNeighbourInDegreePropability(Vertex u) {
        if (u.getNumberOfNeighbours() == 0) {
            return null;
        }
        List<Vertex> list = u.getNeighbours();
        if (list == null) {
            return null;
        }
        
        // spocitame hodnotu sum = soucet stupnu (in degree) vrcholu nasledniku
        // + celkovy pocet nasledniku
        double sum = 0;
        for (Vertex v : list) {
            sum += (v.getInDegree() + 1);
        }
        
        // zvolime nahodne cislo mezi 0.0 - 1.0
        double rnd = random.nextDouble();
        
        // prochazime seznam nasledniku a pokud nahodne zvolene cislo
        // padne do intervalu daneho vrcholu, vratime tento vrchol.
        double increment = 0.0;
        for (Vertex v : list) {
            double vertexProbability = (v.getInDegree() + 1) / sum;
            increment += vertexProbability;
            if (rnd <= increment) {
                return v;
            }
        }

        return null;
    }
    
    
    /**
     * Stejny princip jako metoda getRandomNeighbourInDegreePropability s tim
     * rozdilem, ze "prednost" maji vrcholy s mensim vstupnim stupnem.
     * Tedy opet je pro kazdy vrchol spocitano cislo <i>base</i> ( = vstupni
     * stupen vrcholu inkrementovany o 1). Dale je spocitano cislo <i>sum</i>,
     * coz je soucet vsech cisel <i>base</i>. Pote je pro kazdy vrchol
     * spocitano cislo <i>reverse</i>, ktere je rovno <i>sum</i> - <i>base</i>
     * daneho vrcholu. Nakonec je spocitano cislo <i>reverseSum</i>, ktere
     * je rovno souctu vsech cisel <i>reverse</i>. Pravdepodobnost, ze bude
     * dany vrchol vybran je rovna <i>reverse</i> deleno <i>reverseSum</i>.
     *
     * @param u vrchol, jehoz nahodneho naslednika chceme ziskat
     * @return nahodny naslednik zadaneho vrcholu nebo null v pripade, ze zadny
     *     nasledni neexistuje
     */
    private Vertex getRandomNeighbourReverseInDegreePropability(Vertex u) {
        if (u.getNumberOfNeighbours() == 0) {
            return null;
        }
        List<Vertex> list = u.getNeighbours();
        if (list == null) {
            return null;
        }
        
        // pokud existuje pouze jeden soused
        if (u.getNumberOfNeighbours() == 1) {
            return list.get(0);
        }
        
        // spocitame hodnotu sum = soucet stupnu (out degree) vrcholu nasledniku
        // + celkovy pocet nasledniku
        double sum = 0;
        for (Vertex v : list) {
            sum += (v.getInDegree() + 1);
        }
        
        // spocitame hodnotu reverseSum
        double reverseSum = 0;
        for (Vertex v : list) {
            reverseSum += (sum - (v.getInDegree() + 1)); 
                          // reverse = sum - base
        }
        
        // zvolime nahodne cislo mezi 0.0 - 1.0
        double rnd = random.nextDouble();
        
        // prochazime seznam nasledniku a pokud nahodne zvolene cislo
        // padne do intervalu daneho vrcholu, vratime tento vrchol.
        double increment = 0.0;
        for (Vertex v : list) {
            double vertexProbability = (sum - (v.getInDegree() + 1)) / reverseSum; // = reverse / reverseSum
            increment += vertexProbability;
            if (rnd <= increment) {
                return v;
            }
        }

        return null;
    }
    
    /**
     * Tato metoda upravi statistiky procentualniho pokryti.
     * Po jednom kroku muze totiz byt procentualni pokryti napr. 10%
     * a po dalsim kroku jiz 30%. Tedy mezi hodnotami 20 a 30 je deset
     * honot, ktere maji prirazeny cas 0. Temto hodnotam se priradi stejy cas
     * jako ma hodnota 20%. Tim dosahneme toho, ze vysledny generovany graf
     * bude "pekny".
     */
    protected void updatePercentageCover() {
        long current = 0;
        for (int p = 0; p < 101; p++) {
            if (percentageCover[p] > current) {
                current = percentageCover[p];
            }            
            if (percentageCover[p] == 0) {
                percentageCover[p] = current;
            }
        }
    }

    /**
     * Vraci informace o tom, zda-li je aktivni rezim vypisu nahodne prochazky
     * 
     * @return informace o tom, zda-li je aktivni rezim vypisu nahodne prochazky
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Nastavuje rezim vypisu nahodne prochazky
     *
     * @param verbose ma-li byt rezim vypisu aktivni ci nikoli
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Vraci interval vypisu nahodne prochazky
     * 
     * @return interval vypisu nahodne prochazky
     */
    public long getVerboseTime() {
        return verboseTime;
    }

    /**
     * Nastavuje interval vypisu nahodne prochazky (implicitni je DEFAULT_VERBOSE_TIME)
     *
     * @param verboseTime interval vypisu nahodne prochazky
     */
    public void setVerboseTime(long verboseTime) {
        this.verboseTime = verboseTime;
    }

    /**
     * Vraci graf prirazeny teto nahodne prochazce
     *
     * @return graf prirazeny teto nahodne prochazce
     */
    public Graph getGraph() {
        return graph;
    }

    /*
     * Vraci mod vyberu naslednika.
     *
     * @return mod vyberu naslednika
     */
    public int getMode() {
        return mode;
    }

    /*
     * Nastavuje mod vyberu naslednika.
     *
     * @param mode mod vyberu naslednika. Moznosti jsou:
     *    <ul>
     *        <li><code>CLASSIC_MODE</code> -- kazdy naslednik ma stejnou
               pravdepodobnost, ze bude vybran</li>
     *
     *      <li><code>OUT_DEGREE_MODE</code> -- sance na vyber je primoumerna
     *      stupni (vystupniho) vrcholu.</li>
     *    </ul>
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * Vraci zda-li je zapnut discover mod
     * @return discover mod
     */
    public boolean isDiscoverMode() {
        return discoverMode;
    }

    /**
     * Nastavuje discover mod
     * @param discoverMode
     */
    public void setDiscoverMode(boolean discoverMode) {
        this.discoverMode = discoverMode;
    }
}
