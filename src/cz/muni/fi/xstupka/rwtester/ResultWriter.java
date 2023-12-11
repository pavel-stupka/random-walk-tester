// File: ResultWriter.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Tato trida slouzi k vypisu vysledku analyzy nahodne prochazky do textovych
 * souboru.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class ResultWriter {

    private String fileTemplate;
    
    /** 
     * Vytvori novou instanci tridy ResultWriter
     *
     * @param fileTemplate sablona pro vysledne textove soubory
     * @throw NullPointerException pokud je parametr <code>fileTemplate</code> null
     */
    public ResultWriter(String fileTemplate) {
        if (fileTemplate == null) {
            throw new NullPointerException("fileTemplate parameter is null");
        }
        this.fileTemplate = fileTemplate;
    }
    
    /**
     * Zapise vysledna data do souboru.
     *
     * @param result soubor vyslednych dat, ktera maji byt zapsana
     * @throw NullPointerException pokud je parametr <code>result</code> null
     * @throw IOException pokud dojde k chybe pri praci se soubory
     */
    public void write(RWResult result) throws IOException {
        if (result == null) {
            throw new NullPointerException("result parameter is null");
        }
        
        File degreeVisitedFile = new File(fileTemplate + "_degree_visited.txt");
        File degreeTimeFile = new File(fileTemplate + "_degree_time.txt");        
        File coverageFile = new File(fileTemplate + "_coverage.txt");
        File degreeTimeLengthFile = new File(fileTemplate + "_degree_time_length.txt");
        
        File lengthVisitedFile = new File(fileTemplate + "_length_visited.txt");
        File lengthTimeFile = new File(fileTemplate + "_length_time.txt");
        
        writeData(result.getDegreeVisited(), degreeVisitedFile);
        writeData(result.getDegreeTime(), degreeTimeFile);
        writeData(result.getPercentageCover(), coverageFile);
        writeData(result.getDegreeTimeLength(), degreeTimeLengthFile);
        
        writeData(result.getLengthVisited(), lengthVisitedFile);
        writeData(result.getLengthTime(), lengthTimeFile);
        
        // vypise data, ktera jsou specificka pouze pro orientovane grafy
        if (result.isDirected()) {
            writeDirected(result);
        }
    }
    
    /**
     * Zapise data, ktera jsou specificka pouze pro orientovane grafy
     *
     * @param result soubor vyslednych dat, ktera maji byt zapsana
     * @throw NullPointerException pokud je parametr <code>result</code> null
     */
    private void writeDirected(RWResult result) throws IOException {
        if (result == null) {
            throw new NullPointerException("result parameter is null");
        }
        
        File inDegreeVisitedFile = new File(fileTemplate + "_in_degree_visited.txt");
        File outDegreeVisitedFile = new File(fileTemplate + "_out_degree_visited.txt");
        
        File inDegreeTimeFile = new File(fileTemplate + "_in_degree_time.txt");
        File outDegreeTimeFile = new File(fileTemplate + "_out_degree_time.txt");
        
        File inDegreeTimeLengthFile = new File(fileTemplate + "_in_degree_time_length.txt");
        File outDegreeTimeLengthFile = new File(fileTemplate + "_out_degree_time_length.txt");
        
        writeData(result.getInDegreeVisited(), inDegreeVisitedFile);
        writeData(result.getOutDegreeVisited(), outDegreeVisitedFile);
        
        writeData(result.getInDegreeTime(), inDegreeTimeFile);
        writeData(result.getOutDegreeTime(), outDegreeTimeFile);
        
        writeData(result.getInDegreeTimeLength(), inDegreeTimeLengthFile);
        writeData(result.getOutDegreeTimeLength(), outDegreeTimeLengthFile);
    }
    
    /**
     * Zapise data ze zadaneho seznamu do souboru
     *
     * @param data seznam dat k zapsani
     * @param file soubor, do ktereho se maji data zapsat
     * @throw IOException pri I/O chybe, ktera muze nastat pri praci se souborem
     */
    private void writeData(List<int[]> data, File file) throws IOException {
        FileWriter out = new FileWriter(file);
        
        for (int[] foo : data) {
            String line = foo[0] + "    " + foo[1] + "\n";
            out.write(line);
        }
        
        out.close();
    }
    
    /**
     * Zapise data ze zadaneho seznamu do souboru
     *
     * @param data seznam dat k zapsani
     * @param file soubor, do ktereho se maji data zapsat
     * @throw IOException pri I/O chybe, ktera muze nastat pri praci se souborem
     */
    private void writeData(long[] data, File file) throws IOException {
        FileWriter out = new FileWriter(file);
        
        for (int p = 0; p < 101; p++) {
            String line = p + "    " + data[p] + "\n";
            out.write(line);
        }
        
        out.close();
    }
}
