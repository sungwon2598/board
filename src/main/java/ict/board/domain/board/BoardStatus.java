package ict.board.domain.board;

public enum BoardStatus {
    UNCHECKED("미확인"), // 미확인
    IN_PROGRESS("처리중"), // 처리중
    COMPLETED("처리완료"); // 처리완료

    private final String koreanName;

    BoardStatus(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
