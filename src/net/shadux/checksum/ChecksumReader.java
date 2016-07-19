package net.shadux.checksum;

import java.io.*;
import java.util.HashMap;

/**
 * Created by spencerh on 6/3/2016.
 */
public class ChecksumReader {


   public static HashMap<String, String> readFile(File file) throws FileNotFoundException, IOException, Exception {
       HashMap<String, String> hashMap = new HashMap<String, String>();
       FileReader fileReader = new FileReader(file);
       BufferedReader bufferedReader = new BufferedReader(fileReader);
       String line = "";
       String[] data;
       while((line = bufferedReader.readLine()) != null) {
            data = line.split("/");
            hashMap.put(data[0], data[1]);
       }

       return hashMap;
   }
}
