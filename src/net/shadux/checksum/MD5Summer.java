package net.shadux.checksum;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by spencerh on 5/26/2016.
 */
public class MD5Summer {

    private String rootDir;

    HashMap<String, String> md5Hashes;


    public MD5Summer(File checkPath, String rootDir) {
        this.rootDir = rootDir;
        this.md5Hashes = new HashMap<String, String>();
    }

    //Returns in form of HashMap<Filename_str,MD5Hash_str>, takes a file/folder for return all checksums and a rootdir to name the files relative to
    //eg rootDir is C:\Test
    //and actual file is C:\Test\test.txt
    //so then the resulting name stored in the hashmap would be test.txt
    public HashMap<String, String> checkMD5Sums(File currFile) throws NullPointerException {
        if(!currFile.isDirectory()) {
            md5Hashes.put(currFile.getPath().replace(rootDir,""), getMD5Sum(currFile));
        } else {
            for(File file : currFile.listFiles()) {
                md5Hashes.putAll(checkMD5Sums(file));
            }
        }
        return md5Hashes;
    }


    public void printHashList() {
        for (Map.Entry<String, String> entry : md5Hashes.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        if(md5Hashes.isEmpty()) {
            System.out.println("No hashes generated!");
        }

    }

    public String getMD5Sum(File file) {
        String hexString = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            InputStream is = Files.newInputStream(file.toPath());

            byte buffer[] = new byte[1024];
            int numRead;
            do {
                numRead = is.read(buffer);
                if(numRead > 0) {
                    md.update(buffer, 0, numRead);
                }
            } while(numRead != -1);
            is.close();


            byte[] digest = md.digest();
            for(int i = 0;i<digest.length; i++) {
                hexString += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
            }

        } catch(NoSuchAlgorithmException e) {
            System.err.println("No MD5 Algorithm found on local machine!?");
            e.printStackTrace();
        } catch(IOException e) {
            System.err.println("IO Error!");
            e.printStackTrace();
        }

        return hexString;
    }
}
