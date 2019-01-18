package com.fijo.cores.utils.office;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fijo.cores.utils.AppFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.fijo.cores.utils.AppFileUtils;

@Component
public class PdfBuilder {
	private int pageHeight;
	private int pageWidth;
	public static BaseFont bfChinese;
	public static Font titleFont;// 标题
	public static Font mediumTitleFont;// 中等标题	
	public static Font smallTitleFont;// 小标题
	public static Font cellFont;// 内容
	public static Font mediumCellFont;// 中等内容
	public static Font smallCellFont;// 小内容
	
	@Autowired
	protected com.fijo.cores.utils.AppFileUtils AppFileUtils;
	
	static{
		try {
			bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			titleFont = new Font(bfChinese, 14, Font.BOLD);
			mediumTitleFont = new Font(bfChinese, 12, Font.BOLD);
			smallTitleFont = new Font(bfChinese, 10, Font.BOLD);
			cellFont = new Font(bfChinese, 12, Font.NORMAL);
			mediumCellFont = new Font(bfChinese, 10, Font.NORMAL);
			smallCellFont = new Font(bfChinese, 10, Font.NORMAL);
		} catch (DocumentException | IOException e) {
		}	
	}

	public Document createDocument(File pdfFile) throws DocumentException, IOException{
		Document document = new Document(new Rectangle(pageWidth, pageHeight));
		document.setPageSize(PageSize.A4);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
		//写入页尾
		this.setFooter(writer); 
		writer.setFullCompression();
		writer.setPdfVersion(PdfWriter.VERSION_1_4);
		document.open();	
//		//加入二维码图片
//		Image image = Image.getInstance(System.getProperty(appConfig.getValue("app.root"))+"/images/logoqrcode.png");
//        image.scaleAbsolute(40,40);//控制图片大小
//        image.setAlignment(Image.LEFT);
//        document.add(image);
        return document;
	}

	private void setFooter(PdfWriter writer) throws DocumentException,
			IOException {
		//HeaderFooter headerFooter = new HeaderFooter("海南国保", 10, PageSize.A4);
		// 更改事件，瞬间变身 第几页/共几页 模式。
		HeaderFooter headerFooter = new HeaderFooter();// 就是上面那个类
		writer.setBoxSize("art", PageSize.A4);
		writer.setPageEvent(headerFooter);
	}

	//特定大小、特定样式中文字体
	public Font createFont(int size, int style){
		return new Font(bfChinese, size, style);
	}
	
	//标题
	public static PdfPCell createTitle(String text, Font font, int location) {	
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setBackgroundColor(new BaseColor(242, 242, 242));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);// 垂直居中
		cell.setHorizontalAlignment(location);
		return cell;
	}	
	
	//单元格内容
	public static PdfPCell createCell(String text, Font font, int location) {
		PdfPCell cell = new PdfPCell(new Phrase(text, cellFont));		
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);// 垂直居中
		cell.setHorizontalAlignment(location);
		return cell;
	}

	public int getPageHeight() {
		return pageHeight;
	}

	public void setPageHeight(int pageHeight) {
		this.pageHeight = pageHeight;
	}

	public int getPageWidth() {
		return pageWidth;
	}

	public void setPageWidth(int pageWidth) {
		this.pageWidth = pageWidth;
	}
}
