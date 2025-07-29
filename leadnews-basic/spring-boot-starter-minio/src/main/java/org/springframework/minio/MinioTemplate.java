package org.springframework.minio;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.minio.config.MinIOConfig;
import org.springframework.minio.config.MinIOConfigProperties;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
@EnableConfigurationProperties(MinIOConfigProperties.class)
@ConditionalOnClass(MinioClient.class)
@RequiredArgsConstructor
public class MinioTemplate {

    private MinioClient minioClient;

    private MinIOConfigProperties minIOConfigProperties;

    /**
     * @param parentDir file's parent dictionary, e.g. images/upload
     * @param filename  e.g. yyyy/mm/dd/file.jpg
     * @return
     */
    private String buildFullFilepath(String parentDir, String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        return Paths.get(parentDir.isBlank() ? "" : parentDir, todayStr, filename).toString();
    }

    /**
     * @param filepath file full path e.g. http://minio/images/upload/1.jpg
     * @return e.g. images/upload/1.jpg
     */
    private String extractAbsolutePath(String filepath) {
        String bucketPath = Paths.get(minIOConfigProperties.getEndpoint(), minIOConfigProperties.getBucket()).toString();
        return filepath.replace(bucketPath,"");
    }

    public String uploadImageFile(String filename, InputStream inputStream) {
        return uploadImageFile("", filename, inputStream);
    }

    /**
     *  Upload Image File
     * @param parentDir  file's parent dictionary, e.g. images/upload
     * @param filename  filename, e.g  1.jpg
     * @param inputStream file input stream
     * @return full filepath e.g. http://minio/bucket/images/upload/1.jpg
     */
    public String uploadImageFile(String parentDir, String filename, InputStream inputStream) {
        String filePath = buildFullFilepath(parentDir, filename);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("image/jpg")
                    .bucket(minIOConfigProperties.getBucket())
                    .stream(inputStream,inputStream.available(),-1)
                    .build();
            minioClient.putObject(putObjectArgs);

            return minIOConfigProperties.getReadPath() + File.separator +
                    minIOConfigProperties.getBucket() + File.separator +
                    filePath;
        }catch (Exception e){
            log.error("Minio upload image file failed.", e);
            throw new RuntimeException("Upload Image File Failed");
        }
    }

    public String uploadHtmlFile(String filename, InputStream inputStream) {
        return uploadHtmlFile("", filename, inputStream);
    }

    /**
     *  Upload Html File
     * @param parentDir  file's parent dictionary, e.g. htmls/upload
     * @param filename  filename, e.g  1.html
     * @param inputStream  file input stream
     * @return  full filepath e.g. http://minio/bucket/htmls/upload/1.html
     */
    public String uploadHtmlFile(String parentDir, String filename, InputStream inputStream) {
        String filePath = buildFullFilepath(parentDir, filename);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("text/html")
                    .bucket(minIOConfigProperties.getBucket())
                    .stream(inputStream,inputStream.available(),-1)
                    .build();
            minioClient.putObject(putObjectArgs);
            return minIOConfigProperties.getReadPath() + File.separator +
                    minIOConfigProperties.getBucket() + File.separator +
                    filePath;
        }catch (Exception e){
            log.error("Minio upload html file failed.", e);
            throw new RuntimeException("Upload Html File Failed");
        }
    }

    /**
     * Remove
     * @param path  full filepath needed to be removed
     */
    public void remove(String path) {
        String filePath = extractAbsolutePath(path);
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(minIOConfigProperties.getBucket())
                .object(filePath)
                .build();
        try {
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("Minio delete file failed. Path {}, error {}", path, e);
        }
    }


    /**
     * Download File
     * @param path  filepath
     * @return  文件流
     *
     */
    public byte[] downloadFile(String path)  {
        String filePath = extractAbsolutePath(path);
        InputStream inputStream = null;
        try {
            inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minIOConfigProperties.getBucket())
                    .object(filePath).build());
        } catch (Exception e) {
            log.error("Minio download file failed. Path:{}, error {}",path, e);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while (true) {
            try {
                if (!((rc = inputStream.read(buff, 0, 100)) > 0)) break;
            } catch (IOException e) {
                log.error("Minio delete file failed.", e);
            }
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
