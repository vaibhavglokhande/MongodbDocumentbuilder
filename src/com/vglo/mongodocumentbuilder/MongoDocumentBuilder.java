package com.vglo.mongodocumentbuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

public class MongoDocumentBuilder<TObject> {

	AnnotationReader annotationReader;
	private static Map<String, Class> primitiveArrayTypes;

	public MongoDocumentBuilder() {
		annotationReader = new AnnotationReader();
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
	 * This method generates document for mongodb insert method.
	 * 
	 * @param object
	 *            Object of class which is to be converted to Document.
	 * @return Document mapped from object.
	 */
	public Document buildDocument(TObject object) {
		Document document = new Document();
		if (object != null && annotationReader.isDocument(object)) {
			for (Field field : object.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Item item = annotationReader.getItem(field);
				if (item != null) {
					String key = item.key();
					if (key.equals("NO_KEY")) {
						key = field.getName();
					}
					switch (item.type()) {
					case ARRAY:
						MongoDocumentBuilder<Object> builderObject = new MongoDocumentBuilder<>();
						try {
							Object currentFieldValue = field.get(object);
							if (currentFieldValue != null) {
								List<Object> list = new ArrayList<>();
								if (currentFieldValue instanceof List) {
									List<Object> objects = (List<Object>) currentFieldValue;
									for (Object o : objects) {
										if (annotationReader.isDocument(o)) {
											list.add(builderObject.buildDocument(o));
										} else {
											list.add(o);
										}
									}
								} else if (currentFieldValue.getClass().isArray()) {
									if (hasPrimitiveType(currentFieldValue.getClass().getSimpleName())) {
										list = (List<Object>) buildPrimitiveArray(currentFieldValue);
									} else {
										Object[] objects = (Object[]) currentFieldValue;
										for (Object o : objects) {
											if (annotationReader.isDocument(o)) {
												list.add(builderObject.buildDocument(o));
											} else {
												list.add(o);
											}
										}
									}
								}
								document.append(key, list);
							}
						} catch (IllegalArgumentException | IllegalAccessException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}

						break;
					case DOCUMENT:
						MongoDocumentBuilder<Object> builderField = new MongoDocumentBuilder<>();
						try {
							Object obj = field.get(object);
							if (obj != null)
								document.put(key, builderField.buildDocument(obj));
						} catch (IllegalArgumentException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IllegalAccessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					case VALUE:
						try {
							document.put(key, field.get(object));
						} catch (IllegalArgumentException | IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
						break;
					}
				}
			}
		}
		return document;
	}

	private List<?> buildPrimitiveArray(Object object) {
		Class<?> primitiveClass = primitiveArrayTypes.get(object.getClass().getSimpleName());
		switch (primitiveClass.getSimpleName()) {
		case "int":
			List<Integer> integers = new ArrayList<>();
			int[] intData = (int[]) object;
			for (int i = 0; i < intData.length; i++) {
				integers.add(intData[i]);
			}
			return integers;
		case "long":
			List<Long> longs = new ArrayList<>();
			long[] longData = (long[]) object;
			for (int i = 0; i < longData.length; i++) {
				longs.add(longData[i]);
			}
			return longs;
		case "double":
			List<Double> doubles = new ArrayList<>();
			double[] doubleData = (double[]) object;
			for (int i = 0; i < doubleData.length; i++) {
				doubles.add(doubleData[i]);
			}
			return doubles;
		case "float":
			List<Float> floats = new ArrayList<>();
			float[] floatData = (float[]) object;
			for (int i = 0; i < floatData.length; i++) {
				floats.add(floatData[i]);
			}
			return floats;
		case "boolean":
			List<Boolean> booleans = new ArrayList<>();
			boolean[] booleanData = (boolean[]) object;
			for (int i = 0; i < booleanData.length; i++) {
				booleans.add(booleanData[i]);
			}
			return booleans;
		case "char":
			List<Character> characters = new ArrayList<>();
			char[] charData = (char[]) object;
			for (int i = 0; i < charData.length; i++) {
				characters.add(charData[i]);
			}
			return characters;
		case "byte":
			List<Byte> bytes = new ArrayList<>();
			byte[] byteData = (byte[]) object;
			for (int i = 0; i < byteData.length; i++) {
				bytes.add(byteData[i]);
			}
			return bytes;
		case "short":
			List<Short> shorts = new ArrayList<>();
			short[] shortData = (short[]) object;
			for (int i = 0; i < shortData.length; i++) {
				shorts.add(shortData[i]);
			}
			return shorts;
		}
		return null;
	}

	private boolean hasPrimitiveType(String type) {
		if (primitiveArrayTypes.containsKey(type))
			return true;
		return false;
	}

}
