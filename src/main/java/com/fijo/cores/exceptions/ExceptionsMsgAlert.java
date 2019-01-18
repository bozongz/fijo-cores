package com.fijo.cores.exceptions;

import java.lang.reflect.Field;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinColumn;

import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.ReferenceTable;
import com.fijo.cores.utils.annotations.ReferenceTables;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.ReferenceTable;
import com.fijo.cores.utils.annotations.ReferenceTables;

public class ExceptionsMsgAlert {
	
	@SuppressWarnings("rawtypes")
	private static Map<Class,Map<String,String>> references = Maps.newHashMap(); //外键约束异常缓存
	@SuppressWarnings("rawtypes")
	private static Map<Class,Map<String,String>> notNulls = Maps.newHashMap(); //必填字段异常缓存
	
	/**
	 * 唯一键重复约束异常
	 * @param clazz
	 * @param errorMessage
	 * @return
	 */
	public static String alertDuplicateKeyException(@SuppressWarnings("rawtypes") Class clazz, String errorMessage){
		String ret = Constants.EMPTY;
		Field[] fields = clazz.getDeclaredFields();
		for(Field f : fields){
			if(StringUtils.contains(errorMessage, "key"+Constants.SPACE+Constants.SQUOTE+f.getName()+Constants.SQUOTE)){
				NotNullColumn columnMeaning = f.getAnnotation(NotNullColumn.class);
				ret = columnMeaning.value();
				break;
			}
		}
		return ret;
	}
	
	/**
	 * 必填字段约束异常 与 外键删除约束异常
	 * @param clazz
	 * @param errorMessage
	 * @return
	 */
	public static String alertDataIntegrityViolationException(@SuppressWarnings("rawtypes") Class clazz, String errorMessage){
		String ret = Constants.EMPTY;
		// 判断外键删除约束异常
		if(StringUtils.contains(errorMessage,"foreign key constraint fails")){			
			if(references.get(clazz) == null){
				@SuppressWarnings("unchecked")
                ReferenceTables tables = (ReferenceTables) clazz.getAnnotation(ReferenceTables.class);
				if(tables != null){
					ReferenceTable[] joinTables = tables.joinTables();
					Map<String,String> kvs = Maps.newHashMap();
					for(ReferenceTable j : joinTables){
						kvs.put(j.table(), j.value());				
					}
					references.put(clazz, kvs);
				}
			}
			if(references.get(clazz) != null){
				String referenceTable = errorMessage.substring(errorMessage.indexOf(".`"), errorMessage.indexOf(",")).replace(".`", "").replace("`", "");
				ret = references.get(clazz).get(referenceTable);
			}
		}
		//判断必填字段约束异常
		else if(StringUtils.contains(errorMessage, "cannot be null") || StringUtils.contains(errorMessage, "doesn't have a default value")){
			if(notNulls.get(clazz) == null){
				Map<String,String> kvs = Maps.newHashMap();
				Field[] fields = clazz.getDeclaredFields();
				for(Field f : fields){
					NotNullColumn nnColumn = f.getAnnotation(NotNullColumn.class);
					Column column = f.getAnnotation(Column.class);					
					JoinColumn joinColumn = f.getAnnotation(JoinColumn.class);
					if(nnColumn !=null && column != null){
						kvs.put(column.name(), nnColumn.value());
					}
					if(nnColumn !=null && joinColumn != null){
						kvs.put(joinColumn.name(), nnColumn.value());
					}
				}
				notNulls.put(clazz, kvs);
			}
			String notNullColumn = Constants.EMPTY;
			if(StringUtils.contains(errorMessage, "cannot be null")){
				notNullColumn = errorMessage.substring(errorMessage.indexOf("Column '")+8, errorMessage.indexOf("cannot be null")-2);
				
			}
			else if(StringUtils.contains(errorMessage, "doesn't have a default value")){
				notNullColumn = errorMessage.substring(errorMessage.indexOf("Field '")+7, errorMessage.indexOf("doesn't have a default value")-2);
			}
			if(!StringUtils.isEmpty(notNullColumn))
				ret = notNulls.get(clazz).get(notNullColumn);
		}
		return ret;
	}
	
	public static void main(String[] args) {
		//String str1 = "Cannot delete or update a parent row: a foreign key constraint fails (`cores`.`wm_matl_type`, CONSTRAINT `FKD3B9BE643BE818BF` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)); nested exception is java.sql.BatchUpdateException: Cannot delete or update a parent row: a foreign key constraint fails (`cores`.`wm_matl_type`, CONSTRAINT `FKD3B9BE643BE818BF` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)) nested exception is java.sql.BatchUpdateException:Cannot delete or update a parent row: a foreign key constraint fails (`cores`.`wm_matl_type`, CONSTRAINT `FKD3B9BE643BE818BF` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`))";
		String str2 = "Cannot delete or update a parent row: a foreign key constraint fails (`cores`.`sys_user_role`, CONSTRAINT `FK660C51783BE818BF` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)); nested exception is java.sql.BatchUpdateException: Cannot delete or update a parent row: a foreign key constraint fails (`cores`.`sys_user_role`, CONSTRAINT `FK660C51783BE818BF` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)) nested exception is java.sql.BatchUpdateException:Cannot delete or update a parent row: a foreign key constraint fails (`cores`.`sys_user_role`, CONSTRAINT `FK660C51783BE818BF` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`))";
		String str = str2.substring(str2.indexOf(".`"), str2.indexOf(",")).replace(".`", "").replace("`", "");
		String str3 = " SQL []; Column 'phone' cannot be null; nested exception is com.mysql.jdbc.exceptions";
		str = str3.substring(str3.indexOf("Column '")+8, str3.indexOf("cannot be null")-2);
		String str4 = "Cause: java.sql.SQLException: Field 'employee_id' doesn't have a default value";
		str = str4.substring(str4.indexOf("Field '")+7, str4.indexOf("doesn't have a default value")-2);
		System.out.println(str);
	}
}
