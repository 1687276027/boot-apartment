package com.atguigu.lease.common.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    /*//第一种映射方式：该配置类会在配置文件中匹配对应的value值进行映射到该变量
    @Value("${minio.endpoint}")*/

    //第二种方式：在配置类中配置好，直接扫描该Properties类进参数映射
    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;
}
