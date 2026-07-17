package sn.l2gl.girls.daara.model.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import sn.l2gl.girls.daara.exception.ProgressionIntrouvableException;
import sn.l2gl.girls.daara.exception.ProgressionInvalideException;
import sn.l2gl.girls.daara.model.models.Progression;
import sn.l2gl.girls.daara.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class ProgressionDao implements Dao<Progression, Long> {

    private Session ouvrirSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    private void valider(Progression p) {
        if (p.getTalibe() == null) {
            throw new ProgressionInvalideException("Le talibé est obligatoire.");
        }
        if (p.getSourate() == null || p.getSourate().isBlank()) {
            throw new ProgressionInvalideException("La sourate est obligatoire.");
        }
        if (p.getNombreVersets() < 0) {
            throw new ProgressionInvalideException("Le nombre de versets doit être positif.");
        }
    }

    @Override
    public Progression inserer(Progression p) {
        valider(p);
        Transaction transaction = null;
        try (Session session = ouvrirSession()) {
            transaction = session.beginTransaction();
            session.persist(p);
            transaction.commit();
            return p;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    @Override
    public Optional<Progression> trouver(Long id) {
        try (Session session = ouvrirSession()) {
            Progression p = session.get(Progression.class, id);
            return Optional.ofNullable(p);
        }
    }

    public Progression trouverObligatoire(Long id) {
        return trouver(id).orElseThrow(() -> new ProgressionIntrouvableException(id));
    }

    @Override
    public List<Progression> listerTous() {
        try (Session session = ouvrirSession()) {
            return session.createQuery(
                    "from Progression order by dateEvaluation desc",
                    Progression.class
            ).list();
        }
    }

    @Override
    public Optional<Progression> modifier(Progression p) {
        valider(p);
        if (trouver(p.getId()).isEmpty()) {
            throw new ProgressionIntrouvableException(p.getId());
        }
        Transaction transaction = null;
        try (Session session = ouvrirSession()) {
            transaction = session.beginTransaction();
            Progression resultat = (Progression) session.merge(p);
            transaction.commit();
            return Optional.of(resultat);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    @Override
    public boolean supprimer(Long id) {
        trouverObligatoire(id);
        Transaction transaction = null;
        try (Session session = ouvrirSession()) {
            transaction = session.beginTransaction();
            Progression managed = session.get(Progression.class, id);
            session.remove(managed);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public List<Progression> listerParTalibe(String matriculeTalibe) {
        try (Session session = ouvrirSession()) {
            return session.createQuery(
                    """
                        from Progression p
                        where p.talibe.matricule = :matricule
                        order by p.dateEvaluation desc
                    """,
                    Progression.class
            ).setParameter("matricule", matriculeTalibe).list();
        }
    }
}