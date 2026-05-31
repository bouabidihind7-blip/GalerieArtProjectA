package service;

import entities.Oeuvre;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class OeuvreService {

    // ── Ajouter ──────────────────────────────────────────────────
    public void saveOeuvre(Oeuvre o) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(o);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // ── Modifier ─────────────────────────────────────────────────
    public void updateOeuvre(Oeuvre o) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(o);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // ── Supprimer ────────────────────────────────────────────────
    public void deleteOeuvre(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Oeuvre o = (Oeuvre) session.get(Oeuvre.class, id);
            if (o != null) session.delete(o);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // ── Lister tout ──────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public List<Oeuvre> getAllOeuvres() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.createQuery("from Oeuvre").list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }
}
