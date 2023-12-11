// File: RWAnalyzer.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester;

import cz.muni.fi.xstupka.rwtester.graph.Graph;
import cz.muni.fi.xstupka.rwtester.graph.Vertex;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Tato trida analyzuje vysledky dokoncene nahodne prochazky. 
 * Nabizi pouze jednu verejnou statickou metodu <code>analyze</code>, ktera
 * provadi veskerou funkcionalitu. Vystupem teto analyzy je instance
 * tridy <code>RWResult</code>.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class RWAnalyzer {
    
    /**
     * Provadi analyzu dokoncene nahodne prochazky.
     * Parametrem metody je instance tridy <code>RandomWalk</code>,
     * ktera v sobe obsahuje graf, na kterem byla nahodna prochazka
     * spustena. Vystupem analyzy je pak instance tridy <code>RWResult</code>.
     * 
     * @param rw dokoncena nahodna prochazka, jejiz analyza ma byt provedena
     * @return vysledek analyzy v podobe instance tridy <code>RWResult</code>
     * @throws NullPointerException pokud je parametr <code>rw</code> null
     */
    public static RWResult analyze(RandomWalk rw) {
        if (rw == null) {
            throw new NullPointerException("rw parameter is null");
        }
        
        Graph graph = rw.getGraph();
        
        // vytvori novou instanci tridy RWResult a nastavi ji orientaci grafu
        RWResult result = new RWResult(graph.isDirected());
        
        // nastavi zavislost poctu navstiveni vrcholu na stupni vrcholu
        setDegreeVisited(graph, result);
        
        // nastavi zavislost casu prvniho pristupu k vrcholu na stupni vrcholu
        setDegreeTime(graph, result);
        
        // nastavi zavislost pomeru casu prvniho pristupu a vzdalenosti od pocatku
        // nahodne prochazky na stupni vrcholu
        setDegreeTimeLength(graph, result);
        
        // nastavi zavislost poctu navstiveni vrcholu na vzdalenosti od pocatku
        setLengthVisited(graph, result);
        
        // nastavi zavislost casu prvniho navstiveni vrcholu na vzdalenosti od pocatku
        setLengthTime(graph, result);
        
        // nastavi prubeh procentualniho pokryti grafu
        result.setPercentageCover(rw.getPercentageCover());
        
        return result;
    }
    
    /**
     * Nastavi zavislost poctu navstiveni vrcholu na vzdalenosti vrcholu
     * od pocatku nahodne prochazky
     * 
     * @param graph graf, ze ktereho maji byt ziskana potrebna data
     * @param result vysledek, do ktereho maji byt ziskana data ulozena
     */   
    private static void setLengthVisited(Graph graph, RWResult result) {
        Collection<Vertex> vertices = graph.getVertices();
        long maxLength = GraphAnalyzer.getMaxLength(graph);
        
        Map<Long, long[]> data = createDataMap(0, maxLength);
        
        for (Vertex v : vertices) {
            long length = v.getLength();
            long [] foo = data.get(length);

            long value = v.getValueA(); // ve valueA je pocet navstiveni vrcholu
            if (value != 0) {
                foo[0] = foo[0] + v.getValueA();
                foo[1] = foo[1] + 1;
            }
            //data.put(degree, foo);
        }
            
        List<int []> foo = computeResultList(data, 0, maxLength);
        result.setLengthVisited(foo);
    }
    
    /**
     * Nastavi zavislost casu prvniho navstiveni na vzdalenosti vrcholu
     * od pocatku nahodne prochazky
     * 
     * @param graph graf, ze ktereho maji byt ziskana potrebna data
     * @param result vysledek, do ktereho maji byt ziskana data ulozena
     */   
    private static void setLengthTime(Graph graph, RWResult result) {
        Collection<Vertex> vertices = graph.getVertices();
        long maxLength = GraphAnalyzer.getMaxLength(graph);
        
        Map<Long, long[]> data = createDataMap(0, maxLength);
        
        for (Vertex v : vertices) {
            long length = v.getLength();
            long [] foo = data.get(length);

            long value = v.getValueB(); // ve valueB je cas prvniho pristupu k vrcholu
            if (value != RandomWalk.INFINITY) {
                foo[0] = foo[0] + v.getValueB();
                foo[1] = foo[1] + 1;
            }
            //data.put(degree, foo);
        }
            
        List<int []> foo = computeResultList(data, 0, maxLength);
        result.setLengthTime(foo);
    }
    
    /**
     * Nastavi zavislost poctu navstiveni vrcholu na stupni vrcholu
     * 
     * @param graph graf, ze ktereho maji byt ziskana potrebna data
     * @param result vysledek, do ktereho maji byt ziskana data ulozena
     */   
    private static void setDegreeVisited(Graph graph, RWResult result) {        
        if (graph.isDirected()) {
            List<int []> result1 = analyzeDegreeVisited(graph, GraphAnalyzer.DEGREE);
            result.setDegreeVisited(result1);
            
            List<int []> result2 = analyzeDegreeVisited(graph, GraphAnalyzer.IN_DEGREE);
            result.setInDegreeVisited(result2);
            
            List<int []> result3 = analyzeDegreeVisited(graph, GraphAnalyzer.OUT_DEGREE);
            result.setOutDegreeVisited(result3);
        } else {
            List<int []> result1 = analyzeDegreeVisited(graph, GraphAnalyzer.DEGREE);
            result.setDegreeVisited(result1);
        }
    }
    
    /**
     * Nastavi zavislost casu prvniho pristupu k vrcholu na stupni vrcholu
     * 
     * @param graph graf, ze ktereho maji byt ziskana potrebna data
     * @param result vysledek, do ktereho maji byt ziskana data ulozena
     */   
    private static void setDegreeTime(Graph graph, RWResult result) {        
        if (graph.isDirected()) {
            List<int []> result1 = analyzeDegreeTime(graph, GraphAnalyzer.DEGREE);
            result.setDegreeTime(result1);
            
            List<int []> result2 = analyzeDegreeTime(graph, GraphAnalyzer.IN_DEGREE);
            result.setInDegreeTime(result2);
            
            List<int []> result3 = analyzeDegreeTime(graph, GraphAnalyzer.OUT_DEGREE);
            result.setOutDegreeTime(result3);
        } else {
            List<int []> result1 = analyzeDegreeTime(graph, GraphAnalyzer.DEGREE);
            result.setDegreeTime(result1);
        }
    }
    
    /**
     * Nastavi zavislost pomeru casu prvniho pristupu a vzdalenosti od pocatku
     * nahodne prochazky na stupni vrcholu
     * 
     * @param graph graf, ze ktereho maji byt ziskana potrebna data
     * @param result vysledek, do ktereho maji byt ziskana data ulozena
     */    
    private static void setDegreeTimeLength(Graph graph, RWResult result) {        
        if (graph.isDirected()) {
            List<int []> result1 = analyzeDegreeTimeLength(graph, GraphAnalyzer.DEGREE);
            result.setDegreeTimeLength(result1);
            
            List<int []> result2 = analyzeDegreeTimeLength(graph, GraphAnalyzer.IN_DEGREE);
            result.setInDegreeTimeLength(result2);
            
            List<int []> result3 = analyzeDegreeTimeLength(graph, GraphAnalyzer.OUT_DEGREE);
            result.setOutDegreeTimeLength(result3);
        } else {
            List<int []> result1 = analyzeDegreeTimeLength(graph, GraphAnalyzer.DEGREE);
            result.setDegreeTimeLength(result1);
        }
    }
    
    
    /**
     * Provadi analyzu zavislosti poctu navstiveni vrcholu na stupni vrcholu.
     * Vysledkem je seznam dvojic cisel. Prvni cislo udava stupen vrcholu
     * a druhe pak prumerny pocet navstiveni vsech vrcholu majicich tento stupen.
     * 
     * @param graph graf, ze ktereho maji byt ziskana data
     * @param type typ stupne, pro ktery se ma analyza provest, moznosti jsou:
     *     <code>GraphAnalyzer.DEGREE</code>, <code>GraphAnalyzer.IN_DEGREE</code>
     *     a <code>GraphAnalyzer.OUT_DEGREE</code>
     * @return seznam dvojic cisel. Prvni udava pozadovany stupen vrcholu a druhe
     *     pak prumerny pocet navstiveni vsech vrcholu majicich tento stupen
     */
    private static List<int[]> analyzeDegreeVisited(Graph graph, int type) {            
        Collection<Vertex> vertices = graph.getVertices();
        int min = GraphAnalyzer.getMinDegree(graph, type);
        int max = GraphAnalyzer.getMaxDegree(graph, type);
            
        /*  
         * Pro vypocet pouzijeme Map. klicem bude stupen vrcholu a hodnotou
         * bude dvojice soucet vsech poctu navstev a pocet vrcholu majici
         * tento stupen. Na zacatku nastavime tuto dvojici na 0, 0. Kdyz
         * objevime vrchol stupne k, upravime prislusnou dvojici prictenim
         * 1 k poctu vrcholu stupne k a prictem poctu navstem k celku
         * u tohoto vrcholu.
         */
            
        Map<Long, long[]> data = createDataMap(min, max);
            
        for (Vertex v : vertices) {
            long degree = getDegree(graph.isDirected(), v, type);
                        
            long [] foo = data.get(degree);
            /* Pokud je hodnota A rovna nule, potom vrchol ani jednou
             * nebyl navstiven -> pocitali jsme napr. pokryti mene nez 100%
             * nebo jsme pouze hledali cestu mezi 2 vrcholy, kazdopadne
             * pokud valueA == 0, potom takovy vrchol vubec nebereme v uvahu
             * jinak by nam totiz uplne pokazil statistiky !!!!!!!!!
             */            
            long value = v.getValueA(); // ve valueA je pocet navstiveni vrcholu
            if (value != 0) {
                foo[0] = foo[0] + v.getValueA();
                foo[1] = foo[1] + 1;
            }
            //data.put(degree, foo);
        }
            
        return computeResultList(data, min, max);
    }

    /**
     * Provadi analyzu zavislosti casu prvniho navstiveni vrcholu na stupni vrcholu.
     * Vysledkem je seznam dvojic cisel. Prvni cislo udava stupen vrcholu
     * a druhe pak prumerny cas prvniho navstiveni vrcholu majicich tento stupen.
     * 
     * @param graph graf, ze ktereho maji byt ziskana data
     * @param type typ stupne, pro ktery se ma analyza provest, moznosti jsou:
     *     <code>GraphAnalyzer.DEGREE</code>, <code>GraphAnalyzer.IN_DEGREE</code>
     *     a <code>GraphAnalyzer.OUT_DEGREE</code>
     * @return seznam dvojic cisel. Prvni udava pozadovany stupen vrcholu a druhe
     *     pak prumerny cas prvniho navstiveni vrcholu majicich tento stupen.
     */
    private static List<int[]> analyzeDegreeTime(Graph graph, int type) {            
        Collection<Vertex> vertices = graph.getVertices();
        int min = GraphAnalyzer.getMinDegree(graph, type);
        int max = GraphAnalyzer.getMaxDegree(graph, type);
            
        /*  
         * Pro vypocet pouzijeme Map. klicem bude stupen vrcholu a hodnotou
         * bude dvojice soucet vsech casu prvniho pristupu a pocet vrcholu majici
         * tento stupen. Na zacatku nastavime tuto dvojici na 0, 0. Kdyz
         * objevime vrchol stupne k, upravime prislusnou dvojici prictenim
         * 1 k poctu vrcholu stupne k a prictem cas prvniho pristupu k celku
         * u tohoto vrcholu.
         */
            
        Map<Long, long[]> data = createDataMap(min, max);
            
        for (Vertex v : vertices) {
            long degree = getDegree(graph.isDirected(), v, type);
            
            long [] foo = data.get(degree);
            /* Pokud je hodnota B rovna RandomWalk.INFINITY, potom vrchol ani jednou
             * nebyl navstiven -> pocitali jsme napr. pokryti mene nez 100%
             * nebo jsme pouze hledali cestu mezi 2 vrcholy, kazdopadne
             * pokud valueB == RandomWalk.INFINITY, potom takovy vrchol vubec nebereme v uvahu
             * jinak by nam totiz uplne pokazil statistiky !!!!!!!!!
             */            
            long value = v.getValueB(); // ve valueB je cas prvniho pristupu k vrcholu
            if (value != RandomWalk.INFINITY) {
                foo[0] = foo[0] + v.getValueB();
                foo[1] = foo[1] + 1;
            }
            //data.put(degree, foo);
        }
            
        return computeResultList(data, min, max);
    }
    
    /**
     * Provadi analyzu zavislosti pomeru casu prvniho navstiveni vrcholu a vzdalenosti
     * vrcholu od pocatku nahodne prochazky na stupni vrcholu.
     * Vysledkem je seznam dvojic cisel. Prvni cislo udava stupen vrcholu
     * a druhe pak pomer casu prvniho navstiveni vrcholu majicich tento stupen
     * a vzdalenosti techto vrcholu od pocatku nahodne prochazky.
     * 
     * @param graph graf, ze ktereho maji byt ziskana data
     * @param type typ stupne, pro ktery se ma analyza provest, moznosti jsou:
     *     <code>GraphAnalyzer.DEGREE</code>, <code>GraphAnalyzer.IN_DEGREE</code>
     *     a <code>GraphAnalyzer.OUT_DEGREE</code>
     * @return seznam dvojic cisel. Prvni udava stupen vrcholu a druhe pak
     *     pomer casu prvniho navstiveni vrcholu majicich tento stupen a vzdalenosti
     *     techto vrcholu od pocatku nahodne prochazky.
     */
    private static List<int[]> analyzeDegreeTimeLength(Graph graph, int type) {            
        Collection<Vertex> vertices = graph.getVertices();
        int min = GraphAnalyzer.getMinDegree(graph, type);
        int max = GraphAnalyzer.getMaxDegree(graph, type);
            
        /*
         *  tentokrat je dvojice soucet casu prvniho pristupu
         *  a soucet vzdelenosti od pocatku
         */
        
        Map<Long, long[]> data = createDataMap(min, max);
            
        for (Vertex v : vertices) {
            long degree = getDegree(graph.isDirected(), v, type);
            
            long [] foo = data.get(degree);
            /* Pokud je hodnota B rovna RandomWalk.INFINITY, potom vrchol ani jednou
             * nebyl navstiven -> pocitali jsme napr. pokryti mene nez 100%
             * nebo jsme pouze hledali cestu mezi 2 vrcholy, kazdopadne
             * pokud valueB == RandomWalk.INFINITY, potom takovy vrchol vubec nebereme v uvahu
             * jinak by nam totiz uplne pokazil statistiky !!!!!!!!!
             */            
            long value = v.getValueB(); // ve valueB je cas prvniho pristupu k vrcholu
            if (value != RandomWalk.INFINITY) {
                foo[0] = foo[0] + v.getValueB();
                foo[1] = foo[1] + v.getLength(); // vzdalenost vrcholu od pocatku nahodne prochazky
            }
            //data.put(degree, foo);
        }

        return computeResultList(data, min, max);
    }
    
    /**
     * Vytvori novou kolekci typu Map a nastavi ji implicitni data.
     * Klicem budou hodnoty v rozsahu parametru <code>min</code>
     * a <code>max</code>. Hodnotami pak dvojice integeru nastavenych
     * na hodnoty 0.
     * 
     * @param min pocatek klicu v kolekci
     * @param max konec klicu v kolekci
     * @param a implicitni hodnota prvniho cisla z dvojice hodnot
     * @param b implicitni hodnota druheho cisla z dvojice hodnot
     * @return kolekce Map s nastavenymi daty
     */
    private static Map<Long, long[]> createDataMap(long min, long max) {
        Map<Long, long[]> data = new HashMap<Long, long[]>();
        for (long p = min; p <= max; p++) {
        long [] foo = new long[2];
            foo[0] = 0;
            foo[1] = 0;
            data.put(p, foo);
        }
        return data;
    }
    
    /**
     * Tato metoda vraci pozadovany stupen daneho vrcholu
     *
     * @param directed jedna-li se o vrchol patrici orientovanemu grafu
     * @param vertex vrchol, na jehoz stupen se ptame
     * @param pozadovany stupen vrcholu, moznosti jsou:
     *     <code>GraphAnalyzer.DEGREE</code>, <code>GraphAnalyzer.IN_DEGREE</code>
     *     a <code>GraphAnalyzer.OUT_DEGREE</code>
     * @return pozadovany stupen daneho vrcholu
     */
    private static int getDegree(boolean directed, Vertex vertex, int type) {
        int degree = 0;
        if (directed) {
            if (type == GraphAnalyzer.DEGREE) {
                degree = vertex.getDegree() + vertex.getInDegree();
            } else if (type == GraphAnalyzer.IN_DEGREE) {
                degree = vertex.getInDegree();
            } else { // GraphAnalyzer.OUT_DEGREE
                degree = vertex.getDegree();
            }
        } else {
            degree = vertex.getDegree();
        }
        return degree;
    }
    
    /**
     * Ze zadanych dat v kolekci vypocita prumer.
     * Vtupem metody je kolekce Map, ktera jako klic obsahuje stupne vrcholu a jako
     * hodnotu dvoji integeru. Tato dvojice muze byt napriklad pocet vrcholu majicich
     * dany stupen a soucet poctu navstiveni techto vrcholu. Metoda vraci seznam
     * dvojic cisel. Prvni cislo udava stupen vrcholu a druhe pak prumerny vysledek
     * z dvojice cisel, ktere jsou hodnotami zadane kolekce.
     *
     * @param data kolekce Map obsahujici data pro prevod
     * @mix minimalni stupen v grafu
     * @max maximalni stupen v grafu
     * @return seznam dvojic cisel. Prvni cislo udava stupen vrcholu a druhe pak 
     *     prumerny vysledek z dvojice cisel, ktere jsou hodnotami zadane kolekce.
     */
    
    private static List<int []> computeResultList(Map<Long, long[]> data, long min, long max) {
        List<int []> result = new LinkedList<int []>();
        for (long p = min; p <= max; p++) {
            long [] foo = data.get(p);
            if (foo[1] != 0) { // proti deleni nulou
                int [] bar = new int[2];
                bar[0] = (int) p;
                bar[1] = (int) ((double) foo[0] / (double) foo[1]); // prumer
                result.add(bar);
            }
        }
        return result;
    }
}
