package net.shadux.checksum;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by spencerh on 6/3/2016.
 */
public class ChecksumTester {


    private enum MODE {
        GENERATE,COMPARE,SPECIFY,NONE;
    }

    public static MODE PROGRAM_MODE = MODE.NONE;
    public static boolean forceDelete = false;
    public static boolean specifyFiles = false;

    public static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("CompareTestBuild", options, true);
        System.exit(0);
    }

    public static void main(String[] args) {


        Options options = new Options();
        options.addOption("h", "print this message");
        options.addOption(Option.builder("g").hasArg(true).numberOfArgs(1).argName("folder").longOpt("generate").desc("Generate a checksums.txt for the specified folder, cannot be used in conjunction with -c").build());
        options.addOption(Option.builder("f").hasArg(false).longOpt("force").desc("Force overwrite the checksums.txt even if it already exists").build());
        options.addOption(Option.builder("c").hasArg(true).numberOfArgs(2).argName("folder1> <folder2").longOpt("checksum").desc("Compares two checksum files, cannot be used in conjunction with -g, when used with -s it takes textfiles as arguments instead").build());
        options.addOption(Option.builder("s").hasArg(true).numberOfArgs(2).argName("checksum1.txt> <checksum2.txt").longOpt("specify-files").desc("A flag to specify two txt's to check against eachother, cannot be used with -f, -g or -c").build());


        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            parser = new DefaultParser();
            cmd = parser.parse(options, args);
            if (cmd.hasOption('g') && !cmd.hasOption('s') && !cmd.hasOption('c')) {
                PROGRAM_MODE = MODE.GENERATE;
            } else if(cmd.hasOption('c') && !cmd.hasOption('s') && !cmd.hasOption('g')) {
                PROGRAM_MODE = MODE.COMPARE;
            } else if(cmd.hasOption('s') && !cmd.hasOption('g') && !cmd.hasOption('c') && !cmd.hasOption('f')) {
                specifyFiles = true;
                PROGRAM_MODE = MODE.SPECIFY;
            } else {
                System.out.println("Invalid options!");
                printHelp(options);
            }
            if(cmd.hasOption('f') && PROGRAM_MODE != MODE.SPECIFY) {
                forceDelete = true;
            }
        } catch(ParseException e) {
            System.out.println("Error parsing args!");
            printHelp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if (PROGRAM_MODE == MODE.GENERATE) {
                String folder = cmd.getOptionValue('g');
                if(forceDelete) {
                    try {
                        File file = new File(folder + "/checksums.txt");
                        file.delete();
                    } catch (Exception e) { } //Doesn't matter if we find it or not
                }
                System.out.println("Generating checksums for folder: \'" + folder + "\'...");
                new GenerateChecksum(folder).run();
            } else if (PROGRAM_MODE == MODE.COMPARE) {
                String folder1 = cmd.getOptionValues('c')[0];
                String folder2 = cmd.getOptionValues('c')[1];

                if(forceDelete) {
                    try {
                        File file = new File(folder1 + "/checksums.txt");
                        file.delete();
                        File file1 = new File(folder2 + "/checksums.txt");
                        file1.delete();
                    } catch (Exception e) { } //Doesn't matter if we find it or not
                    new GenerateChecksum(folder1).run(); //Re-generate checksums.txt
                    new GenerateChecksum(folder2).run();
                }
                new CompareChecksum(folder1, folder2, true).run();
            } else if(PROGRAM_MODE == MODE.SPECIFY) {
                String file1 = cmd.getOptionValues('s')[0];
                String file2 = cmd.getOptionValues('s')[1];
                new CompareChecksum(file1, file2, false).run();
            }
        } catch(FileNotFoundException e) {
            System.out.println("File not found! Check your args!");
            printHelp(options);
           // e.printStackTrace();
        } catch(NullPointerException e) {
            System.out.println("Invalid options!");
            printHelp(options);
            //e.printStackTrace();
        } catch(IndexOutOfBoundsException e) {
            System.out.println("Invalid arguments! Check your ordering!");
            printHelp(options);
           // e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Error!");
            e.printStackTrace();
        }
    System.out.println("Done!");
    }

}
