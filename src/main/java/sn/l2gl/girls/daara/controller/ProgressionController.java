package sn.l2gl.girls.daara.controller;

import sn.l2gl.girls.daara.exception.DaaraException;
import sn.l2gl.girls.daara.model.dao.ProgressionDao;
import sn.l2gl.girls.daara.model.dao.TalibeDao;
import sn.l2gl.girls.daara.model.models.Progression;
import sn.l2gl.girls.daara.model.models.Talibe;
import sn.l2gl.girls.daara.util.CsvExporter;
import sn.l2gl.girls.daara.view.ProgressionView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ProgressionController {

    private final ProgressionDao dao = new ProgressionDao();
    private final TalibeDao talibeDao = new TalibeDao();
    private final ProgressionView vue;
    private Progression enCours;

    public ProgressionController(ProgressionView vue) {
        this.vue = vue;

        vue.setListeTalibes(talibeDao.listerTous());

        vue.getBoutonAjouter().addActionListener(e -> enregistrer());
        vue.getBoutonModifier().addActionListener(e -> enregistrer());
        vue.getBoutonSupprimer().addActionListener(e -> supprimer());
        vue.getBoutonFiltrer().addActionListener(e -> filtrerParTalibe());
        vue.getBoutonToutAfficher().addActionListener(e -> { vue.reinitialiser(); rafraichir(); });
        vue.getBoutonExporter().addActionListener(e -> exporter());

        vue.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int ligne = vue.getTable().getSelectedRow();
            if (ligne < 0) return;
            Long id = Long.valueOf(vue.getModeleTable().getValueAt(ligne, 0).toString());
            try {
                Progression p = dao.trouverObligatoire(id);
                enCours = p;
                vue.remplir(p);
            } catch (DaaraException ex) {
                afficherErreur(ex.getMessage());
            }
        });

        rafraichir();
    }

    private void enregistrer() {
        Talibe talibe = (Talibe) vue.getComboTalibe().getSelectedItem();
        String sourate = vue.getChampSourate().getText().trim();
        String versetsTexte = vue.getChampNombreVersets().getText().trim();
        String dateTexte = vue.getChampDateEvaluation().getText().trim();
        String appreciation = vue.getChampAppreciation().getText().trim();

        int nombreVersets;
        try {
            nombreVersets = Integer.parseInt(versetsTexte);
        } catch (NumberFormatException ex) {
            afficherErreur("Le nombre de versets doit être un entier.");
            return;
        }
        LocalDate dateEvaluation;
        try {
            dateEvaluation = dateTexte.isEmpty() ? null : LocalDate.parse(dateTexte);
        } catch (DateTimeParseException ex) {
            afficherErreur("Date invalide, format attendu : AAAA-MM-JJ");
            return;
        }

        try {
            if (enCours == null) {
                Progression nouvelle = new Progression();
                nouvelle.setTalibe(talibe);
                nouvelle.setSourate(sourate);
                nouvelle.setNombreVersets(nombreVersets);
                nouvelle.setDateEvaluation(dateEvaluation);
                nouvelle.setAppreciation(appreciation);
                dao.inserer(nouvelle);
                JOptionPane.showMessageDialog(vue, "Progression ajoutée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                enCours.setTalibe(talibe);
                enCours.setSourate(sourate);
                enCours.setNombreVersets(nombreVersets);
                enCours.setDateEvaluation(dateEvaluation);
                enCours.setAppreciation(appreciation);
                dao.modifier(enCours);
                JOptionPane.showMessageDialog(vue, "Progression modifiée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
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
            afficherErreur("Sélectionnez d'abord une progression dans la table.");
            return;
        }
        Long id = Long.valueOf(vue.getModeleTable().getValueAt(ligne, 0).toString());
        int confirmation = JOptionPane.showConfirmDialog(vue,
                "Confirmer la suppression de cette progression ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) return;

        try {
            dao.supprimer(id);
            JOptionPane.showMessageDialog(vue, "Progression supprimée.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            vue.reinitialiser();
            enCours = null;
            rafraichir();
        } catch (DaaraException ex) {
            afficherErreur(ex.getMessage());
        }
    }

    private void filtrerParTalibe() {
        Talibe talibe = (Talibe) vue.getComboFiltreTalibe().getSelectedItem();
        if (talibe == null) { rafraichir(); return; }
        vue.afficher(dao.listerParTalibe(talibe.getMatricule()));
    }

    private void rafraichir() {
        vue.afficher(dao.listerTous());
    }

    private void exporter() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("progressions.csv"));
        if (chooser.showSaveDialog(vue) != JFileChooser.APPROVE_OPTION) return;

        List<Progression> progressions = dao.listerTous();
        String[] entetes = {"id", "talibe", "sourate", "nombreVersets", "dateEvaluation", "appreciation"};
        List<String[]> lignes = new ArrayList<>();
        for (Progression p : progressions) {
            lignes.add(new String[]{
                    String.valueOf(p.getId()),
                    p.getTalibe() != null ? p.getTalibe().getMatricule() : "",
                    p.getSourate(), String.valueOf(p.getNombreVersets()),
                    p.getDateEvaluation() != null ? p.getDateEvaluation().toString() : "",
                    p.getAppreciation()
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