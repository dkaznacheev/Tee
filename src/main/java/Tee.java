import org.apache.commons.cli.*;

import java.io.IOException;

public class Tee {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("a", false, "append to given files, do not overwrite");
        options.addOption("e", false, "exit on error writing to any non-pipe output");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            TeeOutputStream teeOutputStream = new TeeOutputStream(
                    commandLine.getArgs(),
                    commandLine.hasOption("a"),
                    commandLine.hasOption("e")
            );

            int buffer = System.in.read();
            while (buffer != -1) {
                teeOutputStream.write(buffer);
                buffer = System.in.read();
            }
        } catch (ParseException e) {
            System.err.println("Invalid options.");
        } catch (IOException e) {
            System.err.println("Exiting...");
        }
    }
}
