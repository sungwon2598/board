package ict.board.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClassroomDto {
    private String name;
    private int capacity;
    private List<Long> softwareIds;
}
