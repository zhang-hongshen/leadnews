package com.zhanghongshen.minio;

import org.springframework.minio.MinioTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MinioTest {

    @Autowired
    private MinioTemplate minioTemplate;

    @Test
    public void testPutObject() throws Exception {
        try {
            InputStream inputStream = MinioTest.class.getClassLoader().getResourceAsStream("1.html");
            minioTemplate.uploadImageFile("", "1.html", inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
