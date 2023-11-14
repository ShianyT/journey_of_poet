package com.zhengaicha.journey_of_poet.utils;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static com.zhengaicha.journey_of_poet.utils.SystemConstants.*;

public class ImageUtil {

    // 图片默认缩放比率
    private static final double DEFAULT_SCALE = 0.3d;


    public static String saveImage(MultipartFile multipartFile,String type){
        try {
            String OriginalFilename = multipartFile.getOriginalFilename();
            String format = OriginalFilename.substring(OriginalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "")
                    + RandomUtil.randomNumbers(5) + format;

            if(type.equals("icon")){
                File file = new File(ICON_PATH + "/" + fileName);
                FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
                file.deleteOnExit();


                String toFile = THUMBNAIL_ICON_PATH + "/" + fileName;
                Thumbnails.of(file).scale(DEFAULT_SCALE).toFile(toFile);
            } else if (type.equals("post")) {
                File file = new File(POST_IMAGES_PATH + "/" + fileName);
                FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
                file.deleteOnExit();


                String toFile = THUMBNAIL_POST_PATH + "/" + fileName;
                Thumbnails.of(file).scale(DEFAULT_SCALE).toFile(toFile);
            }

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断文件后缀是否为图片类型
     */
    public static boolean isValidImage(String fileName) {
        String format = fileName.substring(fileName.lastIndexOf("."));
        return format.equals(".jpg") || format.equals(".jpeg") || format.equals(".png");
    }
}
