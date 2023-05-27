package com.nowcoder.community;

import java.io.IOException;

public class WKTests {

    // 在命令行中因为有配置path，所以可以直接使用命令
    // 但是在JAVA程序中要写出命令的完整路径
    public static void main(String[] args) {
        String cmd = "d:/install/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.csdn.net/  d:/work/data/wk-images/3.jpg";
        try {
            // 这行代码：会把对应的命令交给操作系统来执行。
            // 所以这一行所要执行的目标命令和下面一行命令是异步进行的，也就是执行的先后顺序不一定是从上到下的。
            Runtime.getRuntime().exec(cmd);
            System.out.println("成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
