package org.cloud.project;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;

public class ExtractFrames {

	public static void run(String inputImageDir) throws IOException, JCodecException, ClassNotFoundException, InterruptedException
	{
		  int frameNumber = 0;
          BufferedImage frame = null;           

          //video from which frames can be retrieved, declare frame number,
          //returns numbered frame from video                 	
          frame = FrameGrab.getFrame(new File("Repository\\Video1.mp4"), 0);
          ImageIO.write(frame, "jpg", new File(inputImageDir+Integer.toString(frameNumber)+".jpg"));          
          System.out.println("\t--> Adding frame to folder "+inputImageDir+" as image: "+Integer.toString(frameNumber)+".jpg" + " ...");
          frameNumber  = frameNumber + 1;
          
          frame = FrameGrab.getFrame(new File("Repository\\Video1.mp4"), 50);
          ImageIO.write(frame, "jpg", new File(inputImageDir+Integer.toString(frameNumber)+".jpg"));
          System.out.println("\t--> Adding frame to folder "+inputImageDir+" as image: "+Integer.toString(frameNumber)+".jpg" + " ...");
          frameNumber  = frameNumber + 1;
		
		
	}
}
