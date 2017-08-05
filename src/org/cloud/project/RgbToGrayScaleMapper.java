package org.cloud.project;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.hipi.image.FloatImage;
import org.hipi.image.HipiImageHeader;

public class RgbToGrayScaleMapper extends Mapper<HipiImageHeader, FloatImage, IntWritable, FloatImage> {
    public void map(HipiImageHeader key, FloatImage value, Context context) 
      throws IOException, InterruptedException {  // Verify that image was properly decoded, is of sufficient size, and has three color channels (RGB)
        if (value != null && value.getWidth() > 1 && value.getHeight() > 1 && value.getNumBands() == 3) {
        	
        	System.out.println("********Inside Mapper********");
            // Get dimensions of image
            int w = value.getWidth();
            int h = value.getHeight();

            // Get pointer to image data
            float[] valData = value.getData();           
            
            // Converting from RGB to GrayScale
            for (int j = 0; j < h; j++) {
              for (int i = 0; i < w; i++) {
                float red = valData[(j*w+i)*3+0]; // R
                float green = valData[(j*w+i)*3+1]; // G
                float blue = valData[(j*w+i)*3+2]; // B
                
                float lum = (red+green+blue)/3;
                valData[(j*w+i)*3+0] = lum;
                valData[(j*w+i)*3+1] = lum;
                valData[(j*w+i)*3+2] = lum;
              }
            }                        
            FloatImage avg = new FloatImage(w,h,3,valData);            
            context.write(new IntWritable(1), avg);
          } // If (value != null...
          
        } // map()

      } // RbgToGrayScaleMapper