package com.fijo.cores.utils.office;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddressList;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.common.collect.Lists;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.annotations.ExcelVOAttribute;

public class ExcelUtil2<T> {
	Class<T> clazz;

	public ExcelUtil2(Class<T> clazz) {
		this.clazz = clazz;
	}
	

	public List<T> importExcel0307(String sheetName, InputStream input) throws InvalidFormatException {
		int maxCol = 0;
		List<T> list = Lists.newArrayList();
		try {
//			HSSFWorkbook workbook = new HSSFWorkbook(input);
			Workbook workbook = WorkbookFactory.create(input); 
			Sheet sheet = workbook.getSheet(sheetName);
			if (!sheetName.trim().equals("")) {
				sheet = workbook.getSheet(sheetName);// 如果指定sheet名,则取指定sheet中的内容.
			}
			if (sheet == null) {
				sheet = workbook.getSheetAt(0); // 如果传入的sheet名不存在则默认指向第1个sheet.
			}
			int rows = sheet.getPhysicalNumberOfRows();

			if (rows > 0) {// 有数据时才处理
				// Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.
				Collection<Field> allFields = getMappedFiled(clazz, null, null);

				Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>();// 定义一个map用于存放列的序号和field.
				for (Field field : allFields) {
					// 将有注解的field存放到map中.
					if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
						ExcelVOAttribute attr = field
								.getAnnotation(ExcelVOAttribute.class);						
						int col = getExcelCol(attr.column());// 获得列号
						maxCol = Math.max(col, maxCol);
						// System.out.println(col + "====" + field.getName());
						field.setAccessible(true);// 设置类的私有字段属性可访问.
						fieldsMap.put(col, field);
					}
				}
				for (int i = 1; i < rows; i++) {// 从第2行开始取数据,默认第一行是表头.
					Row row = sheet.getRow(i);
					// int cellNum = row.getPhysicalNumberOfCells();
					// int cellNum = row.getLastCellNum();
					int cellNum = maxCol;
					T entity = null;
					for (int j = 0; j <= cellNum; j++) {					
						Cell cell = row.getCell(j);
						if (cell == null) {
							continue;
						}
						int cellType = cell.getCellType();
						String c = "";
						if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
							c = String.valueOf(cell.getNumericCellValue());
						} else if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {
							c = String.valueOf(cell.getBooleanCellValue());
						} else {
							c = cell.getStringCellValue();
						}
						if (c == null || c.equals("")) {
							continue;
						}
						entity = (entity == null ? clazz.newInstance() : entity);// 如果不存在实例则新建.
						// System.out.println(cells[j].getContents());
						Field field = fieldsMap.get(j);// 从map中得到对应列的field.
						if (field == null) {
							continue;
						}
						// 取得类型,并根据对象类型设置值.
						Class<?> fieldType = field.getType();
						if (String.class == fieldType) {
							field.set(entity, String.valueOf(c));
						} else if ((Integer.TYPE == fieldType)
								|| (Integer.class == fieldType)) {
							field.set(entity, Integer.parseInt(c));
						} else if ((Long.TYPE == fieldType)
								|| (Long.class == fieldType)) {
							field.set(entity, Long.valueOf(c));
						} else if ((Float.TYPE == fieldType)
								|| (Float.class == fieldType)) {
							field.set(entity, Float.valueOf(c));
						} else if ((Short.TYPE == fieldType)
								|| (Short.class == fieldType)) {
							field.set(entity, Short.valueOf(c));
						} else if ((Double.TYPE == fieldType)
								|| (Double.class == fieldType)) {
							field.set(entity, Double.valueOf(c));
						} else if (Character.TYPE == fieldType) {
							if ((c != null) && (c.length() > 0)) {
								field.set(entity,
										Character.valueOf(c.charAt(0)));
							}
						}

					}
					if (entity != null) {
						list.add(entity);
					}
				}
			}

		} catch (IOException e) {
			Exceptions.printException(e);;
		} catch (InstantiationException e) {
			Exceptions.printException(e);;
		} catch (IllegalAccessException e) {
			Exceptions.printException(e);;
		} catch (IllegalArgumentException e) {
			Exceptions.printException(e);;
		}
		return list;
	}
	
	/*excel2003*/
	public List<T> importExcel(String sheetName, InputStream input) {
		int maxCol = 0;
		List<T> list = Lists.newArrayList();
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(input);
			HSSFSheet sheet = workbook.getSheet(sheetName);
			if (!sheetName.trim().equals("")) {
				sheet = workbook.getSheet(sheetName);// 如果指定sheet名,则取指定sheet中的内容.
			}
			if (sheet == null) {
				sheet = workbook.getSheetAt(0); // 如果传入的sheet名不存在则默认指向第1个sheet.
			}
			int rows = sheet.getPhysicalNumberOfRows();

			if (rows > 0) {// 有数据时才处理
				// Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.
				Collection<Field> allFields = getMappedFiled(clazz, null, null);

				Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>();// 定义一个map用于存放列的序号和field.
				for (Field field : allFields) {
					// 将有注解的field存放到map中.
					if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
						ExcelVOAttribute attr = field
								.getAnnotation(ExcelVOAttribute.class);						
						int col = getExcelCol(attr.column());// 获得列号
						maxCol = Math.max(col, maxCol);
						// System.out.println(col + "====" + field.getName());
						field.setAccessible(true);// 设置类的私有字段属性可访问.
						fieldsMap.put(col, field);
					}
				}
				for (int i = 1; i < rows; i++) {// 从第2行开始取数据,默认第一行是表头.
					HSSFRow row = sheet.getRow(i);
					// int cellNum = row.getPhysicalNumberOfCells();
					// int cellNum = row.getLastCellNum();
					int cellNum = maxCol;
					T entity = null;
					for (int j = 0; j <= cellNum; j++) {					
						HSSFCell cell = row.getCell(j);
						if (cell == null) {
							continue;
						}
						int cellType = cell.getCellType();
						String c = "";
						if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
							c = String.valueOf(cell.getNumericCellValue());
						} else if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {
							c = String.valueOf(cell.getBooleanCellValue());
						} else {
							c = cell.getStringCellValue();
						}
						if (c == null || c.equals("")) {
							continue;
						}
						entity = (entity == null ? clazz.newInstance() : entity);// 如果不存在实例则新建.
						// System.out.println(cells[j].getContents());
						Field field = fieldsMap.get(j);// 从map中得到对应列的field.
						if (field == null) {
							continue;
						}
						// 取得类型,并根据对象类型设置值.
						Class<?> fieldType = field.getType();
						if (String.class == fieldType) {
							field.set(entity, String.valueOf(c));
						} else if ((Integer.TYPE == fieldType)
								|| (Integer.class == fieldType)) {
							field.set(entity, Integer.parseInt(c));
						} else if ((Long.TYPE == fieldType)
								|| (Long.class == fieldType)) {
							field.set(entity, Long.valueOf(c));
						} else if ((Float.TYPE == fieldType)
								|| (Float.class == fieldType)) {
							field.set(entity, Float.valueOf(c));
						} else if ((Short.TYPE == fieldType)
								|| (Short.class == fieldType)) {
							field.set(entity, Short.valueOf(c));
						} else if ((Double.TYPE == fieldType)
								|| (Double.class == fieldType)) {
							field.set(entity, Double.valueOf(c));
						} else if (Character.TYPE == fieldType) {
							if ((c != null) && (c.length() > 0)) {
								field.set(entity,
										Character.valueOf(c.charAt(0)));
							}
						}

					}
					if (entity != null) {
						list.add(entity);
					}
				}
			}

		} catch (IOException e) {
			Exceptions.printException(e);;
		} catch (InstantiationException e) {
			Exceptions.printException(e);;
		} catch (IllegalAccessException e) {
			Exceptions.printException(e);;
		} catch (IllegalArgumentException e) {
			Exceptions.printException(e);;
		}
		return list;
	}


    /**
     * 将集合数组长度小于65535的数据导出到excel中
     * @param list
     * @param sheetName
     * @param output
     * @param isTagValue
     * @return
     */
	public boolean exportExcel(List<T> list, String sheetName, OutputStream output, String isTagValue) {
		return exportExcelList(list, sheetName, 65535, output,isTagValue);
	}
	
	/**
	 * controller里传递过来的list的元素大于65535就需要在这里分离成多个list然后放在集合中再调用导出方法。
	 * 为什么是65535？
	 * excel的sheet页支持65536行
	 * 第一行被表头占用，所以都用65536-1=65535来表示sheetSize。
	 * @param list
	 * @param sheetName
	 * @param rowSize
	 * @param output
	 * @param resu
	 * @return
	 */
    public boolean exportExcelList(List<T> list, String sheetName, int rowSize,
            OutputStream output,String resu) {  
        if (rowSize > 65535 || rowSize < 1) {
            rowSize = 65535;
        }
        double sheetNo = Math.ceil(list.size() / rowSize)+1;// 取出一共有多少个sheet.
        String sheetNames[] = new String[(int) sheetNo];
        List<T>[]lists=new ArrayList[(int) sheetNo];
        sheetNames[0] = sheetName;
        for(int i=1;i<sheetNo;i++){
        	sheetNames[i] = sheetName + i; 
        }
        for(int i=0;i<sheetNo;i++){
        	int startNo = i * rowSize;
            int endNo = Math.min(startNo + rowSize, list.size());
            List listi = new ArrayList();
            for (int j = startNo; j < endNo; j++) {
            	listi.add(list.get(j));
			}
            lists[i] = listi;
        }
        return exportExcel(lists,sheetNames,output,resu);
    }

	
	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 * 
	 * @param sheetName
	 *            工作表的名称
	 * @param output
	 *            java输出流
	 */
	public boolean exportExcel(List<T> lists[], String sheetNames[], OutputStream output, String isTagValue) {
		
		if (lists.length != sheetNames.length) {
			System.out.println("数组长度不一致");
			return false;
		}

		HSSFWorkbook workbook = new HSSFWorkbook();// 产生工作薄对象

		for (int ii = 0; ii < lists.length; ii++) {
			List<T> list = lists[ii];
			String sheetName = sheetNames[ii];

			List<Field> fields = getMappedFiled(clazz, null, isTagValue);

			HSSFSheet sheet = workbook.createSheet();// 产生工作表对象

			workbook.setSheetName(ii, sheetName);

			HSSFRow row;
			HSSFCell cell;// 产生单元格
			HSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
			style.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框 
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中 
			Font font = workbook.createFont();
		    font.setFontName("微软雅黑");
		    font.setFontHeightInPoints((short)14); //字体大小
		    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		    style.setFont(font);
			row = sheet.createRow(0);// 产生一行
			int intArray[]=new int[fields.size()];//定义列宽
			// 写入各个字段的列头名称
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
				int col = getExcelCol(attr.column());// 获得列号
				cell = row.createCell(col);// 创建列
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);// 设置列中写入内容为String类型
				cell.setCellValue(attr.name());// 写入列名
				int iLength = attr.name().getBytes().length*256*2;
				intArray[i] = iLength;//初始化列宽
				sheet.setColumnWidth(i, iLength);//手动设置列宽
				// 如果设置了提示信息则鼠标放上去提示.
				if (!attr.prompt().trim().equals("")) {
					setHSSFPrompt(sheet, "", attr.prompt(), 1, 100, col, col);// 这里默认设了2-101列提示.
				}
				// 如果设置了combo属性则本列只能选择不能输入
				if (attr.combo().length > 0) {
					setHSSFValidation(sheet, attr.combo(), 1, 100, col, col);// 这里默认设了2-101列只能选择不能输入.
				}
				cell.setCellStyle(style);
			}

			int startNo = 0;
			int endNo = list.size();
			// 写入各条记录,每条记录对应excel表中的一行
			for (int i = startNo; i < endNo; i++) {
				row = sheet.createRow(i + 1 - startNo);				
				T vo = (T) list.get(i); // 得到导出对象.
				for (int j = 0; j < fields.size(); j++) {
					Field field = fields.get(j);// 获得field.
					field.setAccessible(true);// 设置实体类私有属性可访问
					ExcelVOAttribute attr = field
							.getAnnotation(ExcelVOAttribute.class);
					try {
						// 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
						if (attr.isExport()) {
							cell = row.createCell(getExcelCol(attr.column()));// 创建cell
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellValue(field.get(vo) == null ? ""
									: String.valueOf(field.get(vo)));// 如果数据存在就填入,不存在填入空格.
							if(field.get(vo) != null){
								String value = field.get(vo)+"";
								//int iLength = value.getBytes().length*256*2;
								int iLength = value.length()*256%2;
								if(iLength>intArray[j]){
									intArray[j] = iLength;//设置列宽
									sheet.setColumnWidth(j, iLength);//手动设置列宽
								}
							}
							font.setFontHeightInPoints((short)12); //字体大小
							font.setBoldweight(Font.BOLDWEIGHT_NORMAL);		
							style.setFont(font);
							cell.setCellStyle(style);
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		try {
			output.flush();
			workbook.write(output);
			output.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Output is closed ");
			return false;
		}

	}


//	public boolean exportExcel(Collection<T> list, String sheetName, int sheetSize,
//			OutputStream output,String resu) {
//		Collection<T>[] lists = new ArrayCollection[1];
//		lists[0] = list;
//
//		String[] sheetNames = new String[1];
//		sheetNames[0] = sheetName;
//
//		return exportExcel(lists, sheetNames, output);
//	}
	
	/**
	 * 将EXCEL中A,B,C,D,E列映射成0,1,2,3
	 * 
	 * @param col
	 */
	public static int getExcelCol(String col) {
		col = col.toUpperCase();
		// 从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。
		int count = -1;
		char[] cs = col.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			count += (cs[i] - 64) * Math.pow(26, cs.length - 1 - i);
		}
		return count;
	}

	/**
	 * 设置单元格上提示
	 * 
	 * @param sheet
	 *            要设置的sheet.
	 * @param promptTitle
	 *            标题
	 * @param promptContent
	 *            内容
	 * @param firstRow
	 *            开始行
	 * @param endRow
	 *            结束行
	 * @param firstCol
	 *            开始列
	 * @param endCol
	 *            结束列
	 * @return 设置好的sheet.
	 */
	public static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle,
			String promptContent, int firstRow, int endRow, int firstCol,
			int endCol) {
		// 构造constraint对象
		DVConstraint constraint = DVConstraint
				.createCustomFormulaConstraint("DD1");
		// 四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow,
				endRow, firstCol, endCol);
		// 数据有效性对象
		HSSFDataValidation data_validation_view = new HSSFDataValidation(
				regions, constraint);
		data_validation_view.createPromptBox(promptTitle, promptContent);
		sheet.addValidationData(data_validation_view);
		return sheet;
	}

	/**
	 * 设置某些列的值只能输入预制的数据,显示下拉框.
	 * 
	 * @param sheet
	 *            要设置的sheet.
	 * @param textlist
	 *            下拉框显示的内容
	 * @param firstRow
	 *            开始行
	 * @param endRow
	 *            结束行
	 * @param firstCol
	 *            开始列
	 * @param endCol
	 *            结束列
	 * @return 设置好的sheet.
	 */
	public static HSSFSheet setHSSFValidation(HSSFSheet sheet,
			String[] textlist, int firstRow, int endRow, int firstCol,
			int endCol) {
		// 加载下拉列表内容
		DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
		// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow,
				endRow, firstCol, endCol);
		// 数据有效性对象
		HSSFDataValidation data_validation_list = new HSSFDataValidation(
				regions, constraint);
		sheet.addValidationData(data_validation_list);
		return sheet;
	}

	/**
	 * 得到实体类所有通过注解映射了数据表的字段
	 * 
	 * @param map
	 * @return
	 */
	private List<Field> getMappedFiled(@SuppressWarnings("rawtypes") Class clazz, List<Field> fields, String isTagValue) {
		if (fields == null) {
			fields = new ArrayList<Field>();
		}

		Field[] allFields = clazz.getDeclaredFields();// 得到所有定义字段
		// 得到所有field并存放到一个list中.
		for (Field field : allFields) {
			if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
				ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
				String [] tag = attr.isTag();
            	if(tag.length>0 && isTagValue != null){
            		for(String s :tag){
            			if(s.equals(isTagValue)){
        					fields.add(field);
        				}
            		}
            	}
            	else{	
    				fields.add(field);              			
    			}
			}
		}
		if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
			getMappedFiled(clazz.getSuperclass(), fields, isTagValue);
		}

		return fields;
	}
}
