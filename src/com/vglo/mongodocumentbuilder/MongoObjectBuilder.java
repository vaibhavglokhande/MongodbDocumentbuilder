package com.vglo.mongodocumentbuilder;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

public class MongoObjectBuilder<TObject> {

	AnnotationReader annotationReader;
	Class<TObject> clazz;
	private static Map<String, Class> primitiveArrayTypes;

	/**
	 * @param clazz Class of the object to be created.
	 * */
	public MongoObjectBuilder(Class<TObject> clazz) {
		annotationReader = new AnnotationReader();
		this.clazz = clazz;
		if (primitiveArrayTypes == null) {
			primitiveArrayTypes = new HashMap<>();
			primitiveArrayTypes.put("int[]", Integer.TYPE);
			primitiveArrayTypes.put("long[]", Long.TYPE);
			primitiveArrayTypes.put("double[]", Double.TYPE);
			primitiveArrayTypes.put("float[]", Float.TYPE);
			primitiveArrayTypes.put("boolean[]", Boolean.TYPE);
			primitiveArrayTypes.put("char[]", Character.TYPE);
			primitiveArrayTypes.put("byte[]", Byte.TYPE);
			primitiveArrayTypes.put("short[]", Short.TYPE);
		}
	}

	/**
	 * @param document Document which is to be mapped to the object.
	 * @return Object mapped to the provided document.
	 * */
	public TObject buildObject(Document document) {
		TObject tObject = null;
		try {
			tObject = clazz.newInstance();
			if (annotationReader.isDocument(tObject) && document != null) {
				for (Field field : tObject.getClass().getDeclaredFields()) {
					field.setAccessible(true);
					Class<Object> forName;
					Item item = annotationReader.getItem(field);
					if (item != null) {
						MongoObjectBuilder<Object> mongoObjectBuilder;
						String key = item.key();
						if (key.equals("NO_KEY")) {
							key = field.getName();
						}
						switch (item.type()) {
						case ARRAY:
							Type genericType = field.getGenericType();
							if (genericType instanceof ParameterizedType) {
								ParameterizedType parameterizedType = (ParameterizedType) genericType;
								for (Type type : parameterizedType.getActualTypeArguments()) {
									forName = (Class<Object>) Class.forName(type.getTypeName());
									Object newInstance = forName.newInstance();
									if (annotationReader.isDocument(newInstance)) {
										ArrayList<Document> documentList = (ArrayList<Document>) document.get(key);
										List<Object> list = new ArrayList<>();
										mongoObjectBuilder = new MongoObjectBuilder<>(forName);
										if (documentList != null) {
											for (Document tempDoc : documentList) {
												list.add(mongoObjectBuilder.buildObject(tempDoc));
											}
										}
										field.set(tObject, list);
									} else {
										ArrayList<?> arrayList = (ArrayList<?>) document.get(key);
										field.set(tObject, arrayList);
									}
								}
							} else {
								if (hasPrimitiveType(genericType.getTypeName())) {
									ArrayList<?> arrayList = (ArrayList<?>) document.get(key);
									if (arrayList != null) {
										switch (genericType.getTypeName()) {
										case "int[]":
											int ints[] = new int[arrayList.size()];
											for (int i = 0; i < arrayList.size(); i++)
												ints[i] = (int) arrayList.get(i);
											field.set(tObject, ints);
											break;
										case "long[]":
											long longs[] = new long[arrayList.size()];
											for (int i = 0; i < arrayList.size(); i++)
												longs[i] = (long) arrayList.get(i);
											field.set(tObject, longs);
											break;
										case "double[]":
											double doubles[] = new double[arrayList.size()];
											for (int i = 0; i < arrayList.size(); i++)
												doubles[i] = (double) arrayList.get(i);
											field.set(tObject, doubles);
											break;
										case "float[]":
											float floats[] = new float[arrayList.size()];
											for (int i = 0; i < arrayList.size(); i++)
												floats[i] = (float) arrayList.get(i);
											field.set(tObject, floats);
											break;
										case "boolean[]":
											boolean booleans[] = new boolean[arrayList.size()];
											for (int i = 0; i < arrayList.size(); i++)
												booleans[i] = (boolean) arrayList.get(i);
											field.set(tObject, booleans);
											break;
										case "char[]":
											char chars[] = new char[arrayList.size()];
											for (int i = 0; i < arrayList.size(); i++)
												chars[i] = (char) arrayList.get(i);
											field.set(tObject, chars);
											break;
										case "byte[]":
											byte bytes[] = new byte[arrayList.size()];
											for (int i = 0; i < arrayList.size(); i++)
												bytes[i] = (byte) arrayList.get(i);
											field.set(tObject, bytes);
											break;
										case "short[]":
											short shorts[] = new short[arrayList.size()];
											for (int i = 0; i < arrayList.size(); i++)
												shorts[i] = (short) arrayList.get(i);
											field.set(tObject, shorts);
											break;
										}
									}
								} else {
									field.set(tObject, (Object[]) document.get(key));
								}
							}
							break;
						case DOCUMENT:
							forName = (Class<Object>) Class.forName(field.getGenericType().getTypeName());
							mongoObjectBuilder = new MongoObjectBuilder<>(forName);
							field.set(tObject, mongoObjectBuilder.buildObject((Document) document.get(key)));
							break;
						case VALUE:
							field.set(tObject, document.get(key));
							break;
						default:
							break;

						}
					}
				}
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return tObject;
	}

	private boolean hasPrimitiveType(String type) {
		if (primitiveArrayTypes.containsKey(type))
			return true;
		return false;
	}
}
