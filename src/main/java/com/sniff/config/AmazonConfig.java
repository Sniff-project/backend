package com.sniff.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AmazonConfig {
    @Value("${aws.bucket.access-key}")
    private String BUCKET_ACCESS_KEY;
    @Value("${aws.bucket.secret-key}")
    private String BUCKET_SECRET_KEY;
    @Value("${aws.bucket.region}")
    private String BUCKET_REGION;

    @Bean
    public AmazonS3 S3(){
        AWSCredentials awsCredentials = new BasicAWSCredentials(
                BUCKET_ACCESS_KEY,
                BUCKET_SECRET_KEY
        );
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(BUCKET_REGION)
                .build();
    }
}
