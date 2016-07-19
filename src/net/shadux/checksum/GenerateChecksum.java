package net.shadux.checksum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by spencerh on 6/3/2016.
 */
public class GenerateChecksum {


    private String rootDir;
    private File rootFile;

    public GenerateChecksum(String rootDir) throws FileNotFoundException {
        this.rootDir = rootDir;

        this.rootFile = new File(rootDir);
        if(!rootFile.exists()) { throw new FileNotFoundException("Specified root file does not exist: " + rootDir); }
    }

    //filename, checksum-242j34kl23j4l2k3j4l2k3j4-etc
    public HashMap<String, String> getMD5Sums() {
        MD5Summer md5summer = new MD5Summer(rootFile, rootDir);
        HashMap<String, String> sums = md5summer.checkMD5Sums(rootFile);
        return sums;
    }

    public void run() throws FileNotFoundException {
        HashMap<String, String> hashMap = getMD5Sums();
        File outputFile = new File(rootDir + File.separator + "checksums.txt");


        if(outputFile.exists() && !ChecksumTester.forceDelete) {
            System.out.println("checksums.txt already exists! Use -f to force overwrite!");
        } else {
            PrintWriter pw = new PrintWriter(outputFile);

            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                pw.println(entry.getKey() + "/" + entry.getValue());
            }
            pw.close();
        }

    }
}
