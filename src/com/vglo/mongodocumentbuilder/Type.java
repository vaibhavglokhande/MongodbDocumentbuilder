package com.vglo.mongodocumentbuilder;


/**
 * This enum is used to define the type of data stored in mongodb documents. 
 * VALUE: value is single object.
 * ARRAY: value is array of objects.
 * DOCUMENT: value is embedded documents.
 * */
public enum Type {
	VALUE,ARRAY,DOCUMENT
}
