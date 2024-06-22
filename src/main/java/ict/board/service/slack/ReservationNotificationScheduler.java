package ict.board.service.slack;

import ict.board.domain.board.BoardStatus;
import ict.board.domain.board.ReservationBoard;
import ict.board.repository.BoardRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationNotificationScheduler {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분");
    @Autowired
    private BoardRepository reservationBoardRepository;
    @Autowired
    private SlackMessageSender slackMessageSender;

    @Scheduled(fixedRate = 60000) // 매 1분마다 실행
    public void sendReservationNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);

        List<ReservationBoard> reservations = reservationBoardRepository.findByBoardStatusAndReservationDateBetween(
                BoardStatus.UNCHECKED, now, thirtyMinutesLater);

        for (ReservationBoard reservation : reservations) {
            sendSlackNotification(reservation);
        }
    }

    private void sendSlackNotification(ReservationBoard reservation) {
        HashMap<String, String> data = new HashMap<>();
        data.put("제목 : ", reservation.getTitle());
        data.put("내용 : ", reservation.getContent());
        data.put("요청자 : ", reservation.getRequester());
        data.put("위치 : ", reservation.getRequesterLocation());
        data.put("예약일정 : ", reservation.getReservationDate().format(formatter));

        slackMessageSender.sendMessage(slackMessageSender.getSlackWebhookUrlReservation(), "30분뒤에 예약된 민원이 있습니다",
                data);
    }
}