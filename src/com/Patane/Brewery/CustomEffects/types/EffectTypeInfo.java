package com.Patane.Brewery.CustomEffects.types;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EffectTypeInfo {
	public String name();
}