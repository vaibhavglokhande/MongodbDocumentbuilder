package com.vglo.mongodocumentbuilder;

import java.lang.reflect.Field;

public class AnnotationReader {

	public boolean isDocument(Object object){
		MongoDocumentClass annotation = object.getClass().getAnnotation(MongoDocumentClass.class);
		if(annotation!=null){
			return true;
		}
		return false;
	}
	
	public Item getItem (Field field){
		Item annotation = field.getAnnotation(Item.class);
		return annotation;
	}
	
}
