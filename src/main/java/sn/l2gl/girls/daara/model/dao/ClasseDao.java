package sn.l2gl.girls.daara.model.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import sn.l2gl.girls.daara.exception.ClasseDejaExistanteException;
import sn.l2gl.girls.daara.exception.ClasseIntrouvableException;
import sn.l2gl.girls.daara.exception.SuppressionImpossibleException;
import sn.l2gl.girls.daara.model.models.Classe;
import sn.l2gl.girls.daara.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class ClasseDao implements Dao<Classe, String> {
    // Ouvrir une session
    // En Hibernate, une Session représente une connexion de travail avec la base de données.
    private Session ouvrirSession() {
        //getSessionFactory c'est l'usine on le configure qu'une seule fois au demarrage
        return HibernateUtil.getSessionFactory().openSession();
    }

    // Inserer une classe
    @Override
    public Classe inserer(Classe c){
        // Verifie si la classe existe deja dans la base
        if(trouver(c.getCode()).isPresent()){
            throw new ClasseDejaExistanteException(c.getCode());
        }
        //Les transactions sont surtout nécessaires pour les opérations qui modifient la base
        // Pour une transaction soit tout reussit, soit rien n'est enregistré
        Transaction transaction = null; // declaration

        // try-with-resources
        try(Session session = ouvrirSession()){ // Ouvre une session et la ferme automatiquement à la fin de cette derniere
            transaction = session.beginTransaction(); // Demarre la transaction
            session.persist(c); // prépare l'insertion
            transaction.commit(); // valide définitivement
            return c;
        } catch (Exception e) {
            if(transaction != null){
                transaction.rollback(); // Annule tout ce qui avait été commencé.
            }
            throw e; // relance l'exception
            // Dao ne doit pas afficher les messages à l'utilisateur mais au controller
        }
    }

    // Trouver une classe
    @Override
    public Optional<Classe> trouver(String code){
        try(Session session = ouvrirSession()){
            // Permet juste de tranformer ce qu'on aura comme resultat en objet
            Classe c = session.get(Classe.class, code);
            return Optional.ofNullable(c);
        }
    }

    // Recherche unique qui lève une exception métier si absente
    // (pratique en modification/suppression/sélection dans le controller)
    public Classe trouverObligatoire(String code){
        return trouver(code).orElseThrow(() -> new ClasseIntrouvableException(code));
    }

    //Lister Toutes les classes
    @Override
    public List<Classe> listerTous(){
        try(Session session = ouvrirSession()){
            return session.createQuery(
                    "from Classe order by libelle",
                    // Permet juste de tranformer ce qu'on aura comme resultat en objet
                    Classe.class
            ).list();
        }
    }

    //Modifier une classe
    @Override
    public Optional<Classe> modifier(Classe c){
        if(trouver(c.getCode()).isEmpty()){
            throw new ClasseIntrouvableException(c.getCode());
        }

        Transaction transaction = null;
        try(Session session = ouvrirSession()){
            transaction = session.beginTransaction();
            Classe resultat = (Classe) session.merge(c); //permet juste de fusionner et mets à jour la classe
            transaction.commit();
            return Optional.of(resultat); // "ce qui a été réellement enregistré, confirmé par la base."
        } catch (Exception e) {
            if(transaction != null){
                transaction.rollback();
            }
            throw e;
        }
    }

    // Supprimer une classe
    @Override
    public boolean supprimer(String code){
        //  La classe doit exister
        Classe c = trouverObligatoire(code); // appelle trouver(code), qui ouvre SA PROPRE session, l'utilise, PUIS LA FERME

        Transaction transaction = null;
        try(Session session = ouvrirSession()){ // session B, différente de la session A
            //  Règle métier : suppression interdite si la classe contient
            //    encore au moins un talibé.
            Long nbTalibes = session.createQuery(
                    """
                        select count(t) from Talibe t
                        where t.classe.code = :code 
                    """, // est un paramètre nommé — un espace réservé dans la requête
                    Long.class // count(...) renvoie toujours un type Long par convention
            ).setParameter("code", code).uniqueResult();

            if (nbTalibes != null && nbTalibes > 0) {
                throw new SuppressionImpossibleException(
                        "Impossible de supprimer la classe " + code
                                + " : elle contient encore " + nbTalibes + " talibé(s)."
                );
            }

            transaction = session.beginTransaction();
            // on retrouve l'entité DANS cette session avant de la supprimer
            Classe managed = session.get(Classe.class, code);
            session.remove(managed);
            transaction.commit();
            return true;
        } catch (SuppressionImpossibleException e) {
            throw e; // on ne rollback pas : rien n'a été commencé pour cette règle
        } catch (Exception e) {
            if(transaction != null){
                transaction.rollback();
            }
            throw e;
        }
    }

    // "Cherche toutes les classes dont le libellé contient le texte tapé (n'importe où dedans, insensible à la casse), " +
    // "triées par ordre alphabétique."
    // Rechercher Par libelle
    public List<Classe> rechercherParLibelle(String libelle){
        try(Session session = ouvrirSession()){
            return session.createQuery(
                    """ 
                        from Classe c 
                        where lower(c.libelle)
                        like lower(:libelle)
                        order by c.libelle
                    """,
                    Classe.class
            ).setParameter("libelle", "%" + libelle + "%").list();
        }
    }


    //Lister par maitre
    //  prend un seul matricule de maître en entrée, et cherche toutes les classes que ce maître encadre
    public List<Classe> listerParMaitre(String matricule){
        try(Session session = ouvrirSession()){
            return session.createQuery(
                    """
                        from Classe c
                        where c.maitre.matricule = :matricule
                        order by c.libelle
                    """,
                    Classe.class
            ).setParameter("matricule",matricule).list();
        }
    }

}
