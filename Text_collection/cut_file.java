package asd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class asd {
    public static void main(String[] args) {
        cutFile("D:\\asd\\data.xml", "D:\\asd\\hhh",1024 * 1024 * 1);
    }

    private static void cutFile(String src, String endsrc, int num) {
        FileInputStream fis = null;
        File file = null;
        try {
            fis = new FileInputStream(src);
            file = new File(src);
            byte[] b = new byte[num];
            int len = 0;
            int name = 1;
            while ((len = fis.read(b)) != -1) {
                String name2 = file.getName();
                int lastIndexOf = name2.lastIndexOf(".");
                String substring = name2.substring(0, lastIndexOf);
                String substring2 = name2.substring(lastIndexOf, name2.length());
                FileOutputStream fos = new FileOutputStream(endsrc + "\\\\"+ substring + "-" + name + substring2);
                fos.write(b, 0, len);
                fos.close();
                name++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}