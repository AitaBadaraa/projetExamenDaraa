package sn.l2gl.girls.daara.model.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import sn.l2gl.girls.daara.exception.TalibeDejaExistantException;
import sn.l2gl.girls.daara.exception.TalibeIntrouvableException;
import sn.l2gl.girls.daara.model.models.Talibe;
import sn.l2gl.girls.daara.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class TalibeDao implements Dao<Talibe, String>{
    @Override
    public Talibe inserer(Talibe entity) {
        //on ouvre une session de  connexion vers la base de donnee
        Session s= HibernateUtil.getSessionFactory().openSession();
        // On vérifie d'abord si le matricule existe déjà
        Talibe existant = s.find(Talibe.class, entity.getMatricule());
        if (existant != null) {
            s.close();
            throw new TalibeDejaExistantException(entity.getMatricule());
        }
        //permet l'ecriture
        Transaction tx= s.beginTransaction();
        //Ajouter dans la base
        s.persist(entity);
        //Confirme l'ajout
        tx.commit();
        s.close();
        return entity;
    }

    @Override
    public Optional<Talibe> trouver(String matricule) {

        //se connecter à la base
        Session s = HibernateUtil.getSessionFactory().openSession();
        //on cherche par clé primaire avec find() le talibe
        Talibe t = s.find(Talibe.class, matricule);
        s.close(); //ferner la connexion
        return Optional.ofNullable(t);

    }

    // Recherche obligatoire utilisée quand on modifie/supprime/affiche
    public Talibe trouverObligatoire(String matricule) {
        return trouver(matricule)
                .orElseThrow(() -> new TalibeIntrouvableException(matricule));
    }

    @Override
    public List<Talibe> listerTous() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Talibe> liste = s.createQuery("from Talibe", Talibe.class).list();
        s.close();
         return liste;
    }

    @Override
    public Optional<Talibe> modifier(Talibe entity) {
        // On vérifie que le talibé existe avant de modifier
        trouverObligatoire(entity.getMatricule());

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx= s.beginTransaction();
        s.merge(entity); //Modification de l'objet
        tx.commit();
        s.close();
        return Optional.of(entity);
    }

    @Override
    public boolean supprimer(String matricule) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx= s.beginTransaction();
        Talibe t = s.find(Talibe.class, matricule);

        if (t == null) {
            s.close();
            throw new TalibeIntrouvableException(matricule);
        }

        s.remove(t); // grace à la cascade sur Talibe, ses progressions seront aussi supprimées
        tx.commit(); //on valide
        s.close();
        return true;

    }

//    public  Session getSession() {
//        return HibernateUtil.getSessionFactory().openSession();
//    }
    public  List<Talibe> rechercherParNom(String nom) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Talibe> liste = s.createQuery( "from Talibe t where lower(t.nom) like lower(:n)",
                Talibe.class)
                .setParameter("n", "%" + nom + "%")
                .list();
        s.close();
        return liste;
    }

    public List<Talibe> listerParClasse(String code){
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Talibe> liste = s.createQuery(
                "from Talibe t where t.classe.code = :c",
                Talibe.class)
                .setParameter("c", code)
                .list();
        s.close();
        return liste;
    }
}
