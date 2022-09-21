/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package g54018.rainbowtable;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author basile
 */
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please choese between 2 option : \n * 1 for generate a file with head and tail with 2500 reduction function \n * 2 for attack the file with the already generated rainbow table");
        System.out.println("If you enter a bad letter it will stop the programm so please write the good choice");
        System.out.println("The file will be stored into target");
        int choice = sc.nextInt();
        if(choice==1000){
            generatePasswordToCrack();
        }else if(choice==1){
            try {
                generateHeadTail();
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(choice==2){
            try {
                attackPassword();
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void generateHeadTail() throws InterruptedException, ExecutionException, IOException{
        mainThread mt = new mainThread();
        System.out.println("For a optimal result of the generating password, please close all the programm open. \n\t Your desktop will may be make a little noise but don't worry. \n BTW : let the desktop charge on sector and doe not close desktop.");
        mt.multiThread();
        long sizeFileAfterAllThread = mt.getFileSizeMegaBytes();
        int necessaryCloneFor3Gb =0;
        while(sizeFileAfterAllThread<3_000){
            sizeFileAfterAllThread += sizeFileAfterAllThread;
            necessaryCloneFor3Gb++;
        }
        if(necessaryCloneFor3Gb<=1){
            necessaryCloneFor3Gb=30;
        }
        System.out.println("\n You need at least " + necessaryCloneFor3Gb +"for 3GB of head and tail");
        mt.multiClone(necessaryCloneFor3Gb);
        System.out.println("----------------------------");
        System.out.println("  The generation is finish  ");
        System.out.println("----------------------------");
        
        System.out.println("TOTALEMENT FINI");
    }
    
    private static void generatePasswordToCrack(){
        generatePassword gp = new generatePassword();
        try {
            gp.createPassword(100);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void attackPassword() throws InterruptedException, ExecutionException, IOException{
        attackMain am = new attackMain();
        am.listPasswordToCrack();
        am.multiThread(am.getNbrOfGeneration(), 5);
        int threadRemaining = am.getNbOfLineInFilePassword() - (am.getNbrOfGeneration()*5);
        if(threadRemaining>=16){
            am.multiThread(am.getNbrOfGeneration(), 5);
        }else{
            am.multiThread(threadRemaining, 5);
        }
        System.out.println("");
        System.out.println("Their is the list of the password which are cracked : ");
        System.out.println(am.getFinalAttackList());
    }
}
