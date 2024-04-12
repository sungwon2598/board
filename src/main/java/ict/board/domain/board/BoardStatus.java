package ict.board.domain.board;

public enum BoardStatus {
    UNCHECKED("미조치"), // 미확인
    IN_PROGRESS("보류"), // 처리중
    COMPLETED("완료"),
    NIGHT_SHIFT("야간 이전");// 처리완료

    private final String koreanName;

    BoardStatus(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
