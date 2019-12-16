package com.yianit.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class YianFileUtil {
	public static String readString2(String path) {
		StringBuffer str = new StringBuffer("");
		File file = new File(path);
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			int ch = 0;
			while ((ch = fr.read()) != -1) {
				// System.out.print((char) ch + " ");
				str.append((char) ch);
			}
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("File reader出错");
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return str.toString();
	}

	public static void write(String path, String content) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(path));
			pw.println(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (pw != null) {
				try {
					pw.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 删除文件下所有文件夹和文件 file：文件名
	 */
	public static void deleteFileAll(File file) {
		if (file.exists()) {
			File files[] = file.listFiles();
			int len = files.length;
			for (int i = 0; i < len; i++) {
				if (files[i].isDirectory()) {
					deleteFileAll(files[i]);
				} else {
					files[i].delete();
				}
			}
			file.delete();
		}
	}

	/**
	 * 复制单个文件
	 *
	 * @param srcFile
	 *            包含路径的原文件，如：E:/phsftp/src/abc.txt
	 * @param dirDest
	 *            目标文件目录；若文件目录不存在则自动创建
	 * @throws Exception
	 */
	public static void copyFile(String srcFile, String dirDest) throws Exception {
		FileOutputStream out = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(srcFile);
			// mkdir(dirDest);
			File pf = new File(dirDest);
			if (!pf.exists()) {
				pf.mkdirs();
			}
			File f = new File(dirDest + "/" + new File(srcFile).getName());
			if(f.exists()){
				f.delete();
			}
			f.createNewFile();
			out = new FileOutputStream(dirDest + "/" + new File(srcFile).getName());
			int len;
			byte buffer[] = new byte[1024];
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.flush();

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				// LOG.error(e);
			}

		}
	}

	public static void main(String[] args) {
		File f = new File("F:/boot/test");
		File fo = new File("F:/boot/test/online");
		if(fo.exists()){
			YianFileUtil.deleteFileAll(fo);
		}
		int startport = 8000;
		for(int i=100;i>0;i--){
			f = new File("F:/boot/test/TestCoder"+i+".class");
			fo = new File("F:/boot/test/online/"+startport);
			if(fo.exists()){
				YianFileUtil.deleteFileAll(fo);
			}
			fo.mkdirs();
			try {
				YianFileUtil.copyFile("F:/boot/test/TestCoder"+i+".class", "F:/boot/test/online/"+startport);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			startport--;
		}
	}
}
