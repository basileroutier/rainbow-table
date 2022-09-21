/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package g54018.rainbowtable;

import java.io.IOException;
import java.text.DecimalFormat;
import org.apache.commons.codec.digest.DigestUtils;
import java.text.DecimalFormatSymbols;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author basile
 */
public class threading implements Runnable{
    private StringBuilder headTail;
    private int lengthPassword = 6;
    private int SIZE=16;
    private final String policy = "azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN1234567890";
    private final Random random = new Random();
    
    public threading() {
        this.headTail = new StringBuilder();
    }
    
    private StringBuilder randomPassword() {
        final StringBuilder password = new StringBuilder();
        for (int character = 0; character < lengthPassword; ++character) {
            password.append(policy.charAt(random.nextInt(policy.length() - 0) + 0));
        }
        return password;
    }

    private void RT() throws IOException {
        final DecimalFormat df = new DecimalFormat();
        final DecimalFormatSymbols ds = df.getDecimalFormatSymbols();
        ds.setGroupingSeparator('_');
        df.setDecimalFormatSymbols(ds);
        final int numberOfReductionFunctionToGenerate = 2500;
        final int numberOfGeneratedPassword = 50_000;
        final int chunkSize = 25_000;
        int generatedPass = 0;
        int chunk = 0;
        StringBuilder firstPassword;
        
        final long start = System.nanoTime();
        
        while (generatedPass < numberOfGeneratedPassword) {
            StringBuilder tempHeadTail = new StringBuilder();
            for (int index = chunk * chunkSize; index < (chunk + 1) * chunkSize && index < numberOfGeneratedPassword; ++index) {
                mainThread.nbrOfDisplayGenerated=0;
                firstPassword = randomPassword();
                String lastPassword = tailPassword(firstPassword.toString(), numberOfReductionFunctionToGenerate);
                tempHeadTail.append(firstPassword).append(":").append(lastPassword).append(System.lineSeparator());
                ++generatedPass;
                if (generatedPass % 500 == 0) {
                    if(generatedPass==500 && mainThread.numberOfDisplay==0){
                        long totalFor500 = (long) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                        double totalToSecond = totalFor500/1000;
                        double totalForAllGenerated = (totalToSecond/60)*100; // TO 50_000
                        int numberOfAllGenerated = 50_000 * 16; // FOR ALL the programm number of generated
                        System.out.println("Estimated end for " + numberOfAllGenerated + " it will take : " + (int) totalForAllGenerated + " minutes for all the thread");
                        mainThread.numberOfDisplay = 1;
                    }
                    if(mainThread.nbrOfDisplayGenerated==0){
                        System.out.printf(
                            "%s / %s%n",
                            df.format(generatedPass),
                            df.format(numberOfGeneratedPassword));
                        mainThread.nbrOfDisplayGenerated++;
                    }
                }
            }
            chunk++;
            headTail.append(tempHeadTail);
        }
        final long consumed = System.nanoTime() - start;
        System.out.printf("Done. Took %d seconds%n", TimeUnit.NANOSECONDS.toSeconds(consumed));
    }
    
    @Override
    public void run() {
        try {
            RT();
        } catch (IOException ex) {
            Logger.getLogger(threading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public StringBuilder getTempHeadTail(){
        return headTail;
    }
    
     private String tailPassword(String password, int nbrOfReduction){
        String passwordHash = digest(password);
        String passwordCurrent = password;
        for(var i=1;i<nbrOfReduction;i++){
            passwordCurrent = reductionFunction(passwordHash, i, passwordCurrent.length());
            passwordHash = digest(passwordCurrent);
        }
        passwordCurrent = reductionFunction(passwordHash, 1, passwordCurrent.length());
        return passwordCurrent;
    } 
    
    private int[] toHex(String hash){
        int[] listHex = new int[SIZE];
        for(var i=0;i<SIZE;i++){
            String result = hash.substring(2*i);
            listHex[i] = (int) Sscanf.scan(result, "%02x", 1)[0];
        }
        return listHex;
    }
    
    private String reductionFunction(String passwordHash, int generatedNbr,int length) {
        String reductionAll = "";
        int[] bytes = toHex(passwordHash);
        for(var i=0;i<length;i++){
            int firstRandom = bytes[(i+generatedNbr)%SIZE];
            reductionAll += policy.charAt(firstRandom%policy.length());
        }
        return reductionAll;
    }

    private String digest(String originalString) {
        return DigestUtils.sha256Hex(originalString);
    }
    
}
