package ict.board.domain.member;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class Location {

    @Enumerated(EnumType.STRING)
    private Building building;

    private String roomNumber; // '호수' 정보

    public Location() {
    }

    public Location(Building building, String roomNumber) {
        this.building = building;
        this.roomNumber = roomNumber;
    }

    // Getter 메소드들
    public Building getBuilding() {
        return building;
    }

    public String getRoomNumber() {
        return roomNumber;
    }
}
