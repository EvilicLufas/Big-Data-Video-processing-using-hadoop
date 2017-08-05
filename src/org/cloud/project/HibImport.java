package org.cloud.project;

import org.hipi.imagebundle.HipiImageBundle;
import org.hipi.imagebundle.mapreduce.HibInputFormat;
import org.hipi.image.FloatImage;
import org.hipi.image.HipiImageHeader;
import org.hipi.image.HipiImageHeader.HipiImageFormat;
import org.hipi.image.PixelArray;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.ParseException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FSDataInputStream;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Arrays;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

public class HibImport {

	
  private static final Options options = new Options();
  private static final Parser parser = (Parser)new BasicParser();
  static {
    options.addOption("f", "force", false, "force overwrite if output HIB already exists");
    options.addOption("h", "hdfs-input", false, "assume input directory is on HDFS");
  }

  private static void usage() {
    // usage
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("hibImport.jar [options] <image directory> <output HIB>", options);
    System.exit(0);
  }
  
  public static void run(String inputImageDir, String outputHibDir) throws IOException  {	  

	    
	    String outputHib = "result.hib";
	    boolean overwrite = false;
	   
	    System.out.println("\n\t-->Input image directory: " + inputImageDir);	
	    System.out.println("\t-->Output HIB: " + outputHibDir);
	    
	    Configuration conf = new Configuration();
	    FileSystem fs = FileSystem.get(conf);	    
	      File folder = new File(inputImageDir);
	      File[] files = folder.listFiles();
	      Arrays.sort(files);
	    
	      if (files == null) {
	        System.err.println(String.format("Did not find any files in the local FS directory [%s]", inputImageDir));
	        System.exit(0);
	      }
	      
	      HipiImageBundle hib = new HipiImageBundle(new Path(outputHibDir+outputHib), conf);
	      hib.openForWrite(overwrite);
	      
	      for (File file : files) {
	        FileInputStream fis = new FileInputStream(file);
	        String localPath = file.getPath();
	        HashMap<String, String> metaData = new HashMap<String,String>();
	        metaData.put("source", localPath);
	        String fileName = file.getName().toLowerCase();
	        metaData.put("filename", fileName);
	        String suffix = fileName.substring(fileName.lastIndexOf('.'));
	        System.out.println("\n\t--> Adding to hib bundle: "+file);
	        if (suffix.compareTo(".jpg") == 0 || suffix.compareTo(".jpeg") == 0) {
	         hib.addImage(fis, HipiImageFormat.JPEG, metaData);
	         System.out.println(" \t** added: " + fileName);
	       }
	       else if (suffix.compareTo(".png") == 0) {
	         hib.addImage(fis, HipiImageFormat.PNG, metaData);
	         System.out.println(" ** added: " + fileName);
	       } 
	     }
	     hib.close();	    	    
	    System.out.println("Created: " + outputHib + " and " + outputHib + ".dat");    	  
  }
}
