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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author basile
 */
public class attack implements Runnable {

    private int nbOfReduction = 2500;
    private int SIZE = 16;
    private final String policy = "azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN1234567890";
    private final List<String> listToCrack;
    private StringBuilder finalAttackPswd;

    public attack(List<String> listToCrack) {
        this.listToCrack = listToCrack;
    }

    @Override
    public void run() {
        try {
            attackList();
        } catch (IOException ex) {
            Logger.getLogger(threading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StringBuilder getFinalAttackPswd() {
        return finalAttackPswd;
    }

    public void attackList() throws IOException {
        String file = new File("").getAbsoluteFile().getParent();
        Path passwordFile = Paths.get(file + "\\RainbowTable\\data\\maRainbowTable.txt");
        final DecimalFormat df = new DecimalFormat();
        final DecimalFormatSymbols ds = df.getDecimalFormatSymbols();
        ds.setGroupingSeparator('_');
        df.setDecimalFormatSymbols(ds);
        StringBuilder finalResultAttack = new StringBuilder();
        List<String> passwordToCrack = listToCrack;
        int totalToFind = passwordToCrack.size();
        try (BufferedReader readerPasswordGenerate = Files.newBufferedReader(passwordFile, Charset.forName("UTF-8"));) {
            String currentLinePassword;
            long start = System.nanoTime();
            while (((currentLinePassword = readerPasswordGenerate.readLine()) != null)) {
                String tail = currentLinePassword.substring(currentLinePassword.lastIndexOf(":") + 1);
                String head = currentLinePassword.substring(0, currentLinePassword.indexOf(":"));
                String currentHead = head;
                int i = 0;
                int nbFinded = 0;
                while (i != nbOfReduction || nbFinded != totalToFind) {
                    List<String> listReduction = reductionOfList(listToCrack, nbOfReduction, i);
                    if (listReduction.contains(tail)) {
                        System.out.println("Its founded a match from the RT");
                        for (var j = 0; j < listReduction.size(); j++) {
                            if (listReduction.get(j).equals(tail)) {
                                String elementReductionList = listReduction.get(j);
                                for (var k = 0; k < nbOfReduction; k++) {
                                    String currentHash = digest(head);
                                    if (currentHash.equals(elementReductionList)) {
                                        currentHead = reductionInverse(currentHash, i, head.length(), nbOfReduction);
                                        finalResultAttack.append(currentHead).append(System.lineSeparator());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    i++;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        finalAttackPswd = finalResultAttack;
    }

    private String reductionInverse(String hash, int i, int lgtHead, int nbrOfTotalReduc) {
        int reductionToDo = nbrOfTotalReduc - i;
        String reductionOfHash = null;
        if (i == 0) {
            return reductionFunction(hash, nbrOfTotalReduc, lgtHead);
        } else {
            reductionOfHash = reductionFunction(hash, reductionToDo, lgtHead);
            String finalReduction = null;
            reductionToDo++;
            for (var j = 1; j <= nbrOfTotalReduc; j++, reductionToDo++) {
                String passwordHash = digest(reductionOfHash);
                reductionOfHash = reductionFunction(passwordHash, reductionToDo, lgtHead);
            }
            return reductionOfHash;
        }
    }

    private List<String> reductionOfList(List<String> list, int nbrReductionTotal, int currentReduction) {
        List<String> reductionList = new ArrayList<>();
        int reductionToDo = nbrReductionTotal - currentReduction;
        String reductionOfHash = null;
        if (currentReduction == 0) {
            for (var i = 0; i < list.size(); i++) {
                if (list.get(i).length() > 0) {
                    reductionList.add(reductionFunction(list.get(i), nbrReductionTotal, list.get(i).length()));
                }
            }
        } else {
            for (var i = 0; i < list.size(); i++) {
                if (list.get(i).length() > 0) {
                    reductionOfHash = reductionFunction(list.get(i).toString(), reductionToDo, list.get(i).length());
                    String finalReduction = null;
                    reductionToDo++;
                    for (var j = 1; j <= nbrReductionTotal; j++, reductionToDo++) {
                        String passwordHash = digest(reductionOfHash);
                        reductionOfHash = reductionFunction(passwordHash, reductionToDo, list.get(i).length());
                    }
                    reductionList.add(reductionOfHash);
                }
            }
        }
        return reductionList;
    }

    private int[] toHex(String hash) {
        int[] listHex = new int[SIZE];
        for (var i = 0; i < SIZE; i++) {
            String result = hash.substring(2 * i);
            listHex[i] = (int) Sscanf.scan(result, "%02x", 1)[0];
        }
        return listHex;
    }

    private String reductionFunction(String passwordHash, int generatedNbr, int length) {
        String reductionAll = "";
        int[] bytes = toHex(passwordHash);
        for (var i = 0; i < length; i++) {
            int firstRandom = bytes[(i + generatedNbr) % SIZE];
            reductionAll += policy.charAt(firstRandom % policy.length());
        }
        return reductionAll;
    }

    private String digest(String originalString) {
        return DigestUtils.sha256Hex(originalString);
    }
}
