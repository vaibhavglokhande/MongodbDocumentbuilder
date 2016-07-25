package com.vglo.mongodocumentbuilder;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.TYPE })
/**
 * Key:Key with which data is to be inserted. If no value is specified, name of
 * field will be used.
 */
public @interface Item {
	public String key() default "NO_KEY";

	public Type type() default Type.VALUE;
}
