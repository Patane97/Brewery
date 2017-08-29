package com.Patane.Brewery.util;

import java.util.List;
import java.util.StringJoiner;

public class StringUtilities {
	public static String formaliseString(String string) {
		string = string.toLowerCase();
		string = string.substring(0, 1).toUpperCase() + string.substring(1);
		return string;
	}
	public static String stringJoiner(List<String> strings, String delimiter) {
		return stringJoiner(strings.toArray(new String[0]), delimiter);
	}
	public static String stringJoiner(String[] strings, String delimiter) {
		return stringJoiner(strings, new StringJoiner(delimiter));
	}
	public static String stringJoiner(List<String> strings, String delimiter, String prefix, String suffix) {
		return stringJoiner(strings.toArray(new String[0]), new StringJoiner(delimiter, prefix, suffix));
	}
	public static String stringJoiner(String[] strings, String delimiter, String prefix, String suffix) {
		return stringJoiner(strings, new StringJoiner(delimiter, prefix, suffix));
	}
	private static String stringJoiner(String[] strings, StringJoiner stringJoiner){
		for(String string : strings){
			stringJoiner.add(string);
		}
		return stringJoiner.toString();
	}
	public static String normalize(String string) {
		return string.replace(" ", "_").toUpperCase();
	}
}
