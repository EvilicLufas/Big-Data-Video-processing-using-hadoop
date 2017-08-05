package org.cloud.project;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.hipi.image.FloatImage;
import org.hipi.image.HipiImageHeader;

public class BSMapper extends Mapper<HipiImageHeader, FloatImage, Text, FloatImage> {
    public void map(HipiImageHeader key, FloatImage value, Context context) 
      throws IOException, InterruptedException {  
    	 if (value != null && value.getWidth() > 1 && value.getHeight() > 1 && value.getNumBands() == 3) {
	      
    		 System.out.println("************** INSIDE BS MAPPER ****************");
    		 int top = 0;
    		 int bottom = value.getHeight();
    		 int left = 0;
    		 int right = value.getWidth();
    		 
    		 Text t = new Text(top + "," + bottom + "," + left + "," + right);    		 
    		 context.write(t, value);
    	 }
        } // map()

      } // HelloWorldMapper