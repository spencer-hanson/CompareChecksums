package net.shadux.checksum;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by spencerh on 6/3/2016.
 */
public class CompareChecksum {

    public File file1;
    public File file2;

    public CompareChecksum(String file1, String file2, boolean default_val) throws FileNotFoundException {
        if(default_val) {
            this.file1 = new File(file1 + File.separator + "checksums.txt");
            this.file2 = new File(file2 + File.separator + "checksums.txt");
        } else {
            this.file1 = new File(file1);
            this.file2 = new File(file2);
        }


        if(!this.file1.exists()) { throw new FileNotFoundException("File not found: " + file1 + "/checksums.txt"); }
        if(!this.file2.exists()) { throw new FileNotFoundException("File not found: " + file2 + "/checksums.txt"); }
    }

    public void run() throws Exception {
        HashMap<String, String> checksum1 = ChecksumReader.readFile(file1);
        HashMap<String, String> checksum2 = ChecksumReader.readFile(file2);

        HashMap<String, String> invalidMD5 = new HashMap<String,String>();
        HashMap<String, String> missingMD5 = new HashMap<String, String>();
        HashMap<String, String> passedMD5 = new HashMap<String, String>();
        HashMap<String, String> extraMD5 = new HashMap<String, String>();

        int checksum1Count = checksum1.size();

        boolean checked = false; //has this entry been checked?
        for (Map.Entry<String, String> entry1 : checksum1.entrySet()) {
            checked = false;
            for(Map.Entry<String, String> entry2 : checksum2.entrySet()) {
                if(entry1.getKey().equals(entry2.getKey())) {
                    //We found the same file
                    checked = true;
                    if(entry1.getValue().equals(entry2.getValue())) { //MD5 is good
                        passedMD5.put(entry1.getKey(), entry1.getValue());
                    } else {
                        invalidMD5.put(entry1.getKey(), entry1.getValue());
                    }
                    break;
                }
            }
            if(!checked) {
                //Missing entry
                missingMD5.put(entry1.getKey(), entry1.getValue());
            } else { //Found it, make sure we don't go over it again
                checksum2.remove(entry1.getKey());
            }
        }

        extraMD5.putAll(checksum2); //Anything not removed from checksum2 is extra


        double invalid = printHashMap(invalidMD5, "Invalid (Does not match)", checksum1Count);
        double missing = printHashMap(missingMD5, "Missing (Is in file1 but not file2)", checksum1Count);
        double passed = printHashMap(passedMD5, "Passed (Matched in file1 and file2)", checksum1Count);
        double extra = printHashMap(extraMD5, "Extra (Is not in file1 but is in file2)", checksum1Count);
        System.out.println("Summary:");
        System.out.println("Invalid: " + invalid + "% Missing: " + missing + "% Passed: " + passed + "% Extra: " + extra + "%");
        if(missing == 100) {
            System.out.println("You've got a lot of missing! Maybe you accidently added a \'/\' to the end of your arguments?");
        } else if(passed == 100) {
            System.out.println("Woo!");
        }
    }


    public double printHashMap(HashMap<String, String> hashMap, String name, double total) {
        double percent = (double)Math.round(hashMap.size()/total*10000)/100;
        System.out.println(name + " (" +  percent + "%):");
        for(Map.Entry<String, String> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey());// + " " + entry.getValue());
        }
        if(hashMap.isEmpty()) {
            System.out.println("None!");
        }
        System.out.println("---------");
        return percent;
    }
}
