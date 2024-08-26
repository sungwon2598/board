package ict.board.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import ict.board.service.BoardService;
import ict.board.service.FileService;
import ict.board.repository.BoardRepository;
import ict.board.domain.board.Board;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BoardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardRepository boardRepository;

    @MockBean
    private FileService fileService;

    @Autowired
    private BoardService boardService;

    private MockMultipartFile imageFile;

    @BeforeEach
    void setUp() {
        imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());
    }

    @Test
    @WithMockUser(username = "hongsungwon2598@gmail.com", roles = "NONE")
    public void testCreateGeneral() throws Exception {
        // Given
        when(fileService.saveImage(any())).thenReturn("path/to/saved/image.jpg");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Prepare request");

        // Prepare the request
        ResultActions resultActions = mockMvc.perform(multipart("/board/new/general")
                .file(imageFile)
                .param("title", "Test Title")
                .param("content", "Test Content")
                .param("requester", "Test Requester")
                .param("requesterLocation", "Test Location")
                .with(csrf()));

        stopWatch.stop();
        stopWatch.start("Execute request");

        // Execute the request
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/board/*"));

        stopWatch.stop();
        stopWatch.start("Get result");

        // Get the result
        MvcResult result = resultActions.andReturn();

        stopWatch.stop();
        stopWatch.start("Process result");

        // Process the result (e.g., extract information from the response)
        String redirectedUrl = result.getResponse().getRedirectedUrl();
        assertNotNull(redirectedUrl);
        String boardIdStr = redirectedUrl.substring(redirectedUrl.lastIndexOf("/") + 1);
        Long boardId = Long.parseLong(boardIdStr);

        stopWatch.stop();
        stopWatch.start("Verify board");

        // Verify that the board was actually saved
        Board savedBoard = boardRepository.findById(boardId).orElseThrow();
        assertEquals("Test Title", savedBoard.getTitle());
        assertEquals("Test Content", savedBoard.getContent());
        assertEquals("Test Requester", savedBoard.getRequester());
        assertEquals("Test Location", savedBoard.getRequesterLocation());
        assertEquals("path/to/saved/image.jpg", savedBoard.getImagePath());

        stopWatch.stop();

        // Log the detailed timing information
        log.info("Detailed timing for MockMvc perform:");
        for (StopWatch.TaskInfo task : stopWatch.getTaskInfo()) {
            log.info("- {}: {}ms", task.getTaskName(), task.getTimeMillis());
        }
    }

    // Other test methods...
}