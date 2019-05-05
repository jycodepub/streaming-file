package com.jyeh.streaming.streaming.file;

import com.emc.ecs.connector.S3ServiceInfo;
import com.emc.ecs.connector.spring.S3Connector;
import com.emc.ecs.connector.spring.S3ServiceConnectorCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class EcsConfig {

    @Value("${ecs.endpoint}")
    private String ecsEndpoint;
    @Value("${ecs.user}")
    private String ecsUser;
    @Value("${ecs.password}")
    private String ecsPassword;
    @Value("${ecs.bucket}")
    private String bucket;

    @Bean
    public S3Connector s3Connector() {
        S3ServiceConnectorCreator serviceConnectorCreator = new S3ServiceConnectorCreator();
        S3ServiceInfo s3ServiceInfo = new S3ServiceInfo("1", ecsUser, ecsPassword, ecsEndpoint, bucket);
        return serviceConnectorCreator.create(s3ServiceInfo, null);
    }
}
