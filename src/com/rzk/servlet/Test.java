package com.rzk.servlet;

import java.util.UUID;

public class Test {

    public void test() {
//        String str="form-data; name=\"file\"; filename=\"D:\\鎴戠殑鏂囨。\\妗岄潰\\18-鑺\uE1BE\uE6A2妤?.docx\"";
//        String substring = str.substring(34,str.length()-1);
//        String gbk = new String(substring.getBytes("GBK"), "UTF-8");
//        System.out.println(substring);

        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid);
    }
}
