import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class TeeTest {
    @Test
    public void TeeSimpleTest() throws Exception {
        byte[] bytes = new byte[256];
        for (int i = 0; i < 256; i++) {
            bytes[i] = (byte)i;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        String[] names = new String[10];
        for (int i = 0; i < names.length; i++) {
            names[i] = "out" + Integer.toString(i) + ".bin";
        }
        TeeOutputStream outputStream = new TeeOutputStream(names, false, false);
        for (int i = 0; i < 256; i++) {
            outputStream.write(inputStream.read());
        }

        for (int i = 0; i < 2; i++) {
            FileInputStream fileInputStream = new FileInputStream(names[i]);
            byte[] result = new byte[256];
            int j = 0;
            int b = fileInputStream.read();
            while (b != -1) {
                result[j++] = (byte)b;
                b = fileInputStream.read();
            }
            assertArrayEquals(bytes, result);
        }
    }

    @Test
    public void AppendTest() throws Exception {
        byte[] bytes = new byte[256];
        for (int i = 0; i < 256; i++) {
            bytes[i] = (byte) i;
        }
        String[] names = new String[2];
        for (int i = 0; i < names.length; i++) {
            names[i] = "out" + Integer.toString(i) + ".bin";
        }
        for (int j = 0; j < 2; j++) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            TeeOutputStream outputStream = new TeeOutputStream(names, true, false);
            for (int i = 0; i < 256; i++) {
                outputStream.write(inputStream.read());
            }
             outputStream.close();
        }
        
        for (int i = 0; i < 2; i++) {
            FileInputStream fileInputStream = new FileInputStream(names[i]);
            byte[] result = new byte[512];
            int j = 0;
            int b = fileInputStream.read();
            while (b != -1) {
                result[j++] = (byte)b;
                b = fileInputStream.read();
            }
            assertArrayEquals(bytes, Arrays.copyOfRange(result, 0, 256));
            assertArrayEquals(bytes, Arrays.copyOfRange(result, 256, 512));
        }
    }
    
    @Test (expected = IOException.class)
    public void ExitOnErrorTest() throws Exception {
        File readOnlyFile = new File("readOnly.bin");
        readOnlyFile.createNewFile();
        readOnlyFile.setReadOnly();

        String[] names = new String[2];
        names[0] = "readAndWrite.bin";
        names[1] = "readOnly.bin";
        TeeOutputStream outputStream = new TeeOutputStream(names, false, true);
    }

    @Test
    public void ContinueOnErrorTest() throws Exception {
        byte[] bytes = new byte[256];
        for (int i = 0; i < 256; i++) {
            bytes[i] = (byte)i;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        File readOnlyFile = new File("readOnly.bin");
        readOnlyFile.createNewFile();
        readOnlyFile.setReadOnly();
        String[] names = new String[2];
        names[0] = "readAndWrite.bin";
        names[1] = "readOnly.bin";

        TeeOutputStream outputStream = new TeeOutputStream(names, false, false);
        for (int i = 0; i < 256; i++) {
            outputStream.write(inputStream.read());
        }

        FileInputStream fileInputStream = new FileInputStream(names[0]);
        byte[] result = new byte[256];
        int j = 0;
        int b = fileInputStream.read();
        while (b != -1) {
            result[j++] = (byte)b;
            b = fileInputStream.read();
        }
        assertArrayEquals(bytes, result);
    }
}