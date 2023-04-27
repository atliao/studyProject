package com.la.xuecheng.media.service;

import com.la.xuecheng.base.model.PageParams;
import com.la.xuecheng.base.model.PageResult;
import com.la.xuecheng.base.model.RestResponse;
import com.la.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.la.xuecheng.media.model.dto.UploadFileParamsDto;
import com.la.xuecheng.media.model.dto.UploadFileResultDto;
import com.la.xuecheng.media.model.po.MediaFiles;

import java.io.File;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

    /**
    * @description 媒资文件查询方法
    * @param pageParams 分页参数
    * @param queryMediaParamsDto 查询条件
    * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
    * @author Mr.M
    * @date 2022/9/10 8:57
    */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    public UploadFileResultDto upLoadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName);

    MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket_mediafiles, String objectName);

    RestResponse<Boolean> checkFile(String fileMd5);

    RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    RestResponse uploadChunk(String fileMd5, int chunk, String localFilePath);

    RestResponse mergechunks(long l, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    public File downloadFileFromMinIO(String bucket, String objectName);

    public boolean addMediaFilesToMinIO(String mimeType, String localFilePath, String bucket, String objectName);

    MediaFiles getFileById(String mediaId);
}
