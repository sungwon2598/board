package ict.board.service;

import ict.board.exception.DirectoryCreationException;
import ict.board.exception.FileUploadException;
import ict.board.exception.InvalidFileException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new InvalidFileException("업로드된 이미지가 없거나 비어 있습니다.");
        }

        try {
            String originalFilename = image.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String newFilename = generateNewFilename(fileExtension);
            Path imagePath = Paths.get(uploadDir, newFilename);

            createDirectoryIfNotExists(imagePath.getParent());
            Files.write(imagePath, image.getBytes());

            return newFilename;
        } catch (IOException e) {
            log.error("이미지 저장 중 오류 발생", e);
            throw new FileUploadException("이미지 저장 중 오류가 발생했습니다.", e);
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new InvalidFileException("파일 확장자가 없습니다.");
        }
        return filename.substring(lastDotIndex);
    }

    private String generateNewFilename(String fileExtension) {
        return System.currentTimeMillis() + fileExtension;
    }

    private void createDirectoryIfNotExists(Path directory) {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            log.error("디렉토리 생성 중 오류 발생", e);
            throw new DirectoryCreationException("업로드 디렉토리를 생성할 수 없습니다.", e);
        }
    }
}