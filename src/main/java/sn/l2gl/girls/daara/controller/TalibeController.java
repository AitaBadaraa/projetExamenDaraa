package sn.l2gl.girls.daara.controller;

import sn.l2gl.girls.daara.exception.DaaraException;
import sn.l2gl.girls.daara.model.dao.ClasseDao;
import sn.l2gl.girls.daara.model.dao.TalibeDao;
import sn.l2gl.girls.daara.model.models.Classe;
import sn.l2gl.girls.daara.model.models.Talibe;
import sn.l2gl.girls.daara.util.CsvExporter;
import sn.l2gl.girls.daara.view.TalibeView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TalibeController {

    private final TalibeDao dao = new TalibeDao();
    private final ClasseDao classeDao = new ClasseDao();
    private final TalibeView vue;
    private Talibe enCours;

    public TalibeController(TalibeView vue) {
        this.vue = vue;

        vue.setListeClasses(classeDao.listerTous());

        vue.getBoutonAjouter().addActionListener(e -> enregistrer());
        vue.getBoutonModifier().addActionListener(e -> enregistrer());
        vue.getBoutonSupprimer().addActionListener(e -> supprimer());
        vue.getBoutonRechercher().addActionListener(e -> rechercher());
        vue.getBoutonToutAfficher().addActionListener(e -> { vue.reinitialiser(); rafraichir(); });
        vue.getBoutonExporter().addActionListener(e -> exporter());

        vue.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int ligne = vue.getTable().getSelectedRow();
            if (ligne < 0) return;
            String matricule = (String) vue.getModeleTable().getValueAt(ligne, 0);
            try {
                Talibe t = dao.trouverObligatoire(matricule);
                enCours = t;
                vue.remplir(t);
            } catch (DaaraException ex) {
                afficherErreur(ex.getMessage());
            }
        });

        rafraichir();
    }

    private void enregistrer() {
        String matricule = vue.getChampMatricule().getText().trim();
        String prenom = vue.getChampPrenom().getText().trim();
        String nom = vue.getChampNom().getText().trim();
        String dateTexte = vue.getChampDateNaissance().getText().trim();
        String nomTuteur = vue.getChampNomTuteur().getText().trim();
        String telTuteur = vue.getChampTelephoneTuteur().getText().trim();
        Classe classe = (Classe) vue.getComboClasse().getSelectedItem();

        if (matricule.isEmpty() || prenom.isEmpty() || nom.isEmpty()) {
            afficherErreur("Matricule, prénom et nom sont obligatoires.");
            return;
        }
        if (classe == null) {
            afficherErreur("Veuillez sélectionner une classe.");
            return;
        }
        LocalDate dateNaissance;
        try {
            dateNaissance = dateTexte.isEmpty() ? null : LocalDate.parse(dateTexte);
        } catch (DateTimeParseException ex) {
            afficherErreur("Date invalide, format attendu : AAAA-MM-JJ");
            return;
        }

        try {
            if (enCours == null) {
                Talibe nouveau = new Talibe();
                nouveau.setMatricule(matricule);
                nouveau.setPrenom(prenom);
                nouveau.setNom(nom);
                nouveau.setDateNaissance(dateNaissance);
                nouveau.setNomTuteur(nomTuteur);
                nouveau.setTelephoneTuteur(telTuteur);
                nouveau.setClasse(classe);
                dao.inserer(nouveau);
                JOptionPane.showMessageDialog(vue, "Talibé ajouté avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                enCours.setPrenom(prenom);
                enCours.setNom(nom);
                enCours.setDateNaissance(dateNaissance);
                enCours.setNomTuteur(nomTuteur);
                enCours.setTelephoneTuteur(telTuteur);
                enCours.setClasse(classe);
                dao.modifier(enCours);
                JOptionPane.showMessageDialog(vue, "Talibé modifié avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
            vue.reinitialiser();
            enCours = null;
            rafraichir();
        } catch (DaaraException ex) {
            afficherErreur(ex.getMessage());
        }
    }

    private void supprimer() {
        int ligne = vue.getTable().getSelectedRow();
        if (ligne < 0) {
            afficherErreur("Sélectionnez d'abord un talibé dans la table.");
            return;
        }
        String matricule = (String) vue.getModeleTable().getValueAt(ligne, 0);
        int confirmation = JOptionPane.showConfirmDialog(vue,
                "Confirmer la suppression du talibé " + matricule + " ? (ses progressions seront aussi supprimées)",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) return;

        try {
            dao.supprimer(matricule);
            JOptionPane.showMessageDialog(vue, "Talibé supprimé.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            vue.reinitialiser();
            enCours = null;
            rafraichir();
        } catch (DaaraException ex) {
            afficherErreur(ex.getMessage());
        }
    }

    private void rechercher() {
        String texte = vue.getChampRecherche().getText().trim();
        if (texte.isEmpty()) { rafraichir(); return; }
        vue.afficher(dao.rechercherParNom(texte));
    }

    private void rafraichir() {
        vue.afficher(dao.listerTous());
    }

    private void exporter() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("talibes.csv"));
        if (chooser.showSaveDialog(vue) != JFileChooser.APPROVE_OPTION) return;

        List<Talibe> talibes = dao.listerTous();
        String[] entetes = {"matricule", "prenom", "nom", "dateNaissance", "nomTuteur", "telephoneTuteur", "classe"};
        List<String[]> lignes = new ArrayList<>();
        for (Talibe t : talibes) {
            lignes.add(new String[]{
                    t.getMatricule(), t.getPrenom(), t.getNom(),
                    t.getDateNaissance() != null ? t.getDateNaissance().toString() : "",
                    t.getNomTuteur(), t.getTelephoneTuteur(),
                    t.getClasse() != null ? t.getClasse().getCode() : ""
            });
        }
        try {
            CsvExporter.exporter(chooser.getSelectedFile(), entetes, lignes);
            JOptionPane.showMessageDialog(vue, "Export terminé : " + chooser.getSelectedFile(), "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            afficherErreur("Échec de l'export CSV : " + ex.getMessage());
        }
    }

    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(vue, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}