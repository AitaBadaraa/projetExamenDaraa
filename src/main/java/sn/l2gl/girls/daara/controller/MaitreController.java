package sn.l2gl.girls.daara.controller;

import sn.l2gl.girls.daara.exception.DaaraException;
import sn.l2gl.girls.daara.model.dao.MaitreDao;
import sn.l2gl.girls.daara.model.models.Maitre;
import sn.l2gl.girls.daara.view.MaitreView;
import sn.l2gl.girls.daara.util.CsvExporter;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Component;
import java.util.List;

public class MaitreController {
    private final MaitreDao dao = new MaitreDao();
    private final MaitreView vue;
    private Maitre enCours; // null = creation, sinon = modification

    public MaitreController(MaitreView vue) {
        this.vue = vue;

        vue.getBoutonAjouter().addActionListener(
                e -> enregistrer()
        );
        vue.getBoutonModifier().addActionListener(
                e -> enregistrer()
        );
        vue.getBoutonSupprimer().addActionListener(
                e -> supprimer()
        );
        vue.getBoutonRechercher().addActionListener(
                e -> rechercher()
        );
        vue.getBoutonToutAfficher().addActionListener(
                e -> {
                    vue.reinitialiser();
                    rafraichir();
                }
        );
        vue.getBoutonExporter().addActionListener(
                e -> exporter()
        );
        vue.getTable().getSelectionModel().addListSelectionListener(selectionDeLigne());
        rafraichir();
    }

    // Sélection dans la table -> chargement du formulaire
    private ListSelectionListener selectionDeLigne(){
        return e -> {
            if(e.getValueIsAdjusting()){
                return;
            }
            int ligne = vue.getTable().getSelectedRow();
            if(ligne < 0){
                return;
            }
            String matricule = (String) vue.getModeleTable().getValueAt(ligne,0);
            try{
                Maitre maitre = dao.trouverObligatoire(matricule);
                enCours = maitre;
                vue.remplir(maitre);
            } catch (DaaraException ex) {
                afficherErreur(ex.getMessage());
            }
        };
    }

    // Ajouter / Modifier
    private void enregistrer(){
        String matricule = vue.getChampMatricule().getText().trim();
        String nomComplet = vue.getChampNomComplet().getText().trim();
        String telephone = vue.getChampTelephone().getText().trim();

        if(!validerSaisies(matricule, nomComplet)){
            return;
        }

        try {
            if(enCours == null){
                //Creation
                Maitre nouveau = new Maitre(matricule, nomComplet,telephone);
                dao.inserer(nouveau);
                JOptionPane.showMessageDialog(vue, "Maitre ajouté avec succés.",
                        "Succes", JOptionPane.INFORMATION_MESSAGE);
            }else {
                //Modification sans toucher à la clé on la reutilise
                enCours.setNomComplet(nomComplet);
                enCours.setTelephone(telephone);
                dao.modifier(enCours);
                JOptionPane.showMessageDialog(vue, "Maitre modifié avec succés.",
                        "Succes", JOptionPane.INFORMATION_MESSAGE);
            }
            vue.reinitialiser();
            enCours = null;
            rafraichir();
        } catch (DaaraException ex) {
            afficherErreur(ex.getMessage());
        }
    }

    private boolean validerSaisies(String matricule, String nomComplet){
        if(matricule.isEmpty() || nomComplet.isEmpty()){
            afficherErreur("Le matricule et le nom complet sont obligatoires");
            return false;
        }
        return true;
    }

    // Supprimer
    private void supprimer(){
        int ligne = vue.getTable().getSelectedRow();
        if(ligne < 0){
            afficherErreur("Sélectionnez d'abord un maître dans la table.");
            return;
        }
        String matricule = (String) vue.getModeleTable().getValueAt(ligne,0);

        int confirmation = JOptionPane.showConfirmDialog(vue,
                "Confirmer la suppression du maitre" + matricule + "?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if(confirmation != JOptionPane.YES_OPTION){
            return;
        }

        try {
            dao.supprimer(matricule);
            JOptionPane.showMessageDialog(vue, "Maître supprimé.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            vue.reinitialiser();
            enCours = null;
            rafraichir();
        } catch (DaaraException ex) {
            // Capture notamment SuppressionImpossibleException (maître encadrant encore des classes).
            afficherErreur(ex.getMessage());
        }
    }

    // Rechercher / Tout afficher
    private void rechercher(){
        String texte = vue.getChampRecherche().getText().trim();
        if(texte.isEmpty()){
            rafraichir();
            return;
        }
        try {
            vue.afficher(dao.rechercherParNom(texte));
        } catch (DaaraException ex) {
            afficherErreur(ex.getMessage());
        }
    }

    private void rafraichir(){
        vue.afficher(dao.listerTous());
    }

    // Export CSV
    private void exporter(){
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("maitres.csv"));
        int choix = chooser.showSaveDialog(vue);
        if(choix != JFileChooser.APPROVE_OPTION){
            return;
        }

        List<Maitre> maitres = dao.listerTous();
        String[] entetes = {"matricule", "nomComplet", "telephone"};
        List<String[]> lignes = new ArrayList<>();
        for (Maitre m : maitres){
            lignes.add(new String[]{m.getMatricule(), m.getNomComplet(), m.getTelephone()});
        }

        try {
            CsvExporter.exporter(chooser.getSelectedFile(), entetes, lignes);
            JOptionPane.showMessageDialog(vue, "Export terminé : " + chooser.getSelectedFile(),
                            "Succes", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            afficherErreur("Echec de l'export CSV : " + ex.getMessage());
        }
    }

    // Utilitaire

    private void afficherErreur(String message){
        JOptionPane.showMessageDialog((Component) vue, message, "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }
}
