package ict.board.dto;

import ict.board.domain.board.Board;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
public class GusetInfo {

    public Optional<String> memberName;

    public Page<Board> boards;

}
