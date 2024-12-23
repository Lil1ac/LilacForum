package com.lilac.controller;




import com.aliyuncs.exceptions.ClientException;
import com.lilac.pojo.Result;
import com.lilac.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private AliOSSUtils aliOSSUtils;
    @PostMapping("/image")
    public Result upload(MultipartFile image) throws IOException, ClientException {
        log.info("文件上传，文件名：{}",image.getOriginalFilename());
        String url = aliOSSUtils.upload(image);
        log.info("文件上传完成，文件访问的url：{}",url);
        return Result.success(url);
    }
}
