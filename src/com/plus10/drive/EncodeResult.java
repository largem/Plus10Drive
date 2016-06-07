package com.plus10.drive;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 26/05/2016.
 */
interface IEncodeResult {
    public String getRoot();
    public MetaData[] getMetaData();
    public MetaData getSrcMetaData();
}


public class EncodeResult implements IEncodeResult {
    private final String root;
    private List<MetaData> encodedMetaData;
    private MetaData srcMetaData;


    public EncodeResult(String root) {
        this.root = root;
        encodedMetaData = new ArrayList<MetaData>();
        srcMetaData = null;
    }

    public String getRoot() {
        return root;
    }

    public void addResult(String name, String md5, int size) {
        encodedMetaData.add(encodedMetaData.size(), new MetaData(name, md5, size));
    }

    public void setSrcMetadata(String name, String md5, int size) {
        srcMetaData = new MetaData(name, md5, size);
    }

    public MetaData getSrcMetaData() {
        return srcMetaData;
    }

    public MetaData[] getMetaData() {
        return  encodedMetaData.toArray(new MetaData[0]);
    }

    public void reset() {
        encodedMetaData.clear();
        srcMetaData = null;
    }

    public void dump(String file){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(srcMetaData.getMd5()); bw.newLine();
            bw.write(Integer.toString(srcMetaData.getSize())); bw.newLine();

            for (MetaData md : encodedMetaData) {
                bw.write(md.toString());
                bw.newLine();
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void importFrom(String file) {
        //root has to set during creation.
        reset();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            setSrcMetadata("", line, Integer.parseInt(br.readLine()));

            while ((line = br.readLine())!=null) {
                String fields[] = line.split(":");
                addResult(fields[0], fields[1], Integer.parseInt(fields[2]));
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
