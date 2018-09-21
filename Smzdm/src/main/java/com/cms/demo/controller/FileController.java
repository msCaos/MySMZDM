package com.cms.demo.controller;

import com.cms.demo.util.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class FileController {

    @Value("${img.location}")
    private String saveDir;
    @RequestMapping("/uploadImage")
    //出现错误，请重试
    public Result upload(MultipartFile file, HttpServletRequest request){
        Result result = new Result();
        if (file!=null){
            String filename = UUID.randomUUID().toString()+file.getOriginalFilename();
            System.out.println("************"+saveDir);
            File myfile = new File(saveDir, filename);
            try {
                file.transferTo(myfile);
                result.setMsg("http://localhost:8080/uploadPic/"+filename);
                result.setCode(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
