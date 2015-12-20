package scripts.LanAPI.Core.IO.Compression;

import scripts.LanAPI.Core.Logging.LogProxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Laniax
 */
public class ZipCompressor {

    private static LogProxy log = new LogProxy("ZipCompressor");

    private static final int _bufferSize = 4096;

    /**
     * Zips the file to an archive.
     * @param inputPath
     * @param outputPath
     */
    public static void compress(String inputPath, String outputPath) {

        compress(inputPath, outputPath);
    }

    /**
     * Zips multiple files to an archive.
     * @param inputPaths
     * @param outputPath
     */
    public static void compress(List<String> inputPaths, String outputPath) {

        byte[] buffer = new byte[_bufferSize];

        try {

            FileOutputStream fos = new FileOutputStream(outputPath);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (String file : inputPaths) {

                ZipEntry entry = new ZipEntry(file);
                zos.putNextEntry(entry);

                FileInputStream fis = new FileInputStream(file);

                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                fis.close();

            }

            zos.closeEntry();
            zos.close();

        } catch (FileNotFoundException e) {
            log.error("Error writing file to ZIP. Input file couldn't be found.");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Error writing file to ZIP.");
            e.printStackTrace();
        }
    }
}
