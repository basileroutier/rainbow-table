/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package g54018.rainbowtable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author basile
 */
public class generatePassword {
    
    private final String policy = "azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN1234567890";
    private final Random random = new Random();
    
    
    private String digest(String input) {
        return DigestUtils.sha256Hex(input);
    }
    
    public void createPassword(int numberOfPasswordsToGenerate) throws IOException{
        String file = new File("").getAbsoluteFile().getParent();
        Path passwordFile = Paths.get(file+"\\data\\tryToCrackMe.txt");
        Files.deleteIfExists(passwordFile);
        Files.createFile(passwordFile);
        final DecimalFormat df = new DecimalFormat();
        final DecimalFormatSymbols ds = df.getDecimalFormatSymbols();
        ds.setGroupingSeparator('_');
        df.setDecimalFormatSymbols(ds);
        int chunkSize = numberOfPasswordsToGenerate;
        int generated = 0;
        int chunk = 0;

        long start = System.nanoTime();
        while (generated < numberOfPasswordsToGenerate) {
            final StringBuilder passwords = new StringBuilder();
            for (int index = chunk * chunkSize; index < (chunk + 1) * chunkSize && index < numberOfPasswordsToGenerate; ++index) {
                final StringBuilder password = new StringBuilder();
                for (int character = 0; character < 6; ++character) {
                    password.append(policy.charAt(random.nextInt(policy.length() - 0) + 0));
                }
                passwords.append(digest(password.toString())).append(System.lineSeparator());
                ++generated;
            }
            ++chunk;
            Files.writeString(passwordFile, passwords.toString(), StandardOpenOption.APPEND);
        }
        final long consumed = System.nanoTime() - start;
        System.out.printf("Done. Took %d seconds%n", TimeUnit.NANOSECONDS.toSeconds(consumed));
    }
}
