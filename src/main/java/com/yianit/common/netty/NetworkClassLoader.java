
/**
 * Project Name:ActiveUpdate
 * File Name:NetworkClassLoader.java
 * Package Name:com.zenitoo.lqgs.active.classloader
 * Date:2018年1月9日上午10:57:02
 * Copyright (c) 2018, chenzhou1025@126.com All Rights Reserved.
 * 
 */

package com.yianit.common.netty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 类名称: NetworkClassLoader
 * 类描述: TODO 填写类描述.
 * 创建人: zhangw
 * 创建时间: 2018年1月9日 上午10:57:02
 * 修改人: zhangw
 * 修改时间: 2018年1月9日 上午10:57:02
 * 修改备注:
 * 
 * @see
 */
public class NetworkClassLoader extends ClassLoader {
    private String rootUrl;// http://localhost:8090/Test.class

    public NetworkClassLoader(String rootUrl, ClassLoader parent) {
        super(parent);
        this.rootUrl = rootUrl;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = null;// this.findLoadedClass(name); // 父类已加载
        // if (clazz == null) { //检查该类是否已被加载过
        byte[] classData = getClassData(); // 根据类的二进制名称,获得该class文件的字节码数组
        if (classData == null) {
            throw new ClassNotFoundException();
        }
        clazz = defineClass(name, classData, 0, classData.length); // 将class的字节码数组转换成Class类的实例
        // }
        return clazz;
    }

    private byte[] getClassData() {
        InputStream is = null;
        try {
            URL url = new URL("file:///" + rootUrl);
            byte[] buff = new byte[1024 * 4];
            int len = -1;
            is = url.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = is.read(buff)) != -1) {
                baos.write(buff, 0, len);
            }
            return baos.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
