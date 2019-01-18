/**
 * 
 */
package com.fijo.cores.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.fijo.cores.exceptions.Exceptions;

/**
 * 二维码工具类
 * 
 * @author zhangbo
 *
 */
@Component
public class QrCodeUtil {

	@Autowired
	private AppFileUtils fileUtils;
	
	/**
	 * 生成二维码
	 * @param content
	 * @param storePath
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String createQrCode(String content, String storePath){
		String savePath = null;
		try {
			File tmpFile = File.createTempFile("tmp_", ".png");
			QRCodeWriter writer = new QRCodeWriter();
			BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 200, 200);
			MatrixToImageWriter.writeToFile(matrix, "png", tmpFile);
			savePath = fileUtils.uploadFromLocal(tmpFile, storePath);
		} catch (IOException | WriterException e) {
			Exceptions.printException(e);
		}
		return savePath;		
	}
	
	/**
	 * 读取二维码
	 * @param qrCodeFile
	 * @return
	 */
	public String readQrCode(File qrCodeFile){
		String ret = null;
		try {			
			QRCodeReader reader = new QRCodeReader();
			BufferedImage image = ImageIO.read(qrCodeFile);
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap imageBinaryBitmap = new BinaryBitmap(binarizer);
			Result result = reader.decode(imageBinaryBitmap);
			ret = result.getText();
		} catch (IOException |NotFoundException | ChecksumException | FormatException e) {
			Exceptions.printException(e);
		}
		return ret;		
	}
}
