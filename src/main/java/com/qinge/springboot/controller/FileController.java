package com.qinge.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinge.springboot.common.Result;
import com.qinge.springboot.entity.Files;
import com.qinge.springboot.entity.User;
import com.qinge.springboot.mapper.FileMapper;
import com.qinge.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {


    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Resource
    private FileMapper fileMapper;

    @Value("${server.ip}")
    private String serverIp;

//    前端传来的文件
    @PostMapping("/upload")
    public String upload(@RequestBody MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();
        //先存储到磁盘
//        File uploadParentFile = new File(fileUploadPath);
        //判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
//        if(!uploadParentFile.exists()){
//            uploadParentFile.mkdirs();
//        }
        //定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid+ StrUtil.DOT + type;
        File uploadFile = new File(fileUploadPath+fileUUID);
        //判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        File parentFile = uploadFile.getParentFile();
        if(!parentFile.exists()){
            parentFile.mkdirs();
        }
        String md5;
        String url;
;//     当文件存在再获取文件的md5
//        if(uploadFile.exists()){
//            //获取文件的md5
//            md5 = SecureUtil.md5(uploadFile);
//            //数据库查询文件的md5是否存在
//            Files dbFiles = getFileMd5(md5);
////          获取文件url
//            if(dbFiles!=null){
//                url = dbFiles.getUrl();
//            }else {
////          把获取到的文件存储到磁盘目录
//                file.transferTo(uploadFile);
//                url = "http://localhost:9090/file/" + fileUUID;
//            }
//        }else {
        file.transferTo(uploadFile);
        md5 = SecureUtil.md5(uploadFile);
        Files dbFiles = getFileMd5(md5);
        if(dbFiles!=null){
            url = dbFiles.getUrl();
            //判断文件是否重复，重复删除已存在文件
            uploadFile.delete();
        }else {
//          把获取到的文件存储到磁盘目录
            url = "http://"+serverIp+":9090/file/" + fileUUID;
        }
//        url = "http://localhost:9090/file/" + fileUUID;
//        }
        //存储数据库
        System.out.println(url+"0000000000000000000000");
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size/1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        fileMapper.insert(saveFile);
        return url;
    }
    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException{
        //根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath+fileUUID);
        //设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileUUID,"UTF-8"));
        response.setContentType("application/octet-stream");
        //读文件的字节流
//        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    private Files getFileMd5(String md5){
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<Files> filesList = fileMapper.selectList(queryWrapper);
        return filesList.size() ==0 ? null:filesList.get(0);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",false);
        if(!"".equals(name)){
            queryWrapper.like("name",name);
        }
        return Result.success(fileMapper.selectPage(new Page<>(pageNum, pageSize),queryWrapper));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        Files files = fileMapper.selectById(id);
        files.setIsDelete(true);
        fileMapper.updateById(files);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        QueryWrapper<Files> querWrapper = new QueryWrapper<>();
        querWrapper.in("id",ids);
        List<Files> files = fileMapper.selectList(querWrapper);
        for (Files file:files){
            file.setIsDelete(true);
            fileMapper.updateById(file);
        }
        return Result.success();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Files files){
        return Result.success(fileMapper.updateById(files));
    }
}
