package com.example.running.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author hongyuan
 * @since 2022/8/9 20:47
 * RestFul
 **/
@RestController
public class FileController {

    private Logger logger = LoggerFactory.getLogger(FileController.class);

    /**
     * @author Aloha
     * @date 2022/12/9 16:43
     * @description MultipartFile 自动封装上传过来的文件
     */
    @PostMapping("/upload")
    public String uploadFile(@RequestParam String email, @RequestParam String username,
                             @RequestPart("headerImg") MultipartFile uploadFile,  MultipartFile[] photos) throws IOException {
        //获取项目编译路径
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        String filePath = path + "static" + File.separator;

        String format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());

        if(!uploadFile.isEmpty()){
            //为文件命名增加随机码和时间戳
            String originalFilename = format + "-" + UUID.randomUUID() + "-" + uploadFile.getOriginalFilename();
            File file = new File(filePath + originalFilename);
            // FileCopyUtils.copy(this.part.getInputStream(), Files.newOutputStream(dest.toPath())); -> FileCopyUtils.copy(File in, File out)
            // 两个文件的复制
            uploadFile.transferTo(file);
        }


        return "get";
    }

    @PostMapping("/downloadFile")
    public void downloadFile(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String fileName = "4.png";
        // 得到文件的路径
        String path = ResourceUtils.getURL("classpath:").getPath() + "/static" + File.separator + fileName;
        FileInputStream in = new FileInputStream(URLDecoder.decode(path, "UTF-8"));

        // 设置返回值
        httpServletResponse.setHeader(
                "Content-Disposition",
                "attachment;filename=" +fileName);
        //servlet输出流
        ServletOutputStream os = httpServletResponse.getOutputStream();
        // copy 这个方法将内容按字节从一个InputStream对象复制到一个OutputStream对象，并返回复制的字节数。
        //通过FileCopyUtils 对接输入输出流，实现文件下载
        FileCopyUtils.copy(in, os);
    }

    @GetMapping("/downloadFile2")
    public ResponseEntity<InputStreamResource> downloadFile2() throws Exception {
        int qq = 1/0;

        String fileName = "4.png";
        // 得到文件的路径
        String path = ResourceUtils.getURL("classpath:").getPath() + "/static" + File.separator + fileName;
        InputStreamResource isr = new InputStreamResource(new FileInputStream(path));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-disposition", "attachment; filename=" +fileName)
                .body(isr);
    }

    @GetMapping("/downloadFile3")
    public ResponseEntity<ByteArrayResource> downloadFile3() throws Exception {
        String fileName = "4.png";
        // 得到文件的路径
        String path = ResourceUtils.getURL("classpath:").getPath() + "/static" + File.separator + fileName;

        byte[] bytes = Files.readAllBytes(new File(path).toPath());
        ByteArrayResource bar = new ByteArrayResource(bytes);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-disposition", "attachment; filename=" +fileName)
                .body(bar);
    }

}
