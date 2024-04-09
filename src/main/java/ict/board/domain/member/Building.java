package ict.board.domain.member;

import lombok.Getter;

@Getter
public enum Building {

    HC("호천관"),
    HH("흥학관"),
    SJ("세종관"),
    JD("지덕관"),
    BY("배양관"),
    NR("누리관"),
    SE("서일관"),
    DS("도서관");

    private final String koreanName;

    Building(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
