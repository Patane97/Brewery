package com.Patane.Brewery.util;

import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.Msg;

public class ErrorHandler {
	public static <T> T optionalLoadError(Msg msgType, Importance importance, String error) throws BrLoadException{
		switch(importance){
		case NONE:
			break;
		case ERROR:
			Messenger.send(msgType, error);
			break;
		case REQUIRED:
			throw new BrLoadException(error);
		}
		return null;
	}
	public static class BrLoadException extends Exception{
		private static final long serialVersionUID = 1L;

		public BrLoadException(String message){
			super(message);
		}
	}
	public static enum Importance {
		NONE{}, ERROR{}, REQUIRED{};
	}
}
