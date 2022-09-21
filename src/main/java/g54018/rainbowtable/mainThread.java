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
import java.util.concurrent.TimeUnit;

/**
 *
 * @author basile
 */
public class mainThread {
    
    public static int estimatedTimeFor500=0;
    public static int numberOfAllThread = 16;
    public static int numberOfDisplay =0;
    public static int nbrOfGeneration=20;
    private static List<String> listeOfPasswordHash;
    public static int nbrOfDisplayGenerated =0;
    
    public void multiClone(int numberOfClone) throws IOException, ExecutionException, InterruptedException{
        final long start = System.nanoTime();
        for(var i=0;i<numberOfClone;i++){
            final long consumed = System.nanoTime() - start;
            final long total = TimeUnit.NANOSECONDS.toMinutes(consumed);
            if(total>=600){
                System.out.println("----------------------------------------------------------------------------------");
                System.out.println("  The time to make the generation is higher than 10 hours, it finish automaticly ");
                System.out.println("----------------------------------------------------------------------------------");
                break;
            }else{
                multiThreadClone();
            }
        }
    }
    
    
    public void multiThread() throws InterruptedException, ExecutionException, IOException{
        StringBuilder tempHeadTail = new StringBuilder();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        var listeThreadCurrent = new ArrayList<threading>();
        int processorWillBeUse = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor;
        if(processorWillBeUse<8){
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(processorWillBeUse);
        }else{
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbrOfGeneration);
        }

        for(int i=0;i<nbrOfGeneration;i++){
            threading thread = new threading();
            listeThreadCurrent.add(thread);
            Future<?> futur = executor.submit(thread);
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
            tempHeadTail.append(listeThreadCurrent.get(i).getTempHeadTail());
        }
        String file = new File("").getAbsoluteFile().getParent();
        Path passwordFile = Paths.get(file+"\\data\\maRainbowTable.txt");
        Files.deleteIfExists(passwordFile);
        Files.createFile(passwordFile);
        Files.writeString(passwordFile, tempHeadTail, StandardOpenOption.APPEND);
        numberOfDisplay =0;
        System.gc();
    }
    
    private void multiThreadClone() throws IOException, InterruptedException, ExecutionException{
        StringBuilder tempHeadTail = new StringBuilder();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        var listeThreadCurrent = new ArrayList<threading>();
        int processorWillBeUse = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(processorWillBeUse);
        
        for(int i=0;i<nbrOfGeneration;i++){
            threading thread = new threading();
            listeThreadCurrent.add(thread);
            Future<?> futur = executor.submit(thread);
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
            tempHeadTail.append(listeThreadCurrent.get(i).getTempHeadTail());
        }
        
        String file = new File("").getAbsoluteFile().getParent();
        Path passwordFile = Paths.get(file+"\\data\\maRainbowTable.txt");
        Files.writeString(passwordFile, tempHeadTail, StandardOpenOption.APPEND);
        numberOfDisplay =0;
        System.gc();
    }
    
    public long getFileSizeMegaBytes() throws IOException {
          String file = new File("").getAbsoluteFile().getParent();
        Path passwordFile = Paths.get(file+"\\data\\maRainbowTable.txt");
                long bytes = Files.size(passwordFile);
                long kb = bytes / 1024;
                long mb = kb/1024;
		return mb;
    }
    
}
