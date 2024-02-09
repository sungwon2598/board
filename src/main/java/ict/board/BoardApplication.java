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
