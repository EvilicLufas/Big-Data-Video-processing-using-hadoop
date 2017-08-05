package org.cloud.project;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.hipi.image.FloatImage;
import org.hipi.imagebundle.mapreduce.HibInputFormat;
import org.jcodec.api.JCodecException;


public class Main {
	private final static String InputImagesDir = "Repository\\InputImages\\";
	private final static String RGBHibOutputDir = "Repository\\RGBHibOutput\\";
	private final static String GrayScaleImagesDir = "Repository\\GrayScaleImages\\";
	private final static String GrayScaleHibOutputDir = "Repository\\GrayScaleHibOutput\\";
	
	
	//Step 1
	public static void extractFramesFromVideo() throws IOException, JCodecException, ClassNotFoundException, InterruptedException
	{
		ExtractFrames.run(InputImagesDir);
	}
	
	//Step 2 and 4
	public static void inputToHIB(String input, String output) throws IOException
	{
		HibImport.run(input,output);
	}

	//Step 3
	public static void RgbToGrayScaleConversion() throws IOException, ClassNotFoundException, InterruptedException
	{
		Job job = new Job();
		
		job.setInputFormatClass(HibInputFormat.class);
	    // Set the driver, mapper, and reducer classes which express the computation
	    job.setJarByClass(HibImport.class);
	    job.setMapperClass(RgbToGrayScaleMapper.class);
	    job.setReducerClass(RgbToGrayScaleReducer.class);
	    // Set the types for the key/value pairs passed to/from map and reduce layers
	    job.setMapOutputKeyClass(IntWritable.class);
	    job.setMapOutputValueClass(FloatImage.class);
	    job.setOutputKeyClass(IntWritable.class);
	    job.setOutputValueClass(BufferedImage.class);		
		
	    FileInputFormat.setInputPaths(job, new Path(RGBHibOutputDir+"result.hib"));
	    FileOutputFormat.setOutputPath(job, new Path("Repository\\DummyOutput\\"));
		job.submit();
		
	}
	
	public static void BackgroundSubtractionRun() throws IOException, ClassNotFoundException, InterruptedException
	{
		Job job = new Job();
		
		job.setInputFormatClass(HibInputFormat.class);
	    // Set the driver, mapper, and reducer classes which express the computation
	    job.setJarByClass(HibImport.class);
	    job.setMapperClass(BSMapper.class);
	    job.setReducerClass(BSReducer.class);
	    // Set the types for the key/value pairs passed to/from map and reduce layers
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(FloatImage.class);
	    job.setOutputKeyClass(IntWritable.class);
	    job.setOutputValueClass(BufferedImage.class);
		
		
	    FileInputFormat.setInputPaths(job, new Path("Repository/GrayScaleHibOutput/result.hib"));
	    FileOutputFormat.setOutputPath(job, new Path("Repository/FinalOutput/"));
		job.submit();
		
	    boolean success = job.waitForCompletion(true);	
	}
	
	public static void main(String[] args) throws IOException, JCodecException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub
			
		System.out.println("********** Object detection using Background Subtraction *********");	
		
		System.out.println("\n STEP 1- Converting mp4 video to frames- \n");
		
		//Converting video to frames
		Main.extractFramesFromVideo();		
		
		System.out.println("\n STEP 2- Converting input frames to HIB bundles- \n");
		//Converting input images to HIB Bundles
		Main.inputToHIB(InputImagesDir,RGBHibOutputDir);
		
		System.out.println("\n STEP 3- Converting HIB Bundles to GrayScale Images to increase processing efficiency (Map reduce phase 1) ");
		//Converting input bundles to GrayScale images
		Main.RgbToGrayScaleConversion();		
		
		System.out.println("\n STEP 4- Converting GrayScale Images to HIB Bundles to further process background subtraction on them!! ");
		//Converting input grayscale images to output bundles
		Main.inputToHIB(GrayScaleImagesDir, GrayScaleHibOutputDir);
		
		
		Main.BackgroundSubtractionRun();
	}

}
