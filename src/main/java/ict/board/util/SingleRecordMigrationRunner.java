//package ict.board.util;
//
//import jakarta.persistence.EntityManager;
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Component
//public class SingleRecordMigrationRunner implements CommandLineRunner {
//
//    private final EntityManager entityManager;
//    private final AESConverter aesConverter;
//
//    @Autowired
//    public SingleRecordMigrationRunner(EntityManager entityManager, AESConverter aesConverter) {
//        this.entityManager = entityManager;
//        this.aesConverter = aesConverter;
//    }
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        // 네이티브 쿼리로 직접 데이터 조회
//        List<Object[]> result = entityManager.createNativeQuery(
//                        "SELECT member_id, name, email FROM member WHERE member_id = 2")
//                .getResultList();
//
//        if (!result.isEmpty()) {
//            Object[] row = result.get(0);
//            Long id = ((Number) row[0]).longValue();
//            String name = (String) row[1];
//            String email = (String) row[2];
//
//            System.out.println("마이그레이션 전 데이터:");
//            System.out.println("ID: " + id);
//            System.out.println("Name: " + name);
//            System.out.println("Email: " + email);
//
//            // 데이터 암호화
//            String encryptedName = aesConverter.convertToDatabaseColumn(name);
//            String encryptedEmail = aesConverter.convertToDatabaseColumn(email);
//
//            // 암호화된 데이터로 업데이트
//            int updated = entityManager.createNativeQuery(
//                            "UPDATE member SET name = :encryptedName, email = :encryptedEmail WHERE member_id = :id")
//                    .setParameter("encryptedName", encryptedName)
//                    .setParameter("encryptedEmail", encryptedEmail)
//                    .setParameter("id", id)
//                    .executeUpdate();
//
//            System.out.println("\n업데이트 완료: " + updated + "건 처리됨");
//
//            // 업데이트 결과 확인
//            result = entityManager.createNativeQuery(
//                            "SELECT member_id, name, email FROM member WHERE member_id = 2")
//                    .getResultList();
//
//            if (!result.isEmpty()) {
//                row = result.get(0);
//                System.out.println("\n암호화된 데이터:");
//                System.out.println("ID: " + ((Number) row[0]).longValue());
//                System.out.println("Encrypted Name: " + row[1]);
//                System.out.println("Encrypted Email: " + row[2]);
//            }
//        } else {
//            System.out.println("member_id가 1인 레코드를 찾을 수 없습니다.");
//        }
//    }
//}