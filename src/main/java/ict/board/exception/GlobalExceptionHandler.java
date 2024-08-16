package ict.board.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BoardNotFoundException.class)
    public String handleBoardNotFoundException(BoardNotFoundException ex, Model model) {
        log.error("Board not found: ", ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/boardNotFound";
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public String handleUnauthorizedAccessException(UnauthorizedAccessException ex, Model model) {
        log.error("Unauthorized access: ", ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(InvalidBoardFormException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidBoardFormException(InvalidBoardFormException e, Model model) {
        log.error("Invalid board form: {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "board/boardform";
    }

    @ExceptionHandler(FileUploadException.class)
    public String handleFileUploadException(FileUploadException ex, Model model) {
        log.error("파일 업로드 오류: ", ex);
        model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
        return "error/fileUploadError";
    }

    @ExceptionHandler(InvalidFileException.class)
    public String handleInvalidFileException(InvalidFileException ex, Model model) {
        log.error("잘못된 파일: ", ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/fileUploadError";
    }

    @ExceptionHandler(DirectoryCreationException.class)
    public String handleDirectoryCreationException(DirectoryCreationException ex, Model model) {
        log.error("디렉토리 생성 오류: ", ex);
        model.addAttribute("errorMessage", "파일 저장을 위한 디렉토리를 생성할 수 없습니다.");
        return "error/directoryCreationError";
    }

//    @ExceptionHandler(FileUploadException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public String handleFileUploadException(FileUploadException e, Model model) {
//        log.error("File upload error: {}", e.getMessage());
//        model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
//        return "error/fileUploadError";
//    }
//
//    @ExceptionHandler(InvalidStatusChangeException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public String handleInvalidStatusChangeException(InvalidStatusChangeException e, Model model) {
//        log.error("Invalid status change attempt: {}", e.getMessage());
//        model.addAttribute("errorMessage", e.getMessage());
//        return "error/invalidStatusChange";
//    }
//
//    @ExceptionHandler(AIProcessingException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public String handleAIProcessingException(AIProcessingException e, Model model) {
//        log.error("AI processing error: {}", e.getMessage());
//        model.addAttribute("errorMessage", "AI 처리 중 오류가 발생했습니다.");
//        return "error/aiProcessingError";
//    }
//
//    @ExceptionHandler(EntityNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public String handleEntityNotFoundException(EntityNotFoundException e, Model model) {
//        log.error("Entity not found: {}", e.getMessage());
//        model.addAttribute("errorMessage", e.getMessage());
//        return "error/entityNotFound";
//    }

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public String handleGeneralException(Exception e, Model model) {
//        log.error("Unexpected error occurred: ", e);
//        model.addAttribute("errorMessage", "예기치 않은 오류가 발생했습니다.");
//        return "error/generalError";
//    }
}