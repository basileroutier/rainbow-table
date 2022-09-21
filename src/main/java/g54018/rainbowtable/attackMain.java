/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package g54018.rainbowtable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author basile
 */
public class attackMain {
    
    private static List<String> listOfPasswordHash;
    private static int numberAlreadyGet =0;
    private static int nbrOfGeneration=16;
    private static int nbOfLineInFilePassword;
    private static StringBuilder finalAttackList = new StringBuilder();

    public List<String> getListOfPasswordHash() {
        return listOfPasswordHash;
    }

    public int getNumberAlreadyGet() {
        return numberAlreadyGet;
    }

    public int getNbrOfGeneration() {
        return nbrOfGeneration;
    }

    public int getNbOfLineInFilePassword() {
        return nbOfLineInFilePassword;
    }

    public StringBuilder getFinalAttackList() {
        return finalAttackList;
    }
    
    
    
    
    private List listOfPasswordToCrack(int numberOfPasswordToGet, int start){
        List<String> listPassword = new ArrayList<>();
        for(var i=start;i<(start+numberOfPasswordToGet);i++){
            listPassword.add(listOfPasswordHash.get(i));
        }
        return listPassword;
    }
    
    public void multiThread(int nbrOfThread, int nbrOfLinePerList) throws InterruptedException, ExecutionException, IOException{
        List<Future<?>> futures = new ArrayList<Future<?>>();
        var listeThreadCurrent = new ArrayList<attack>();
        int processorWillBeUse = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(processorWillBeUse);
        
        for(int i=0;i<nbrOfThread;i++){
            var list = listOfPasswordToCrack(nbrOfLinePerList,numberAlreadyGet);
            numberAlreadyGet += nbrOfLinePerList;
            attack atk = new attack(list);
            listeThreadCurrent.add(atk);
            Future<?> futur = executor.submit(atk);
            futures.add(futur);
        }
        
        executor.shutdown();

        for(Future<?> future : futures){
            future.get();
        }
        
        boolean allDone = true;
        for(Future<?> future : futures){
            allDone &= future.isDone(); 
        }
        for(var i=0;i<listeThreadCurrent.size();i++){
            if(listeThreadCurrent.get(i).getFinalAttackPswd().length()<=1){
                finalAttackList.append("No correspondance has been finded for the " + nbrOfLinePerList + " number of lines").append(System.lineSeparator());
            }else{
                finalAttackList.append(listeThreadCurrent.get(i).getFinalAttackPswd());
            }
        }
        System.gc();
    }
    
     public void listPasswordToCrack() throws IOException {
         String file = new File("").getAbsoluteFile().getParent();
        Path passwordFile = Paths.get(file+"\\RainbowTable\\data\\tryToCrackMe.txt");
        List<String> temporary = new ArrayList<String>();
        int numberOfLineInFile=0;
        try (BufferedReader readerPasswordGenerate = Files.newBufferedReader(passwordFile, Charset.forName("UTF-8"));) {
            String currentLinePassword;
            while (((currentLinePassword = readerPasswordGenerate.readLine()) != null)) {
                temporary.add(currentLinePassword);
                numberOfLineInFile++;
            }
        }
        nbOfLineInFilePassword = numberOfLineInFile;
        listOfPasswordHash = temporary;
    }
}
