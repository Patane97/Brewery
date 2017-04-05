package com.Patane.Brewery.util;

public class BrItem {
	/*
    Returns an encoded string that appears invisible to the
    client.
*/
public static String encodeItemData(String str){
    try {
        String hiddenData = "";
        for(char c : str.toCharArray()){
            hiddenData += "§" + c;
        }
        return hiddenData;
    }catch (Exception e){
        e.printStackTrace();
        return null;
    }
}

/*
    Decodes an encoded string
*/
public static String decodeItemData(String str){
    try {
        String[] hiddenData = str.split("(?:\\w{2,}|\\d[0-9A-Fa-f])+");
        String returnData = "";
        if(hiddenData == null){
            hiddenData = str.split("§");
            for(int i = 0; i < hiddenData.length; i++){
                returnData += hiddenData[i];
            }
            return returnData;
        }else{
            String[] d = hiddenData[hiddenData.length-1].split("§");
            for(int i = 1; i < d.length; i++){
                returnData += d[i];
            }
            return returnData;
        }

    }catch (Exception e){
        e.printStackTrace();
        return null;
    }
}
}
