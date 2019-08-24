package com.ptop.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class Utility {

	private static final char[] _hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static byte[] intToByteArray(int value) {
		byte[] result = ByteBuffer.allocate(4).putInt(value).array();
		return result;
	}

	public static int byteArrayToInt(byte[] b) {
		ByteBuffer buff = ByteBuffer.allocate(4);
		buff.asIntBuffer();
		
		int result = buff.getInt();
		
		return byteArrayToInt(b, 0);
		
		
	}
	
	public static String byteArrayToString(byte[] b) {
		String str = new String();
		try {
			str = new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
		
		
	}
	
	

	static String byteArrayToHexString(byte in[]) {
	
		    char[] hexChars = new char[in.length * 2];
		    for ( int j = 0; j < in.length; j++ ) {
		        int v = in[j] & 0xFF22;
		        hexChars[j * 2] = hexArray[v >>> 4];
		        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		    }
		   return new String(hexChars);
		
	}

	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		ByteBuffer wrapped = ByteBuffer.wrap(b); // big-endian by default
		value = wrapped.getInt();
		return value;
	}

	public static String getTime() {
		 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		 LocalDateTime now = LocalDateTime.now(); 
		 return dtf.format(now);
	}

	public static void isSleep() {
		try {
			Thread.currentThread();
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
		}
	}
	public static void isdelayed() {
		try {
			Thread.currentThread();
			Thread.sleep(100);
		} catch (InterruptedException ex) {
		}
	}

	public static byte[] decode(final String s) {
		int len = s.length();
		if (len % 2 != 0) {
			return null;
		}

		byte[] bytes = new byte[len / 2];
		int pos = 0;

		for (int i = 0; i < len; i += 2) {
			byte hi = (byte) Character.digit(s.charAt(i), 16);
			byte lo = (byte) Character.digit(s.charAt(i + 1), 16);
			bytes[pos++] = (byte) (hi * 16 + lo);
		}

		return bytes;
	}

	public static String encode(final byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length << 1);
		for (byte aByte : bytes) {
			sb.append(convertDigit(aByte >> 4));
			sb.append(convertDigit(aByte & 0x0f));
		}
		return sb.toString();
	}

	private static char convertDigit(final int value) {
		return _hex[value & 0x0f];
	}

	public static boolean isGzipped(byte[] bytes) {
		return bytes[0] == (byte) 0x1f && bytes[1] == (byte) 0x8b;
	}
}
