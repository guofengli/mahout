package org.conan.mymahout.recommendation;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.mahout.cf.taste.hadoop.item.RecommenderJob;
//import org.conan.mymahout.hdfs.HdfsDAO;

public class ItemCFHadoop {

    private static final String HDFS = "hdfs://namenode:8020";

    public static void main(String[] args) throws Exception {
        String inPath = HDFS + "/tmp/intro.csv";
        String outPath = HDFS + "/tmp/result";
        String tmpPath = HDFS + "/tmp/" + System.currentTimeMillis();

        Configuration conf = config();
        StringBuilder sb = new StringBuilder();
        sb.append("--input ").append(inPath);
        sb.append(" --output ").append(outPath);
        sb.append(" --booleanData true");
        sb.append(" --similarityClassname org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.EuclideanDistanceSimilarity");
        sb.append(" --tempDir ").append(tmpPath);
        args = sb.toString().split(" ");
        RecommenderJob job = new RecommenderJob();
        job.setConf(conf);
        job.run(args);
    }

    public static Configuration config() throws IOException {
    	Configuration conf = new Configuration();
        conf.addResource("classpath:/hadoop/core-site.xml");
        conf.addResource("classpath:/hadoop/hdfs-site.xml");
        conf.addResource("classpath:/hadoop/mapred-site.xml");
        return conf;
    }
}