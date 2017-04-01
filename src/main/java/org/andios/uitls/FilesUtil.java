package org.andios.uitls;

import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by version on 17-3-28.
 * 上传到网易蜂巢OSB对象存储
 */
public class FilesUtil {

    private static String accesskey;
    private static String secretKey;
    private static String endPoint;
    private static String ContentEncoding;
    private static String CacheControl;
    private static String ContentLanguage;

    private static Credentials credentials;
    private static NosClient nosClient;

    /**
     * 创建文件上传容器
     * @param bucketName 容器名
     * @return
     */
    public Bucket createBucket(String bucketName){
        CreateBucketRequest bucketRequest = new CreateBucketRequest(bucketName);
        bucketRequest.setCannedAcl(CannedAccessControlList.PublicRead);
        return nosClient.createBucket(bucketRequest);
    }

    /**
     * 重命名文件
     * @param BucketName 容器名
     * @param source 原文件名
     * @param dst 新文件名
     */
    public void renameFile(String BucketName, String source, String dst){
        nosClient.moveObject(BucketName, source, BucketName, dst);
    }

    /**
     * 删除文件(多文件删除)
     * @param BucketName 容器名
     * @param fileNames 文件名
     */
    public void deleteFile(String BucketName, List<String> fileNames){
        if(fileNames.size() == 1){
            nosClient.deleteObject(BucketName, fileNames.get(0));
        } else {
            try {
                DeleteObjectsResult result = nosClient
                        .deleteObjects(new DeleteObjectsRequest(BucketName));
                result.getDeletedObjects().forEach(System.out::println);
                // 部分对象删除失败
            } catch (MultiObjectDeleteException e) {
                e.printStackTrace();
                e.getErrors().forEach(System.out::println);
            } catch (ServiceException e) {
                //捕捉nos服务器异常错误
                e.printStackTrace();
            } catch (ClientException e) {
                //捕捉客户端错误
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传文件
     * @param bucketName 容器名
     * @param key 文件名
     * @param file 源文件
     * @return
     * @throws IOException
     */
    public PutObjectResult putObject(String bucketName, String key, File file) throws IOException {
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader("Content-Encoding",ContentEncoding);
        metadata.setHeader("Cache-Control",CacheControl);
        metadata.setHeader("Content-Language",ContentLanguage);

        PutObjectRequest objectRequest = new PutObjectRequest(bucketName, key, file);
        objectRequest.setMetadata(metadata);
        return nosClient.putObject(objectRequest);
    }
    /**
     * 下载文件
     * @param bucketName 容器名
     * @param objectName 文件名
     * @param filePath 本地路径
     * @return
     */
    public ObjectMetadata downloadFile(String bucketName, String objectName, String filePath){
        GetObjectRequest objectRequest = new GetObjectRequest(bucketName, objectName);
        ObjectMetadata objectMetadata = nosClient.getObject(objectRequest, new File(filePath));
        return objectMetadata;
    }

    /**
     * 生成下载链接(共享)
     * @param bucketName 容器名
     * @param key 文件名
     * @param date 过期时间
     * @return
     */
    public URL generatedSharedUrl(String bucketName, String key, long million){
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, key);
        Date date = new Date(System.currentTimeMillis() + million);
        urlRequest.setExpiration(date);
        URL url = nosClient.generatePresignedUrl(urlRequest);
        return url;
    }

    private FilesUtil(){}

    private static FilesUtil  filesUtil = null;

    public static FilesUtil getInstance(){
        if(filesUtil == null){
            init();
            filesUtil = new FilesUtil();
        }
        return filesUtil;
    }

    private static void init() {
        Properties properties = new Properties();
        InputStream is = Properties.class.getResourceAsStream("/properties/obs.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        accesskey = properties.getProperty("accesskey");
        secretKey = properties.getProperty("secretKey");
        endPoint = properties.getProperty("endPoint");
        ContentEncoding = properties.getProperty("ContentEncoding");
        CacheControl = properties.getProperty("CacheControl");
        ContentLanguage = properties.getProperty("ContentLanguage");

        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        credentials = new BasicCredentials(accesskey, secretKey);
        nosClient = new NosClient(credentials);
        nosClient.setEndpoint(endPoint);
    }
}
