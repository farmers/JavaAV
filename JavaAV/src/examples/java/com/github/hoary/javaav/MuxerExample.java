/*
 * Copyright (C) 2013 Alex Andres
 *
 * This file is part of JavaAV.
 *
 * JavaAV is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version (subject to the "Classpath"
 * exception as provided in the LICENSE file that accompanied
 * this code).
 *
 * JavaAV is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaAV. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.hoary.javaav;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public class MuxerExample {

	public static void main(String[] args) throws Exception {
		Muxer muxer = new Muxer("src/examples/resources/out3.mp4");
		muxer.setVideoCodec(Codec.getEncoderById(CodecID.H264));
		muxer.setAudioCodec(Codec.getEncoderById(CodecID.AAC));
		muxer.setImageWidth(600);
		muxer.setImageHeight(350);
		muxer.setGOPSize(25);
		muxer.setPixelFormat(PixelFormat.YUV420P);
		muxer.setVideoBitrate(2000000);
		muxer.setAudioBitrate(128000);
		muxer.setFramerate(25);
		muxer.setSamplerate(24000);
		muxer.setAudioChannels(2);
		muxer.open();

		
		Demuxer demuxer = new Demuxer();
		demuxer.open("src/examples/resources/Wildlife.wmv");
		
		MediaFrame mediaFrame;
		while ((mediaFrame = demuxer.readFrame()) != null) {
			if (mediaFrame.getType() == MediaFrame.Type.VIDEO) {
				VideoFrame frame = (VideoFrame) mediaFrame;
				muxer.addImage(frame);
			}
			if (mediaFrame.getType() == MediaFrame.Type.AUDIO) {
				AudioFrame frame = (AudioFrame) mediaFrame;
				muxer.addSamples(frame);
			}
			if ( mediaFrame.getTimestamp() == 2  ) {
			    VideoFrame videoFrame = (VideoFrame) mediaFrame;
		        BufferedImage image = Image.createImage(videoFrame, BufferedImage.TYPE_3BYTE_BGR);
		        
		        File compressedImageFile = new File("src/examples/resources/Wildlife.jpg");

		        OutputStream os = new FileOutputStream(compressedImageFile);

		        float quality = 0.5f;


		        // get all image writers for JPG format
		        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");

		        if (!writers.hasNext())
		            throw new IllegalStateException("No writers found");

		        ImageWriter writer = (ImageWriter) writers.next();
		        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
		        writer.setOutput(ios);

		        ImageWriteParam param = writer.getDefaultWriteParam();

		        // compress to a given quality
		        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		        param.setCompressionQuality(quality);

		        // appends a complete image stream containing a single image and
		        //associated stream and image metadata and thumbnails to the output
		        writer.write(null, new IIOImage(image, null, null), param);

		        // close all streams
		        os.close();
		        ios.close();
		        writer.dispose();
		        
			}
		}
		
		demuxer.close();
		muxer.close();
	}

}