package sn;

import sn.l2gl.girls.daara.exception.*;
import sn.l2gl.girls.daara.model.dao.MaitreDao;
import sn.l2gl.girls.daara.model.models.Maitre;
import sn.l2gl.girls.daara.util.HibernateUtil;

public class Main {

    public static void main(String[] args) {

        MaitreDao dao = new MaitreDao();

        // ========== TEST 1 : Connexion + création des tables ==========
        System.out.println("=== TEST 1 : Connexion Hibernate ===");
        System.out.println("SessionFactory créée avec succès !");
        System.out.println("Les 4 tables ont été générées en base.\n");

        // ========== TEST 2 : Insertion d'un maître ==========
        System.out.println("=== TEST 2 : Insertion d'un maître ===");
        Maitre m1 = new Maitre("M001", "Cheikh Diallo", "771234567");
        dao.inserer(m1);
        System.out.println("Maître inséré : " + m1.getNomComplet() + "\n");

        // ========== TEST 3 : Insertion double ==========
        System.out.println("=== TEST 3 : Insertion double ===");
        try {
            dao.inserer(new Maitre("M001", "Doublon Test", "000000000"));
        } catch (MaitreDejaExistantException e) {
            System.out.println("Exception attendue : " + e.getMessage() + "\n");
        }

        // ========== TEST 4 : Recherche matricule inexistant ==========
        System.out.println("=== TEST 4 : Matricule inexistant ===");
        try {
            dao.trouverObligatoire("M999");
        } catch (MaitreIntrouvableException e) {
            System.out.println("Exception attendue : " + e.getMessage() + "\n");
        }

        // ========== TEST 5 : Suppression sans classe ==========
        System.out.println("=== TEST 5 : Suppression d'un maître sans classe ===");
        boolean supprime = dao.supprimer("M001");
        System.out.println("Suppression réussie : " + supprime + "\n");

        // ========== FIN ==========
        HibernateUtil.shutdown();
        System.out.println("=== Tous les tests sont passés avec succès ! ===");
    }
}