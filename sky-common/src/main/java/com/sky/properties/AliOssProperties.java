package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置字段
 */
@Component
@ConfigurationProperties(prefix = "sky.alioss") // sky.alioss配置文件中的属性值与Java类的字段绑定
@Data //Lombok库提供的注解，用于自动生成Java类的常见方法，如getter、setter、toString等。它简化了Java类的编写
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
