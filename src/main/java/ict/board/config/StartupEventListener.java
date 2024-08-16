package ict.board.config;

import ict.board.domain.member.Building;
import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Location;
import ict.board.domain.member.Role;
import ict.board.domain.member.ShiftType;
import ict.board.repository.IctStaffMemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupEventListener {

    private final IctStaffMemberRepository ictStaffMemberRepository;

    @Value("${ict.ai-ict.email}")
    private String email;

    @Value("${ict.ai-ict.password}")
    private String password;

    @Value("${ict.ai-ict.name}")
    private String name;

    @Value("${ict.ai-ict.team}")
    private String team;

    @Value("${ict.ai-ict.memberNumber}")
    private String memberNumber;


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        Optional<IctStaffMember> staffOptional = ictStaffMemberRepository.findByEmail(email);

        if (staffOptional.isEmpty()) {
            IctStaffMember aiIctStaffMember = new IctStaffMember(
                    email,
                    name,
                    password,
                    new Location(Building.BY, "214"),
                    team,
                    memberNumber,
                    ShiftType.DAY,
                    Role.STAFF
            );
            ictStaffMemberRepository.save(aiIctStaffMember);
            log.info("AI-ICT staff member created");
        }
    }
}
