package com.vglo.mongodocumentbuilder;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.ReflectionDBObject;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

public class MongoIndexBuilder<TObject> {

	AnnotationReader annotationReader;

	public MongoIndexBuilder() {
		annotationReader = new AnnotationReader();
	}

	/**
	 * @deprecated Use {@link #buildIndex(TObject,String)} instead
	 * @param object
	 *            Object of class with @MongoDocumentClass to build indexes.
	 */
	public List<IndexModel> buildIndex(TObject object) {
		return buildIndex(object, null);
	}

	/**
	 * @param object
	 *            Object of class with @MongoDocumentClass to build index.
	 * @param parentKey
	 *            Key of parent Document for nested documents.
	 */
	public List<IndexModel> buildIndex(TObject object, String parentKey) {
		List<IndexModel> indexModels = new ArrayList<>();
		if (object != null && annotationReader.isDocument(object)) {
			for (Field field : object.getClass().getDeclaredFields()) {
				Bson bson = null;
				IndexOptions indexOptions = new IndexOptions();
				field.setAccessible(true);
				Item item = annotationReader.getItem(field);
				if (item != null) {
					String key = "";
					if (parentKey != null) {
						key = parentKey + ".";
					}
					if (item.key().equals("NO_KEY")) {
						key += field.getName();
					} else {
						key += item.key();
					}
					if (item.type() == Type.DOCUMENT) {
						try {
							Object nestedDocument = field.getType().newInstance();
							MongoIndexBuilder<Object> indexBuilder = new MongoIndexBuilder<>();
							List<IndexModel> buildIndex = indexBuilder.buildIndex(nestedDocument, key);
							indexModels.addAll(buildIndex);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (item.type() == Type.ARRAY) {
						java.lang.reflect.Type genericType = field.getGenericType();
						if (genericType instanceof ParameterizedType) {
							ParameterizedType parameterizedType = (ParameterizedType) genericType;
							for (java.lang.reflect.Type type : parameterizedType.getActualTypeArguments()) {
								try {
									Class<?> forName = Class.forName(type.getTypeName());
									Object newInstance = forName.newInstance();
									if (annotationReader.isDocument(newInstance)) {
										MongoIndexBuilder<Object> indexBuilder = new MongoIndexBuilder<>();
										List<IndexModel> buildIndex = indexBuilder.buildIndex(newInstance, key);
										indexModels.addAll(buildIndex);
									}
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InstantiationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}
						/*
						 * java.lang.reflect.Type genericType =
						 * field.getGenericType(); ParameterizedType
						 * parameterizedType=(ParameterizedType)genericType;
						 * for(java.lang.reflect.Type
						 * type:parameterizedType.getActualTypeArguments())
						 * System.out.println(type.getTypeName());
						 */
					}
					Index index = annotationReader.getIndex(field);
					if (index != null) {
						switch (index.index()) {
						case ASCENDING:
							bson = Indexes.ascending(key);
							break;
						case DESCENDING:
							bson = Indexes.descending(key);
							break;
						case HASHED:
							bson = Indexes.hashed(key);
							break;
						case TEXT:
							bson = Indexes.text(key);
							break;
						case _2D:
							bson = Indexes.geo2d(key);
							break;
						case _2DSPHERE:
							bson = Indexes.geo2dsphere(key);
							break;
						default:
							break;

						}
						if (index.background())
							indexOptions.background(true);
						if (index.unique())
							indexOptions.unique(true);
						if (index.sparse())
							indexOptions.sparse(true);
						IndexModel indexModel = new IndexModel(bson, indexOptions);
						indexModels.add(indexModel);
					}
				}
			}
		}
		return indexModels;
	}
}
