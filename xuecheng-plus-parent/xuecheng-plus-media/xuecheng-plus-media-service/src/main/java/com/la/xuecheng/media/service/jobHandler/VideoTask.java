package com.la.xuecheng.media.service.jobHandler;

import com.la.xuecheng.base.utils.Mp4VideoUtil;
import com.la.xuecheng.media.model.po.MediaProcess;
import com.la.xuecheng.media.service.MediaFileProcessService;
import com.la.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author LA
 * @createDate 2023-04-17-11:46
 * @description
 */
@Slf4j
@Component
public class VideoTask {

    @Resource
    MediaFileProcessService mediaFileProcessService;

    @Resource
    MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    String ffmpeg_path;

    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();//执行器总数

        //确定CPU核数
        int cpuNum = 2;

        //查询待处理任务(最多可执行cpu数量的线程)
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, cpuNum);

        //创建线程池
        //任务数量
        int size = mediaProcessList.size();
        if(size <= 0){
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(size);

        //使用的计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);

        //获取任务
        for(MediaProcess mediaProcess : mediaProcessList){
            //将任务加入线程池
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //任务id
                        Long taskId = mediaProcess.getId();
                        //文件id
                        String fileId = mediaProcess.getFileId();
                        //开启任务，抢锁
                        boolean b = mediaFileProcessService.startTask(taskId);
                        if (!b) {
                            log.debug("抢占任务失败，任务id:{}", taskId);
                            //记录失败结果
                            mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "抢占任务失败");
                            return;
                        }
                        //执行视频转码
                        //ffmpeg的路径
                        //已注入
                        String bucket = mediaProcess.getBucket();
                        String filePath = mediaProcess.getFilePath();
                        //源avi视频的路径,下载视频到本地
                        File file = mediaFileService.downloadFileFromMinIO(bucket, filePath);
                        if (file == null) {
                            log.debug("下载任务失败，任务id:{}", taskId);
                            //记录失败结果
                            mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载视频到本地失败");
                            return;
                        }
                        String video_path = file.getAbsolutePath();
                        //转换后mp4文件的名称
                        String mp4_name = fileId + ".mp4";
                        //转换后mp4文件的路径
                        //先创建一个临时文件，作为转换后的文件
                        File tempFile = null;
                        try {
                            tempFile = File.createTempFile("minio", ".mp4");
                        } catch (IOException e) {
                            log.debug("创建临时文件失败，任务id:{}", taskId);
                            //记录失败结果
                            mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件失败");
                            return;
                        }
                        String mp4_path = tempFile.getAbsolutePath();
                        //创建工具类对象，进行转码
                        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4_path);
                        //开始视频转换，成功将返回success
                        String result = videoUtil.generateMp4();
                        if (!result.equals("success")) {
                            log.debug("视频转码失败，任务id:{}", taskId);
                            //记录失败结果
                            mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "视频转码失败");
                        }
                        //上传到minio
                        String objectNameMp4 = getFilePath(fileId, ".mp4");
                        boolean b1 = mediaFileService.addMediaFilesToMinIO("video/mp4", mp4_path, bucket, objectNameMp4);
                        if (!b1) {
                            log.debug("上传mp4到minio失败,taskid:{}", taskId);
                            mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "上传mp4到minio失败");
                            return;
                        }
                        //mp4文件的url
                        String url = "/" + bucket + "/" + objectNameMp4;

                        //更新任务状态为成功
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "2", fileId, url, null);
                    } finally {
                        //计算器减去1
                        countDownLatch.countDown();
                    }
                }
            });
        }
        //阻塞,指定最大限制的等待时间，阻塞最多等待一定的时间后就解除阻塞
        countDownLatch.await(30, TimeUnit.MINUTES);
        executorService.shutdown();

    }

    private String getFilePath(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

}