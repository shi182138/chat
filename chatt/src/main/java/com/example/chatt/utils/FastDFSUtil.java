package com.example.chatt.utils;

import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class FastDFSUtil {

    private static Logger logger = LoggerFactory.getLogger(FastDFSUtil.class);
    private static StorageClient storageClient;


    static {
        try {
            String filePath = new ClassPathResource("fdfs_client.conf").getFile().getAbsolutePath();
            ClientGlobal.init(filePath);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            storageClient = new StorageClient(trackerServer, null);
            logger.info("初始化成功");
        } catch (Exception e) {
            logger.info("初始化FastDFS失败", e);
        }
    }

    //文件上传
    public static String[] upload(MultipartFile file) {
        //获取文件名
        String fileName = file.getOriginalFilename();
//        logger.info("文件名：",fileName);
            String[] uploadResults = null;
        try {
            uploadResults = storageClient.upload_file(file.getBytes(), fileName.substring(fileName.lastIndexOf(".") + 1), null);
        } catch (Exception e) {
            logger.info("文件上传失败",e.getMessage());
        }
        if (null == uploadResults) {
            logger.error("上传失败",storageClient.getErrorCode());
        }
        return uploadResults;
    }

    /**
     * 获取文件信息
     * @param groupName 组名
     * @param remoteFileName 远程文件名
     * @return
     */
    public static FileInfo getFileInfo(String groupName,String remoteFileName) {
        try {
            return storageClient.get_file_info(groupName, remoteFileName);
        } catch (Exception e) {
            logger.error("文件信息获取失败",e.getMessage());
        }
        return null;
    }

    //文件下载
    public static InputStream downFile(String groupName,String remoteFileName) {
        try {
            return new ByteArrayInputStream(storageClient.download_file(groupName,remoteFileName));
        } catch (Exception e) {
            logger.error("文件下载失败",e.getMessage());
        }
        return null;
    }

    //删除文件
    public static void deleteFile(String groupName,String remoteFileName) {
        try {
            storageClient.delete_file(groupName,remoteFileName);
        } catch (Exception e) {
            logger.error("文件删除失败",e.getMessage());
        }
    }

    //获取文件路径,8888 nginx监听端口
    public static String getTrackerUrl() {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        try {
            trackerServer = trackerClient.getTrackerServer();
            storageServer = trackerClient.getStoreStorage(trackerServer);
        }catch (Exception e) {
            logger.error("文件路径获取失败",e.getMessage());
        }
        return "http://" + storageServer.getInetSocketAddress().getHostString() + ":8888/";
    }

}
