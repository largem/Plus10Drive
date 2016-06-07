package com.plus10.drive;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by Administrator on 26/05/2016.
 */
public class FileEncoder {
    static final int EncodeSizeLimit = 1000000;     //around 1M
    static final byte[] EOL = System.getProperty("line.separator").getBytes();

    public static EncodeResult encode(String srcPath, String encodePath) {
        EncodeResult res = new EncodeResult(encodePath);

        final File srcFile = new File(srcPath);
        final String srcFileName = srcFile.getName();

        int count = 0, remainSpace = 0;
        byte[] buff = new byte[3998];   //close to 4K

        try(InputStream in = new BufferedInputStream(new FileInputStream(srcFile))) {
            MessageDigest srcMD5 = MessageDigest.getInstance("MD5");
            MessageDigest encMD5 = MessageDigest.getInstance("MD5");
            int len, totalSize=0, totalEncSize=0;
            OutputStream out = null;
            String currEncFileName = null;
            while ((len = in.read(buff)) > 0) {
                totalSize += len;
                byte[] data;
                //make sure there is not garbage
                if (len == buff.length) {
                    data = buff;
                } else {
                    data = new byte[len];
                    System.arraycopy(buff, 0, data, 0, len);
                }
                srcMD5.update(data);
                byte[] encode = Base64.getMimeEncoder().encode(data);
                if (remainSpace < encode.length) {
                    //need a new encoded file to be created. but close the current one first
                    if (out != null) {
                        String md5 = new String(Base64.getEncoder().encode(encMD5.digest()));
                        res.addResult(currEncFileName, md5, totalEncSize);

                        out.close();
                        out = null;
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append(srcFileName + ".");
                    sb.append(count++);
                    currEncFileName = sb.toString();
                    File encFile = new File(encodePath, currEncFileName);

                    out = new BufferedOutputStream(new FileOutputStream(encFile));
                    remainSpace = EncodeSizeLimit;
                    encMD5.reset();
                    totalEncSize = 0;
                }
                out.write(encode);
                out.write(EOL);
                encMD5.update(encode);
                encMD5.update(EOL);
                remainSpace -= (encode.length + EOL.length);
                totalEncSize += (encode.length + EOL.length);
            }
            //deal with the last encoded file
            String md5 = new String(Base64.getEncoder().encode(encMD5.digest()));
            res.addResult(currEncFileName, md5, totalEncSize);
            out.close();

            md5 = new String(Base64.getEncoder().encode(srcMD5.digest()));
            res.setSrcMetadata(srcPath, md5, totalSize);
        }catch(IOException | NoSuchAlgorithmException e) {
            res.reset();
            e.printStackTrace();
        }
        return res;
    }

    public static void decode(IEncodeResult res, String decodeFile) {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(decodeFile))) {
            String rootEncFiles = res.getRoot();

            for (MetaData md : res.getMetaData()) {
                File encFile = new File(rootEncFiles, md.getName());

                try (BufferedReader in = new BufferedReader(new FileReader(encFile))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        byte[] decode = Base64.getMimeDecoder().decode(line.getBytes());
                        out.write(decode);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
