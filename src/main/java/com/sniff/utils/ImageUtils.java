package com.sniff.utils;

import com.sniff.filestore.exception.EmptyFileException;
import com.sniff.filestore.exception.IncorrectFileFormatException;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.apache.http.entity.ContentType.*;

public class ImageUtils {
    private static final double IMAGE_RESIZE_RATIO = 1.2;

    public static InputStream compressImage(MultipartFile image) throws IOException {
        BufferedImage originalImage = ImageIO.read(image.getInputStream());
        BufferedImage compressedImage = Scalr.resize(
                originalImage,
                Scalr.Method.BALANCED,
                Scalr.Mode.AUTOMATIC,
                (int) (originalImage.getWidth()/IMAGE_RESIZE_RATIO),
                (int) (originalImage.getHeight()/IMAGE_RESIZE_RATIO));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(compressedImage, image.getContentType().split("/")[1], os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    public static String getFileExtension(String fileName) {
        int dotIndex = StringUtils.lastIndexOf(fileName, '.');
        return StringUtils.substring(fileName, dotIndex + 1);
    }

    public static String getFileNameWithExtension(String fileName, String originalFileName) {
        return String.format("%s.%s", fileName, getFileExtension(originalFileName));
    }

    public static void isImage(MultipartFile file) {
        if(!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType()
        ).contains(file.getContentType())){
            throw new IncorrectFileFormatException("File format must be JPEG or PNG");
        }
    }

    public static void isFileEmpty(MultipartFile file) {
        if(file.isEmpty()){
            throw new EmptyFileException("File must not be empty");
        }
    }
}
