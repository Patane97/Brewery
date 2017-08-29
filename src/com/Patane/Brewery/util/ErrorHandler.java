package com.Patane.Brewery.util;

import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.Msg;

public class ErrorHandler {
	public static <T> T optionalLoadError(Msg msgType, boolean optional, String error) throws BrLoadException{
		if(!optional)
			throw new BrLoadException(error);
		Messenger.send(msgType, error);
		return null;
	}
	public static class BrLoadException extends Exception{
		private static final long serialVersionUID = 1L;

		public BrLoadException(String message){
			super(message);
		}
	}
}
