package com.la.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author LA
 * @createDate 2023-04-13-20:11
 * @description 测试SDK
 */
public class minioTest {

    MinioClient minioClient = MinioClient
            .builder()
            .endpoint("http://192.168.101.130:9090")
            .credentials("minioadmin", "minioadmin")
            .build();
    @Test
    public void testUpload() throws IOException, ServerException, InsufficientDataException, InternalException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException, ErrorResponseException {

        //通过扩展名得到媒体资源类型 mimeType
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".jpg");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }

        //上传文件的参数信息
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("testbucket")//桶
                .filename("D:\\证件照\\李翱\\蓝底-2寸.jpg") //指定本地文件路径
                //.object("蓝底-2寸.jpg")//对象名 在桶下存储该文件
                .object("test/01/蓝底-2寸.jpg")//对象名 放在子目录下
                .contentType(mimeType)//设置媒体文件类型
                .build();

        //上传文件
        minioClient.uploadObject(uploadObjectArgs);
    }

    @Test
    public void delete() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("蓝底-2寸.jpg")
                .build();
        minioClient.removeObject(removeObjectArgs);
    }

    @Test
    public void download() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("test/01/蓝底-2寸.jpg")
                .build();
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        byte[] buffer = new byte[1024];
        int len;
        File file = new File("D:\\miniodata\\蓝底.jpg");
        FileOutputStream outputStream = new FileOutputStream(file);
        while((len = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0 , len);
        }

        inputStream.close();
        outputStream.close();


        //校验文件完整性，进行md5
        FileInputStream sourceinputStream = new FileInputStream("D:\\证件照\\李翱\\蓝底-2寸.jpg");
        FileInputStream fileInputStream = new FileInputStream(new File("D:\\miniodata\\蓝底.jpg"));
        String source_md5 = DigestUtils.md5DigestAsHex(sourceinputStream);
        String out_md5 = DigestUtils.md5DigestAsHex(fileInputStream);
        if(source_md5.equals(out_md5)){
            System.out.println("下载成功");
        }


        fileInputStream.close();


    }

    //将分块文件上传至minio
    @Test
    public void uploadChunk(){
        String chunkFolderPath = "D:\\minio\\test\\chunk\\";
        File chunkFolder = new File(chunkFolderPath);
        //分块文件
        File[] files = chunkFolder.listFiles();
        //将分块文件上传至minio
        for (int i = 0; i < files.length; i++) {
            try {
                UploadObjectArgs uploadObjectArgs =
                        UploadObjectArgs
                                .builder()
                                .bucket("testbucket")
                                .object("chunk/" + i)
                                .filename(files[i].getAbsolutePath())
                                .build();
                minioClient.uploadObject(uploadObjectArgs);
                System.out.println("上传分块成功"+i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //合并文件，要求分块文件最小5M
    @Test
    public void test_merge() throws Exception {
        List<ComposeSource> sources = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ComposeSource composeSource = ComposeSource
                    .builder()
                    .bucket("testbucket")
                    .object("chunk/" + i)
                    .build();
            sources.add(composeSource);
        }

        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge/123.mp4")
                .sources(sources)
                .build();
        minioClient.composeObject(composeObjectArgs);

    }
    //清除分块文件
    @Test
    public void test_removeObjects(){
        //合并分块完成将分块文件清除
        List<DeleteObject> deleteObjects = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DeleteObject deleteObject = new DeleteObject("chunk/" + i);
            deleteObjects.add(deleteObject);
        }

        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs
                .builder()
                .bucket("testbucket")
                .objects(deleteObjects)
                .build();

        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        for(Result<DeleteError> result : results){
            try {
                DeleteError deleteError = result.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
