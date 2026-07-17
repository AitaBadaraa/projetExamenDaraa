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
    // Ce champ sert de "mémoire" au controller
    private Maitre enCours; // null = creation, sinon = modification

    /*
    * On mémorise la vue reçue, on branche chaque bouton à sa méthode,
    *  on branche la sélection de ligne pour charger un maître
    *  dans le formulaire, puis on charge immédiatement la liste actuelle
    *  pour que l'utilisateur voie tout de suite les données existantes.
    */
    public MaitreController(MaitreView vue) {
        this.vue = vue; // On stocke la vue reçue en paramètre

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
                    vue.reinitialiser(); // vide le formulaire (champs texte) + désélectionne la ligne dans la table
                    rafraichir(); // recharge et réaffiche la table avec toutes les données actuelles de la base
                }
        );
        vue.getBoutonExporter().addActionListener(
                e -> exporter()
        );
        // uniquement sur des clics à l'intérieur du tableau
        vue.getTable().getSelectionModel().addListSelectionListener(selectionDeLigne());
        rafraichir(); // affiche tout les donnees actuelles
    }

    //enCours se remplit dès la sélection de la ligne, pas au clic sur Modifier
    // Sélection dans la table -> chargement du formulaire
    /*
    *  Quand l'utilisateur clique sur une ligne stable et réelle du tableau,
    * on récupère le matricule affiché, on va chercher le vrai maître correspondant en base
    * (en s'assurant qu'il existe encore), on le mémorise pour une future modification,
    *  et on remplit le formulaire avec ses données
    */
    private ListSelectionListener selectionDeLigne() {
        return e -> {
            if (e.getValueIsAdjusting()) { // ignorer les déclenchements intermédiaires
                return;
            }
            // getSelectedRow() renvoie l'index de la ligne sélectionnée
            int ligne = vue.getTable().getSelectedRow();
            if (ligne < 0) {
                return;
            }
            // va chercher, dans le modèle de la table, la valeur affichée à la position ligne ligne,
            // colonne 0 qui correspond au matricule
            String matricule = (String) vue.getModeleTable().getValueAt(ligne, 0);
            try {
                Maitre maitre = dao.trouverObligatoire(matricule);
                enCours = maitre;
                vue.remplir(maitre);
            } catch (DaaraException ex) {
                afficherErreur(ex.getMessage()); // Si le maitre n'est pas trouvé on le lance
            }
        };
    }

    // Ajouter / Modifier
    /*
    * On récupère et nettoie le texte des 3 champs → on vérifie que matricule et nom sont bien remplis
    * → selon que enCours soit vide ou non, on crée un nouveau maître ou on modifie celui en cours
    * (sans jamais toucher au matricule) → on affiche un message de succès → on vide tout et
    *  on redevient prêt pour une nouvelle action → si une règle métier est violée, on affiche l'erreur proprement.
    */
    private void enregistrer() {
        // .trim() supprime les espaces inutiles au début et à la fin
        String matricule = vue.getChampMatricule().getText().trim(); //récupère le texte actuellement tapé dans le champ
        String nomComplet = vue.getChampNomComplet().getText().trim();
        String telephone = vue.getChampTelephone().getText().trim();

        if (!validerSaisies(matricule, nomComplet)) {
            return;
        }

        try {
            if (enCours == null) {
                //Creation
                Maitre nouveau = new Maitre(matricule, nomComplet, telephone);
                dao.inserer(nouveau);
                // popup
                JOptionPane.showMessageDialog(vue, "Maitre ajouté avec succés.",
                        "Succes", JOptionPane.INFORMATION_MESSAGE);
            } else {
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

    private boolean validerSaisies(String matricule, String nomComplet) {
        if (matricule.isEmpty() || nomComplet.isEmpty()) {
            afficherErreur("Le matricule et le nom complet sont obligatoires");
            return false;
        }
        return true;
    }

    // Supprimer
    /*
    * On vérifie qu'une ligne est sélectionnée → on demande confirmation à l'utilisateur
    * → si Oui, on tente la suppression → si ça réussit, message de succès et retour à l'état propre
    * → si le maître encadre encore des classes, SuppressionImpossibleException est levée et affichée proprement
    */
    private void supprimer() {
        int ligne = vue.getTable().getSelectedRow();
        if (ligne < 0) {
            afficherErreur("Sélectionnez d'abord un maître dans la table.");
            return;
        }
        String matricule = (String) vue.getModeleTable().getValueAt(ligne, 0);

        int confirmation = JOptionPane.showConfirmDialog(vue,
                "Confirmer la suppression du maitre" + matricule + "?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;  // on arrête tout, on ne supprime PAS
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
    private void rechercher() {
        String texte = vue.getChampRecherche().getText().trim();
        if (texte.isEmpty()) {
            rafraichir();
            return;
        }
        try {
            vue.afficher(dao.rechercherParNom(texte));
        } catch (DaaraException ex) {
            afficherErreur(ex.getMessage());
        }
    }
    /*
    * elle va chercher les données fraîches en base (dao.listerTous()),
    * puis transmet ce résultat à la vue pour qu'elle l'affiche (vue.afficher(...))
    * MaitreView ne doit jamais appeler MaitreDao directement
     */
    private void rafraichir() {
        vue.afficher(dao.listerTous());
    }

    // Export CSV
    /*
    * On demande à l'utilisateur où sauvegarder → on récupère tous les maîtres et
    * on les transforme en tableaux de texte → on écrit le fichier CSV → si l'écriture échoue (problème disque),
    * on l'affiche proprement, avec le bon type d'exception cette fois (IOException, pas DaaraException)
    */
    private void exporter() {
        JFileChooser chooser = new JFileChooser(); //  crée un composant Swing tout prêt — la fenêtre classique "Enregistrer sous..."
        chooser.setSelectedFile(new File("maitres.csv")); // pré-remplit le nom de fichier suggéré par défaut  peut etre changer par l'utilisateur
        int choix = chooser.showSaveDialog(vue); // ouvre la fenêtre et attend que l'utilisateur choisisse un emplacement et clique "Enregistrer" (ou "Annuler")
        if (choix != JFileChooser.APPROVE_OPTION) {
            return;
        }

        List<Maitre> maitres = dao.listerTous(); // va chercher tous les maîtres en base
        String[] entetes = {"matricule", "nomComplet", "telephone"}; //  les en-têtes de colonnes du fichier CSV
        List<String[]> lignes = new ArrayList<>();
        for (Maitre m : maitres) {
            lignes.add(new String[]{m.getMatricule(), m.getNomComplet(), m.getTelephone()}); // on ajoute dans la liste
        }

        try {
            /*
            * chooser.getSelectedFile() : récupère le fichier (nom + emplacement) que
            * l'utilisateur a choisi dans la fenêtre "Enregistrer sous..."
            * tout à l'heure — on le donne à CsvExporter.exporter(...) pour qu'il sache où écrire.
            */
            CsvExporter.exporter(chooser.getSelectedFile(), entetes, lignes);
            JOptionPane.showMessageDialog(vue, "Export terminé : " + chooser.getSelectedFile(),
                    "Succes", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) { // aux erreurs d'entrée/sortie
            afficherErreur("Echec de l'export CSV : " + ex.getMessage());
        }
    }

    // Utilitaire

    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog((Component) vue, message, "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }
}
