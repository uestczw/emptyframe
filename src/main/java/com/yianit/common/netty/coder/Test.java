package com.yianit.common.netty.coder;

import java.util.Arrays;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte[] data = new byte[]{0,1,2,3,4,5};
		byte[] cache = Arrays.copyOfRange(data, 2, 3);
		System.out.println(cache);
	}

}
