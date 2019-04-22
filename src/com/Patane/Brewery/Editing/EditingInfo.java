package com.Patane.Brewery.Editing;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.Patane.util.collections.PatCollectable;

@Retention(RetentionPolicy.RUNTIME)
public @interface EditingInfo {
	public Class<? extends PatCollectable> type();

}
