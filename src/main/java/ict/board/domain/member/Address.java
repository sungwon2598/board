package ict.board.domain.member;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Embeddable
@Getter
public class Address {

    private String team;

    @Enumerated(EnumType.STRING)
    private Building building;

    private String zipcode;

    protected Address() {
    }

    public Address(String team, Building building, String zipcode) {
        this.team = team;
        this.building = building;
        this.zipcode = zipcode;
    }
}
