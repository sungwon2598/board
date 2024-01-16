package ict.board;

import ict.board.domain.reply.Reply;
import ict.board.domain.board.Board;
import ict.board.domain.member.Address;
import ict.board.domain.member.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;


public class BoardApplication {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member1 = new Member();
            Address address = new Address("seoul", "ganbuk", "01198");
            member1.setAddress(address);
            member1.setPassword("1234");
            member1.setName("홍성원");
            member1.setEmail("tjddnjs2598@naver.com");
            em.persist(member1);

            Board board1 = new Board();
            board1.addMember(member1);
            board1.setTitle("첫 게시글 제목");
            board1.setContent("안녕하세요 첫번재 게시글 내용입니다");
            em.persist(board1);

            Reply reply1 = new Reply();
            reply1.addBoard(board1);
            reply1.addMember(member1);
            reply1.setContent("첫번째 댓글입니다");
            em.persist(reply1);

            Reply reply2 = new Reply();
            reply2.addBoard(board1);
            reply2.addMember(member1);
            reply2.setContent("두번째 댓글입니다");
            em.persist(reply2);


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            System.out.println("=============error=============");
        } finally {
            em.close();
        }

        emf.close();
    }

}
