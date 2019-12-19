package com.yianit.common.util;

import java.nio.ByteBuffer;

public class HexUtil {
	private static final char[] HEX_CHAR_TABLE = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
	public static byte[] hexStr2Byte(String hex) {
		ByteBuffer bf = ByteBuffer.allocate(hex.length() / 2);
		for (int i = 0; i < hex.length(); i++) {
			String hexStr = hex.charAt(i) + "";
			i++;
			hexStr += hex.charAt(i);
			byte b = (byte) Integer.parseInt(hexStr, 16);
			bf.put(b);
		}
		return bf.array();
	}

	public static int byteToInt(byte b) {
		int x = b & 0xff;
		return x;
	}

	public static String intToHex(int value) {
		return Integer.toHexString(value);
	}

	public static byte[] toHH(int n) {  
		  byte[] b = new byte[4];  
		  b[3] = (byte) (n & 0xff);  
		  b[2] = (byte) (n >> 8 & 0xff);  
		  b[1] = (byte) (n >> 16 & 0xff);  
		  b[0] = (byte) (n >> 24 & 0xff);  
		  return b;  
	}
	public int toInt(byte[] b){
	    int res = 0;
	    for(int i=0;i<b.length;i++){
	        res += (b[i] & 0xff) << (i*8);
	    }
	    return res;
	}
	public static String byteToHex(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

	public static void main(String[] args) {
		System.out.println(HexUtil.byteToHex(HexUtil.toHH(36)));
		System.out.println(HexUtil.intToHex(3600));
		System.out.println(HexUtil.byteToHex(new byte[] { -52, -52 }));
		long l = System.currentTimeMillis();
		System.out.println(HexUtil.byteToHex(("12181624").getBytes()));
	}
}
