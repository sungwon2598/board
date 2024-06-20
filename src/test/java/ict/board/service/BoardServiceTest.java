package ict.board.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.board.ReservationBoard;
import ict.board.domain.member.Member;
import ict.board.repository.BoardRepository;
import ict.board.repository.MemberRepository;
import ict.board.service.ai.AIResponseHandler;
import ict.board.service.slack.NewBoardSender;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AIResponseHandler aiResponseHandler;

    @Mock
    private NewBoardSender newBoardSender;

    private Member testMember;
    private Board testBoard;
    private ReservationBoard testReservationBoard;

    @BeforeEach
    void setUp() {
        testMember = new Member("test@example.com", "Test User", "password", null, "Test Team", "12345");
        testBoard = new Board("Test Title", "Test content", "Requester", "Location");
        testBoard.addMember(testMember);

        testReservationBoard = new ReservationBoard("Reservation Title", "Reservation content", "Requester", "Location",
                LocalDateTime.now());
        testReservationBoard.addMember(testMember);
    }

    @Test
    @DisplayName("게시판 저장 테스트")
    void save() throws IOException, InterruptedException {
        when(memberRepository.findMemberByEmail("test@example.com")).thenReturn(Optional.of(testMember));
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);

        boardService.save(testBoard, "test@example.com");

        verify(boardRepository, times(1)).save(any(Board.class));
        verify(newBoardSender, times(1)).sendFromBoard(any(Board.class));
        verify(aiResponseHandler, times(1)).answerGpt(any(Board.class), anyString());
    }

    @Test
    @DisplayName("게시판 삭제 테스트")
    void delete() {
        doNothing().when(boardRepository).deleteById(1L);

        boardService.delete(1L);

        verify(boardRepository, times(1)).deleteById(1L);
    }


    @Test
    @DisplayName("게시판 업데이트 테스트")
    void update() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));

        boardService.update(1L, "New Title", "New Content", "Requester", "Location");

        verify(boardRepository, times(1)).findById(1L);
        assertEquals("New Title", testBoard.getTitle());
        assertEquals("New Content", testBoard.getContent());
        assertEquals("Requester", testBoard.getRequester());
        assertEquals("Location", testBoard.getRequesterLocation());
    }

    @Test
    @DisplayName("날짜별 게시판 조회 테스트")
    void findAllBoardsByDate() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate date = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Board> boards = Arrays.asList(testBoard);
        Page<Board> page = new PageImpl<>(boards, pageable, boards.size());

        when(boardRepository.findAllByCreatedAtBetween(startOfDay, endOfDay, pageable)).thenReturn(page);

        Page<Board> result = boardService.findAllBoardsByDate(pageable, date);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("모든 게시판 조회 테스트")
    void findAllBoards() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Board> boards = Arrays.asList(testBoard);
        Page<Board> page = new PageImpl<>(boards, pageable, boards.size());

        when(boardRepository.findAllWithMember(pageable)).thenReturn(page);

        Page<Board> result = boardService.findAllBoards(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("단일 게시판 조회 테스트")
    void findOneBoardWithMember() {
        when(boardRepository.findWithMemberById(1L)).thenReturn(testBoard);

        Board result = boardService.findOneBoardWithMember(1L);

        assertNotNull(result);
        assertEquals(testBoard, result);
    }

    @Test
    @DisplayName("회원별 게시판 조회 테스트")
    void findBoardsbyMember() {
        List<Board> boards = Arrays.asList(testBoard);

        when(boardRepository.findByMember(testMember)).thenReturn(boards);

        List<Board> result = boardService.findBoardsbyMember(testMember);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("상태별 게시판 조회 테스트")
    void findAllBoardsByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Board> boards = Arrays.asList(testBoard);
        Page<Board> page = new PageImpl<>(boards, pageable, boards.size());

        when(boardRepository.findAllByBoardStatus(pageable, BoardStatus.COMPLETED)).thenReturn(page);

        Page<Board> result = boardService.findAllBoardsByStatus(pageable, "COMPLETED");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("예약 게시판 저장 테스트")
    void saveReservationBoard() throws IOException, InterruptedException {
        when(memberRepository.findMemberByEmail("test@example.com")).thenReturn(Optional.of(testMember));
        when(boardRepository.save(any(Board.class))).thenReturn(testReservationBoard);

        boardService.save(testReservationBoard, "test@example.com");

        verify(boardRepository, times(1)).save(any(Board.class));
        verify(newBoardSender, times(1)).sendFromBoard(any(Board.class));
        verify(aiResponseHandler, times(1)).answerGpt(any(Board.class), anyString());
    }

    @Test
    @DisplayName("예약 게시판 상태 변경 테스트")
    void changeReservationBoardStatus() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(testReservationBoard));

        boolean result = boardService.changeBoardStatus(1L, BoardStatus.COMPLETED);

        assertTrue(result);
        verify(boardRepository, times(1)).findById(1L);
        assertEquals(BoardStatus.COMPLETED, testReservationBoard.getBoardStatus());
    }
}