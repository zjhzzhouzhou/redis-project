package com.zz.redis.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Description:
 * User: zhouzhou
 * Date: 2019-06-10
 * Time: 9:22
 */
@RestController
@RequestMapping("/file")
public class FileController {


    @ApiOperation(
            value = "Web/App等客户端上传云存储文件",
            produces = "application/json",
            consumes = "multipart/form-data")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public Object upload(@RequestPart MultipartFile file) {
        System.out.println(file);
        return null;
    }

}
