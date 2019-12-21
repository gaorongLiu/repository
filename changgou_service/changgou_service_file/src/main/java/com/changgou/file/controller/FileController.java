package com.changgou.file.controller;

import com.changgou.common.entity.Result;
import com.changgou.common.entity.StatusCode;
import com.changgou.common.exception.ExceptionCast;
import com.changgou.common.model.response.file.FileCode;
import com.changgou.file.util.FastDFSClient;
import com.changgou.file.util.FastDFSFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    @PostMapping("/upload")
    public Result upload(MultipartFile file)  {

        try {
            if (file.isEmpty()){
                ExceptionCast.cast(FileCode.File_NULL_ERROR);
            }
            if (file.getOriginalFilename().isEmpty()){
                ExceptionCast.cast(FileCode.File_NULL_ERROR);
            }
            String filename = file.getOriginalFilename();
            int i = filename.lastIndexOf(".")+1;
            String  suffix= filename.substring(i);

            byte[] bytes = file.getBytes();
            FastDFSFile fastDFSClient =new FastDFSFile(filename,bytes,suffix);

            //上传文件
            String[] uploadResult = FastDFSClient.upload(fastDFSClient);
            String url = FastDFSClient.getTrackerUrl()+uploadResult[0]+"/"+uploadResult[1];
            return new Result(true, StatusCode.OK,"文件上传成功",url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Result(false, StatusCode.ERROR,"文件上传失败");
        //图片地址：http://192.168.200.128:8080/group1/M00/00/00/wKjIgF3626GADjJHAABLjayiUwM352.jpg
    }
}
