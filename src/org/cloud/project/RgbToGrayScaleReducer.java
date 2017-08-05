package org.cloud.project;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.hipi.image.FloatImage;
import org.hipi.image.PixelArray;

public class RgbToGrayScaleReducer extends Reducer<IntWritable, FloatImage, IntWritable, BufferedImage> {
	  private static int count = 1;
  public void reduce(IntWritable key, Iterable<FloatImage> values, Context context) 
    throws IOException, InterruptedException  {

  	System.out.println("*******Inside Reducer************");
  	
      // Create FloatImage object to hold final result
      for(FloatImage v:values)
      {
      	int w = v.getWidth();
          int h = v.getHeight();

          BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

          PixelArray pa = v.getPixelArray();
          int[] rgb = new int[w*h];
          for (int i=0; i<w*h; i++) {

            int r = pa.getElemNonLinSRGB(i*3+0);
            int g = pa.getElemNonLinSRGB(i*3+1);
            int b = pa.getElemNonLinSRGB(i*3+2);

            rgb[i] = (r << 16) | (g << 8) | b;
          }
          bufferedImage.setRGB(0, 0, w, h, rgb, 0, w);
          //IIOImage iioImage = new IIOImage(bufferedImage, null, null);
          File f = new File("Repository//GrayScaleImages//"+Integer.toString(count)+".jpg");
          ImageIO.write(bufferedImage, "jpg", f);
          count++;
      	  context.write(key, bufferedImage);
      }

    } // reduce()

  } // HelloWorldReducer