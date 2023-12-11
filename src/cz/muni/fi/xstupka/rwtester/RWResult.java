// File: RWResult.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tato trida obsahuje vysledky analyzy nahodne prochazky.
 * Kazda nahodna prochazka produkuje data, ktera jsou nasledne analyzovana
 * tridou <code>RWAnalyzer</code>. Trida <code>RWAnalyzer</code> obsahuje
 * pouze jednu verejnou metodu <code>analyze</code>, ktera vraci instanci
 * prave tridy <code>RWResult</code>. Trida <code>RWResult</code> take
 * umi vytvorit prumer z nekolika svych instanci. Toto je vhodne pouzit
 * napr. v pripade, ze nahodnou prochazku spoustime nekolikrat a chceme
 * ziskat prumer ze vsech mereni.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class RWResult {
    
    private boolean directed;
    
    /*
     *   prumerny         |
     *   pocet navstiveni |
     *   vrcholu          |
     *   s timto stupnem  |
     *                    |________________ stupen vrcholu
     */
    private List<int[]> degreeVisited;
    private List<int[]> inDegreeVisited;
    private List<int[]> outDegreeVisited;
    
    /*
     *   prumerny         |
     *   cas prvniho      |
     *   objeveni vrcholu |
     *   s timto stupnem  |
     *                    |________________ stupen vrcholu
     */
    private List<int[]> degreeTime;
    private List<int[]> inDegreeTime;
    private List<int[]> outDegreeTime;
    
    /*
     *   pomer casu                   |
     *   prvniho navstiveni           |
     *   vrcholu s timto stupnem      |
     *   -------------------------    |
     *   a vzdalenosti tohoto vrcholu |
     *   od pocatku nahodne prochazky |________________ stupen vrcholu
     */
    private List<int[]> degreeTimeLength;
    private List<int[]> inDegreeTimeLength;
    private List<int[]> outDegreeTimeLength;
    
    
    /*                              |
     * prumerny pocet navstiveni    |
     * vsech vrcholu, ktere jsou    |
     * stejne vzdalene od pocatku   |
     * nahodne prochazky            |___________________ vzdalenost od pocatku
     */
    private List<int[]> lengthVisited;
    
    /*                                |
     * prumerny cas prvniho           |
     * navstiveni vrcholu, ktere jsou |
     * stejne vzdalene od pocatku     |
     * nahodne prochazky              |___________________ vzdalenost od pocatku
     */
    private List<int[]> lengthTime;

    
    /* 
     * procentualni pokryti grafu. seznam ma velikost 101 prvku, tedy indexy 0 .. 100
     * index udava procentualni pokryti grafu a hodnota pak cas, ve kterem bylo
     * tohoto pokryti dosazeno
     */    
    private long[] percentageCover;
    

    /**
     * Vytvori novou instanci tridy RWResult
     *
     * @param directed jedna-li se o vysledek analyzy orientovaneho 
     *     ci neorientovaneho grafu
     */
    public RWResult(boolean directed) {
        this.setDirected(directed);
    }            
    
    /**
     * Vytvori novou instanci tridy RWResult jako prumer vsech zadanych vysledku
     * 
     * @throws NullPointerException pokud je parametr <code>results</code> null
     * @throws IllegalArgumentException pokud je zadany seznam prazdny
     */
    public RWResult(RWResult[] results) {
        if (results == null) {
            throw new NullPointerException();
        }
        if (results.length == 0) {
            throw new IllegalArgumentException();
        }
        this.setDirected(results[0].isDirected());
        
        Map<Integer, Double> mapDegreeVisited = new HashMap<Integer, Double>();
        Map<Integer, Double> mapInDegreeVisited = new HashMap<Integer, Double>();
        Map<Integer, Double> mapOutDegreeVisited = new HashMap<Integer, Double>();
        
        Map<Integer, Double> mapDegreeTime = new HashMap<Integer, Double>();
        Map<Integer, Double> mapInDegreeTime = new HashMap<Integer, Double>();
        Map<Integer, Double> mapOutDegreeTime = new HashMap<Integer, Double>();
        
        Map<Integer, Double> mapDegreeTimeLength = new HashMap<Integer, Double>();
        Map<Integer, Double> mapInDegreeTimeLength = new HashMap<Integer, Double>();
        Map<Integer, Double> mapOutDegreeTimeLength = new HashMap<Integer, Double>();
        
        Map<Integer, Double> mapLengthVisited = new HashMap<Integer, Double>();
        Map<Integer, Double> mapLengthTime = new HashMap<Integer, Double>();
        
        
        for (int p = 0; p < results.length; p++) {
            updateMap(mapDegreeVisited, results[p].getDegreeVisited(), results.length);
            updateMap(mapDegreeTime, results[p].getDegreeTime(), results.length);
            updateMap(mapDegreeTimeLength, results[p].getDegreeTimeLength(), results.length);
            updateMap(mapLengthVisited, results[p].getLengthVisited(), results.length);
            updateMap(mapLengthTime, results[p].getLengthTime(), results.length);
            
            
            if (directed) {
                updateMap(mapInDegreeVisited, results[p].getInDegreeVisited(), results.length);
                updateMap(mapOutDegreeVisited, results[p].getOutDegreeVisited(), results.length);
                
                updateMap(mapInDegreeTime, results[p].getInDegreeTime(), results.length);
                updateMap(mapOutDegreeTime, results[p].getOutDegreeTime(), results.length);
                
                updateMap(mapInDegreeTimeLength, results[p].getInDegreeTimeLength(), results.length);
                updateMap(mapOutDegreeTimeLength, results[p].getOutDegreeTimeLength(), results.length);
            }
            
            
        }
        
        //-------------------------------------------------------------------
        setDegreeVisited(mapToList(mapDegreeVisited));
        setDegreeTime(mapToList(mapDegreeTime));
        setDegreeTimeLength(mapToList(mapDegreeTimeLength));
        setLengthVisited(mapToList(mapLengthVisited));
        setLengthTime(mapToList(mapLengthTime));
        
        if (directed) {
            setInDegreeVisited(mapToList(mapInDegreeVisited));
            setOutDegreeVisited(mapToList(mapOutDegreeVisited));
            
            setInDegreeTime(mapToList(mapInDegreeTime));
            setOutDegreeTime(mapToList(mapOutDegreeTime));
            
            setInDegreeTimeLength(mapToList(mapInDegreeTimeLength));
            setOutDegreeTimeLength(mapToList(mapOutDegreeTimeLength));
        }
        //-------------------------------------------------------------------
        
        // spocita prumer procentualniho pokryti
        double[] foo = new double[101];
        for (int p = 0; p < 101; p++) {
            foo[p] = 0;
        }
        
        for (int p = 0; p < results.length; p++) {
            for (int q = 0; q < 101; q++) {
                double current = (double) results[p].getPercentageCover()[q];
                foo[q] += (current / (double) results.length);
            }
        }
        
        percentageCover = new long[101];
        for (int p = 0; p < 101; p++) {
            percentageCover[p] = (long) foo[p];
        }
    }
    
    /**
     * Upravi kolekci Map podle zadaneho seznamu.
     * Kolekce je vytvarena jako prumer postupne zadavanych seznamu. Pokud bychom
     * scitali jednotliva data se vsech seznamu a pak je teprve delili poctem seznamu
     * mohli bychom presahnout rozsah promennych. Neni to sice az tak pravdepodobne, 
     * ale mohlo by se to stat. Proto rovnou pricitame cast, kterou se dany seznam
     * podili na celkovem prumeru. Toto provadime tak, ze pricitana data daneho
     * seznamu hned delime poctem seznamu. Tedy rovnou tvorime prumer.
     *
     * @param map kolekce Map, kterou upravujeme zadanym seznamem
     * @param list seznam, podle ktereho upravujeme kolekci
     * @param count celkovy pocet seznamu, kterymi budeme upravovat kolekci
     */
    private void updateMap(Map<Integer, Double> map, List<int[]> list, int count) {
        for (int[] foo : list) {            
            
            int degree = foo[0];
            int value = foo[1];
            
            if (map.containsKey(degree)) {
                double mapValue = map.get(degree);
                mapValue = mapValue + ((double) value / (double) count);
                map.put(degree, mapValue);
            } else {
                map.put(degree, ((double) value / (double) count));
            }
        }
    }
    
    /**
     * Prevede kolekci Map na seznam dvojic integeru
     *
     * @param map kolekce, ktera ma byt prevedena na seznam
     * @return vysledny prevedeny seznam
     */
    private List<int[]> mapToList(Map<Integer, Double> map) {
        Set<Integer> keys = map.keySet();
        List<int[]> list = new LinkedList<int[]>();
        
        for (Integer i : keys) {
            int[] foo = new int[2];
            foo[0] = i;
            double bar = map.get(i);
            foo[1] = (int) bar;
            list.add(foo);
        }
        return list;
    }
    
    // ---------------- GENEROVANO REFACTORINGEM ----------------
    
    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public List<int[]> getDegreeVisited() {
        return degreeVisited;
    }

    public void setDegreeVisited(List<int[]> degreeVisited) {
        this.degreeVisited = degreeVisited;
    }

    public List<int[]> getInDegreeVisited() {
        return inDegreeVisited;
    }

    public void setInDegreeVisited(List<int[]> inDegreeVisited) {
        this.inDegreeVisited = inDegreeVisited;
    }

    public List<int[]> getOutDegreeVisited() {
        return outDegreeVisited;
    }

    public void setOutDegreeVisited(List<int[]> outDegreeVisited) {
        this.outDegreeVisited = outDegreeVisited;
    }

    public List<int[]> getDegreeTime() {
        return degreeTime;
    }

    public void setDegreeTime(List<int[]> degreeTime) {
        this.degreeTime = degreeTime;
    }

    public List<int[]> getInDegreeTime() {
        return inDegreeTime;
    }

    public void setInDegreeTime(List<int[]> inDegreeTime) {
        this.inDegreeTime = inDegreeTime;
    }

    public List<int[]> getOutDegreeTime() {
        return outDegreeTime;
    }

    public void setOutDegreeTime(List<int[]> outDegreeTime) {
        this.outDegreeTime = outDegreeTime;
    }

    public long[] getPercentageCover() {
        return percentageCover;
    }

    public void setPercentageCover(long[] percentageCover) {
        this.percentageCover = percentageCover;
    }

    public List<int[]> getDegreeTimeLength() {
        return degreeTimeLength;
    }

    public void setDegreeTimeLength(List<int[]> degreeTimeLength) {
        this.degreeTimeLength = degreeTimeLength;
    }

    public List<int[]> getInDegreeTimeLength() {
        return inDegreeTimeLength;
    }

    public void setInDegreeTimeLength(List<int[]> inDegreeTimeLength) {
        this.inDegreeTimeLength = inDegreeTimeLength;
    }

    public List<int[]> getOutDegreeTimeLength() {
        return outDegreeTimeLength;
    }

    public void setOutDegreeTimeLength(List<int[]> outDegreeTimeLength) {
        this.outDegreeTimeLength = outDegreeTimeLength;
    }

    public List<int[]> getLengthVisited() {
        return lengthVisited;
    }

    public void setLengthVisited(List<int[]> lengthVisited) {
        this.lengthVisited = lengthVisited;
    }

    public List<int[]> getLengthTime() {
        return lengthTime;
    }

    public void setLengthTime(List<int[]> lengthTime) {
        this.lengthTime = lengthTime;
    }
}
