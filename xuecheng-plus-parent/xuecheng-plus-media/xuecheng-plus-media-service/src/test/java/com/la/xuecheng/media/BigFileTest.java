package com.la.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-15-20:06
 * @description
 */
public class BigFileTest {


    //分块测试
    @Test
    public void testChunk() throws IOException {
        //源文件位置
        File sourceFile = new File("D:\\minio\\test\\123.mp4");
        //分块文件位置
        String chunkFilePath = "D:\\minio\\test\\chunk";
        //分块大小
        int chunkSize = 1024*1024*5; //B、KB、MB
        //分块个数
        int chunkNum = (int)Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        //使用流从源文件读数据，向分块中写数据
        RandomAccessFile read = new RandomAccessFile(sourceFile, "r");
        //缓冲区
        byte[] buffer = new byte[1024];

        for(int i = 0; i < chunkNum; i++){
            File chunkFile = new File(chunkFilePath + "\\" + i);
            //分块文件写入流
            RandomAccessFile write = new RandomAccessFile(chunkFile, "rw");
            int len;
            while((len = read.read(buffer)) != -1){
                write.write(buffer, 0 ,len);
                if(chunkFile.length() >= chunkSize){
                    break;
                }
            }
            write.close();
        }
        read.close();

    }

    //合并测试
    @Test
    public void testMerge() throws IOException {
        //块文件目录
        File chunkFolder = new File("D:\\minio\\test\\chunk");
        //原始文件
        File originalFile = new File("D:\\minio\\test\\123.mp4");
        //合并文件
        File mergeFile = new File("D:\\minio\\test\\merge\\123.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] b = new byte[1024];
        //分块列表
        File[] fileArray = chunkFolder.listFiles();
        // 转成集合，便于排序
        List<File> fileList = Arrays.asList(fileArray);
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        //合并文件
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);

            }
            raf_read.close();
        }
        raf_write.close();

        //校验文件
        try (

                FileInputStream fileInputStream = new FileInputStream(originalFile);
                FileInputStream mergeFileStream = new FileInputStream(mergeFile);

        ) {
            //取出原始文件的md5
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            //取出合并文件的md5进行比较
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }

        }


    }

}
