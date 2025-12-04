package org.jakemarotta.imageprocessingservice.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jakemarotta.imageprocessingservice.datamodels.TransformationRequest;
import org.jakemarotta.imageprocessingservice.entities.ImageEntity;
import org.jakemarotta.imageprocessingservice.entities.ImageResponse;
import org.jakemarotta.imageprocessingservice.repos.ImageRepo;
import org.jakemarotta.imageprocessingservice.utils.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    @Value("${image.upload.url}")
    private String uploadDir;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private ImageRepo imageRepo;

    @Autowired
    private ImageUtil imageUtil;

    public static String[] allowedFormat = {"JPG", "JPEG", "PNG", "BMP", "WBMP", "GIF"};

    public ImageResponse saveImageInDb(MultipartFile file, String url) throws IOException {
        ImageEntity image = ImageEntity.builder()
                .type(file.getContentType())
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .url(url)
                .build();

        imageRepo.save(image);

        return this.convertToImageResponse(image);
    }

    public ImageResponse saveImageInDb(File file, String url) throws IOException {
        Path filePath = file.toPath();

        ImageEntity image = ImageEntity.builder()
                .fileName(file.getName())
                .fileSize(file.length())
                .type(Files.probeContentType(filePath))
                .url(url)
                .build();

        imageRepo.save(image);

        return this.convertToImageResponse(image);
    }

    private ImageResponse convertToImageResponse(ImageEntity image) {

        return ImageResponse.builder()
                .imageId(image.getId())
                .url(image.getUrl())
                .fileSize(image.getFileSize())
                .type(image.getType())
                .build();
    }

    public ImageResponse saveImage(MultipartFile file) throws IOException {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String systemFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + systemFileName);
        file.transferTo(filePath);

        String fileUrl = "http://localhost:" + serverPort + "/" + uploadDir + systemFileName;

        return this.saveImageInDb(file, fileUrl);
    }

    public ImageResponse getImageFromId(UUID id) throws Exception {
        try {
            ImageEntity image = imageRepo.findById(id).orElse(null);

            if (image != null) {
                return convertToImageResponse(image);
            } else {
                throw new EntityNotFoundException("Image with id " + id + " not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public List<ImageResponse> getServerImages(int pageNo, int pageSize) throws Exception {
        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            List<ImageEntity> userImages = imageRepo.findAll(pageable).toList();

            return userImages.stream().map(this::convertToImageResponse).toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public ImageResponse changeFormatAndSaveImage(BufferedImage image, String format, String url) throws Exception {
        int counter  = 1;

        if (format != null) {
            if (Arrays.asList(allowedFormat).contains(format)) {
                String filename = System.currentTimeMillis() + "_Transdformed_" + counter++ +"." + format.toLowerCase();
                String path = uploadDir + filename;
                ImageIO.write(image, format, new File(path));

                File newFile = new File(path);

                String fileUrl = "https://localhost:" + serverPort + "/" + uploadDir + filename;

                return this.saveImageInDb(newFile, fileUrl);
            } else {
                throw new Exception("Incorrect format");
            }
        } else {
            String filename = System.currentTimeMillis() + "_Transformed_" + counter++ + ".png";
            String path = uploadDir + filename;
            ImageIO.write(image, "PNG", new File(path));

            File newFile = new File(path);

            String fileUrl = "https://localhost:" + serverPort + "/" + uploadDir + filename;

            return this.saveImageInDb(newFile, fileUrl);
        }
    }

    public ImageResponse transformImage(ImageResponse image, TransformationRequest.Transformations transformations) throws Exception {
        BufferedImage transformedImage =  imageUtil.convertImage(image.getUrl(), transformations);

        return this.changeFormatAndSaveImage(transformedImage, transformations.getFormat(), image.getUrl());
    }
}
