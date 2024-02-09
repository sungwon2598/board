package ict.board;

import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
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
            //Address address = new Address("seoul", "ganbuk", "01198");
            //member1.setAddress(address);
            member1.setPassword("1234");
            member1.setName("홍성원");
            member1.setEmail("tjddnjs2598@naver.com");
            em.persist(member1);




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
