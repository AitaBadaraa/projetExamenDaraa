package sn.l2gl.girls.daara.model.dao;


import sn.l2gl.girls.daara.model.models.Maitre;
import sn.l2gl.girls.daara.exception.*;
import sn.l2gl.girls.daara.util.HibernateUtil;
import org.hibernate.*;

import java.util.*;

public class MaitreDao implements Dao<Maitre, String> {

    // Ouvre une nouvelle session Hibernate à chaque opération
    private static Session getSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    // Insère un nouveau maître en base
    // Leve MaitreDejaExistantException si le matricule existe déjà
    @Override
    public Maitre inserer(Maitre m) {
        try (Session s = getSession()) {
            Transaction tx = s.beginTransaction();

            // Vérification de doublon avant insertion
            if (trouver(m.getMatricule()).isPresent()) {
                throw new MaitreDejaExistantException(m.getMatricule());
            }

            s.persist(m); // Sauvegarde en base
            tx.commit();  // Valide la transaction
            return m;
        }
    }

    // Recherche un maître par son matricule
    // Retourne Optional.empty() si non trouvé
    @Override
    public Optional<Maitre> trouver(String id) {
        try (Session s = getSession()) {
            return Optional.ofNullable(s.find(Maitre.class, id));
        }
    }

    // Recherche un maître et lève une exception s'il n'existe pas
    // Utilisé quand la présence du maître est obligatoire
    public Maitre trouverObligatoire(String id) {
        return trouver(id)
                .orElseThrow(() -> new MaitreIntrouvableException(id));
    }

    // Retourne la liste de tous les maîtres en base
    @Override
    public  List<Maitre> listerTous() {
        try (Session s = getSession()) {
            return s.createQuery("from Maitre", Maitre.class).list();
        }
    }

    // Met à jour un maître existant
    // Leve MaitreIntrouvableException si le matricule n'existe pas
    @Override
    public Optional<Maitre> modifier(Maitre m) {
        try (Session s = getSession()) {
            Transaction tx = s.beginTransaction();

            // Vérification que le maître existe avant modification
            if (trouver(m.getMatricule()).isEmpty()) {
                throw new MaitreIntrouvableException(m.getMatricule());
            }

            s.merge(m); // Fusionne les changements en base
            tx.commit();
            return Optional.of(m);
        }
    }

    // Supprime un maître par son matricule
    // Leve MaitreIntrouvableException si non trouvé
    // Leve SuppressionImpossibleException si le maître a des classes
    @Override
    public boolean supprimer(String id) {
        try (Session s = getSession()) {
            Transaction tx = s.beginTransaction();

            // Vérification que le maître existe
            Maitre m = s.find(Maitre.class, id);
            if (m == null) throw new MaitreIntrouvableException(id);

            // Vérifier si le maître a des classes assignées
            Long nbClasses = s.createQuery(
                            "select count(c) from Classe c where c.maitre.matricule = :id",
                            Long.class)
                    .setParameter("id", id)
                    .uniqueResult();

            // On ne peut pas supprimer un maître qui gère des classes
            if (nbClasses > 0) {
                throw new SuppressionImpossibleException(
                        "Impossible de supprimer le maître " + id +
                                " : il a " + nbClasses + " classe(s)."
                );
            }

            s.remove(m); // Suppression en base
            tx.commit();
            return true;
        }
    }

    // Recherche les maîtres dont le nom contient le mot-clé
    // La recherche est insensible à la casse (lower)
    public List<Maitre> rechercherParNom(String nom) {
        try (Session s = getSession()) {
            return s.createQuery(
                            "from Maitre m where lower(m.nomComplet) like lower(:nom)",
                            Maitre.class)
                    .setParameter("nom", "%" + nom + "%")
                    .list();
        }
    }
}

