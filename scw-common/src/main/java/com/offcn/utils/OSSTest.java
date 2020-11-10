package com.offcn.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OSSTest {

    /**
     * OSS 使用步骤 阿里云
     * 1）、引入SDK
     * 2）、配置好相应的属性
     */
    public static void main(String[] args)throws IOException {
        // Endpoint以北京为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = "LTAI4GJc3rGr9QEqRURTX3DF";
        String accessKeySecret = "fwswAyBYQBbCTuXSuSG763D45FpO8t";
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 上传文件流。
        InputStream inputStream = new FileInputStream(new File("D:\\007.gif"));
        ossClient.putObject("offcn-parent", "pic/008.gif", inputStream);
        // 关闭OSSClient。
        ossClient.shutdown();

        System.out.println("测试完成");
    }
}
