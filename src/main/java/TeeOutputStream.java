import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An unbuffered OutputStream capable of writing to many files and standard output.
 */
public class TeeOutputStream extends OutputStream implements AutoCloseable{
    /**
     * List of file output streams to write to.
     */
    private List<OutputStream> streams;
    /**
     * A flag that determines whether we exit on errors writing to any of the files or not.
     */
    private boolean exitOnError;

    /**
     * Constructs a TeeOutputStream
     * @param filenames array of the filenames to write to
     * @param append append to files or overwrite them
     * @param exitOnError whether we exit on errors writing to any of the files or not
     * @throws IOException if an IOException occurred when opening one of the files and we have exitOnError flag on
     */
    public TeeOutputStream(String[] filenames, boolean append, boolean exitOnError) throws IOException {
        this.exitOnError = exitOnError;
        streams = new ArrayList<>();

        for (String name : filenames) {
            try {
                File file = new File(name);
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(name, append);
                streams.add(outputStream);
            } catch (IOException e) {
                System.err.println("Error opening/creating file " + name);
                if (exitOnError) {
                    throw e;
                }
            }
        }
    }

    /**
     * Writes byte to all of its OutputStreams and System.out.
     * @param b byte to write
     * @throws IOException if an IOException occurred when writing to one of the files and we have exitOnError flag on
     */
    @Override
    public void write(int b) throws IOException {
        System.out.write(b);

        Iterator<OutputStream> iterator = streams.iterator();
        while (iterator.hasNext()) {
            OutputStream stream = iterator.next();
            try {
                stream.write(b);
            } catch (IOException e) {
                iterator.remove();
                if (exitOnError) {
                    throw e;
                }
            }
        }
    }

    /**
     * Writes byte buffer to all of its OutputStreams and System.out.
     * @param b byte buffer to write
     * @throws IOException if an IOException occurred when writing to one of the files and we have exitOnError flag on
     */
    @Override
    public void write(byte[] b) throws IOException {
        System.out.write(b);

        Iterator<OutputStream> iterator = streams.iterator();
        while (iterator.hasNext()) {
            OutputStream stream = iterator.next();
            try {
                stream.write(b);
            } catch (IOException e) {
                iterator.remove();
                if (exitOnError) {
                    throw e;
                }
            }
        }
    }

    /**
     * Flushes all of its OutputStreams and System.out.
     * @throws IOException if an IOException occurred when flushing one of the files and we have exitOnError flag on
     */
    @Override
    public void flush() throws IOException {
        super.flush();
        Iterator<OutputStream> iterator = streams.iterator();
        while (iterator.hasNext()) {
            OutputStream stream = iterator.next();
            try {
                stream.flush();
            } catch (IOException e) {
                iterator.remove();
                if (exitOnError) {
                    throw e;
                }
            }
        }
    }

    /**
     * Closes all of its OutputStreams.
     * @throws IOException if an IOException occurred when closing one of the files and we have exitOnError flag on
     */
    @Override
    public void close() throws IOException{
        super.close();
        for (OutputStream stream: streams) {
            stream.close();
        }
    }
}
