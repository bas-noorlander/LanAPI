package scripts.LanAPI.Core.IO.Compression;

import scripts.LanAPI.Core.Logging.LogProxy;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Laniax
 */
public class ZipDecompressor {

    private static LogProxy log = new LogProxy("ZipDecompressor");

    private static final int _bufferSize = 4096;

    public static void decompress(String zipPath, String outputFolder) {

        byte[] buffer = new byte[_bufferSize];

        try {

            File output = new File(outputFolder);
            if (!output.exists())
                output.mkdir();

            FileInputStream fis = new FileInputStream(zipPath);
            ZipInputStream zis = new ZipInputStream(fis);

            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                String fileName = ze.getName();
                File file = new File(outputFolder + File.separator + fileName);

                new File(file.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(file);

                int length;
                while ((length = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }


                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
