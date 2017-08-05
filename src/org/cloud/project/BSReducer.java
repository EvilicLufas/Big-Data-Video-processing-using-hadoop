package org.cloud.project;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.hipi.image.FloatImage;
import org.hipi.image.PixelArray;

public class BSReducer extends Reducer<Text, FloatImage, IntWritable, BufferedImage> {
	  public static BufferedImage BSAlgo(BufferedImage currentImg, BufferedImage bgImg, int top, int bottom, int left, int right) throws Exception
	  {
		  if (bgImg==null) {
	    		throw new Exception("Can't subtract background as bgImg==null");
	    	}
	    	else {
	    		for (int i= left; i< right; i++) {
	            	for (int j= top; j< bottom; j++) {
	            		Color c = new Color(currentImg.getRGB(i,j));

	            		//System.out.println("Colour before="+c);
	            		int cred = c.getRed();
	            		int cgreen = c.getGreen();
	            		int cblue = c.getBlue();
	            		
	            		//Subtract the background
	            		Color cBack = new Color(bgImg.getRGB(i, j));
	            		int backRed = cBack.getRed();
	            		int backGreen = cBack.getGreen();
	            		int backBlue = cBack.getBlue();
	            		
	            		cred = loopback(cred-backRed);
	            		cgreen = loopback(cgreen-backGreen);
	            		cblue = loopback(cblue-backBlue);
	            		try {
	            			currentImg.setRGB(i, j, (new Color(cred, cgreen, cblue)).getRGB());
	            		}
	            		catch(Exception e) {
	            			System.out.println("Error subtracting background pixels:"+e.getMessage()); 
	            			System.out.println("cred="+cred+"; cgreen="+cgreen+"; cblue="+cblue+";");
	            		}
	            		//System.out.println("Colour after="+new Color(img.getRGB(i, j)));
	            	}
	            }
	        	return currentImg;
	        }
	  }
	  
  private static int loopback(int hexValue) {
		return (int)128+(hexValue/2);
	}    
  
  public void reduce(Text key, Iterable<FloatImage> values, Context context) 
    throws IOException, InterruptedException  {
	  
	  System.out.println("************ INSIDE BS REDUCER**************");
	  
	  //Create Background image
	  FloatImage bgFloatImage = values.iterator().next();
	  int w = bgFloatImage.getWidth();
      int h = bgFloatImage.getHeight();

      BufferedImage bgImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

      PixelArray pa = bgFloatImage.getPixelArray();
      int[] rgb = new int[w*h];
      for (int i=0; i<w*h; i++) {

        int r = pa.getElemNonLinSRGB(i*3+0);
        int g = pa.getElemNonLinSRGB(i*3+1);
        int b = pa.getElemNonLinSRGB(i*3+2);

        rgb[i] = (r << 16) | (g << 8) | b;
      }
      bgImg.setRGB(0, 0, w, h, rgb, 0, w);	//Background image has been set
      
      
      //Create Bufferd image to be checked for bg subtraction
      FloatImage bgCurrImage = values.iterator().next();
	  int w2 = bgFloatImage.getWidth();
      int h2 = bgFloatImage.getHeight();

      BufferedImage currentImg = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_RGB);

      PixelArray pa2 = bgCurrImage.getPixelArray();
      int[] rgb2 = new int[w2*h2];
      for (int i=0; i<w*h; i++) {

        int r = pa2.getElemNonLinSRGB(i*3+0);
        int g = pa2.getElemNonLinSRGB(i*3+1);
        int b = pa2.getElemNonLinSRGB(i*3+2);

        rgb2[i] = (r << 16) | (g << 8) | b;
      }
      currentImg.setRGB(0, 0, w2, h2, rgb2, 0, w2);	//Background image has been set       
      
      String d = key.toString();
      String dimensions[] = d.split(",");
      
      try {
		currentImg = BSReducer.BSAlgo(currentImg, bgImg, Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]),
				  			Integer.parseInt(dimensions[2]), Integer.parseInt(dimensions[3]));		
	} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      
      File outputfile = new File("saved.jpg");
	  ImageIO.write(currentImg, "jpg", outputfile);
	  
	  context.write(new IntWritable(1), currentImg);
      
  }
  } // HelloWorldReducer