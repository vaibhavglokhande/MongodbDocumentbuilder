package com.vglo.mongodocumentbuilder;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.TYPE })
public @interface Index {

	public IndexType index() default IndexType.ASCENDING;

	public boolean background() default false;

	public boolean unique() default false;

	//public String name();

	//public boolean dropDups() default false;

	public boolean sparse() default false;

	//public int expiresAfterSeconds();
}
