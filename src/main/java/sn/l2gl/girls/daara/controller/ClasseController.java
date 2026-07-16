package sn.l2gl.girls.daara.controller;

import sn.l2gl.girls.daara.exception.ClasseDejaExistanteException;
import sn.l2gl.girls.daara.model.dao.ClasseDao;
import sn.l2gl.girls.daara.model.dao.MaitreDao;
import sn.l2gl.girls.daara.model.models.Classe;
import sn.l2gl.girls.daara.model.models.Maitre;
import sn.l2gl.girls.daara.model.models.Niveau;
import sn.l2gl.girls.daara.util.CsvExporter;
import sn.l2gl.girls.daara.view.ClasseView;
import sn.l2gl.girls.daara.exception.ClasseIntrouvableException;
import sn.l2gl.girls.daara.exception.SuppressionImpossibleException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.util.Optional;

public class ClasseController {

    // Permet de communiquer avec la base de données (DAO)
    private final ClasseDao dao = new ClasseDao();

    // DAO nécessaire pour charger la liste des maîtres dans la combobox
    private final MaitreDao maitreDao = new MaitreDao();

    // Permet de communiquer avec l'interface graphique (View)
    private final ClasseView vue;

    // Garde en mémoire la classe actuellement sélectionnée
    // null = on est en train d'AJOUTER une nouvelle classe
    // pas null = on est en train de MODIFIER une classe existante
    private Classe enCours;


    // Constructeur :
    public ClasseController(ClasseView vue) {

        this.vue = vue;

        // On va chercher tous les maîtres en base pour remplir le JComboBox de la vue
        vue.setListeMaitres(maitreDao.listerTous());

        // On "branche" chaque bouton de la vue à une méthode du contrôleur
        // Quand l'utilisateur clique, la méthode correspondante s'exécute
        vue.getBtnChercher().addActionListener(e -> rechercher());
        vue.getBtnEnregistrer().addActionListener(e -> enregistrer());
        vue.getBtnSupprimer().addActionListener(e -> supprimer());

        // Le bouton "Tout afficher" vide le champ de recherche ET réaffiche toute la liste
        vue.getBtnToutAfficher().addActionListener(e -> {
            vue.getTxtrechercher().setText("");
            lister();
        });

        vue.getBtnExporter().addActionListener(e -> exporter());

        // Quand l'utilisateur clique sur une ligne du tableau, on charge la classe dans le formulaire
        vue.getTable().getSelectionModel().addListSelectionListener(e -> selectionnerClasse());

        // Au démarrage, on affiche directement toutes les classes existantes
        lister();
    }



    // appelée quand l'utilisateur clique sur une ligne du tableau

    private void selectionnerClasse() {

        // On récupère le numéro de la ligne cliquée
        int ligne = vue.getTable().getSelectedRow();

        // Si aucune ligne n'est sélectionnée , on ne fait rien
        if (ligne == -1) {
            return;
        }

        // On récupère le code de la classe
        String code = vue.getTable().getValueAt(ligne, 0).toString();

        // On va chercher la classe correspondante dans la base via le DAO
        Optional<Classe> resultat = dao.trouver(code);

        // Si la classe existe bien
        if (resultat.isPresent()) {

            // On la garde en mémoire (mode "modification" activé)
            enCours = resultat.get();

            // On remplit le formulaire avec ses infos
            vue.remplir(enCours);

            //  on interdit la modification de code
            vue.getTxtcode().setEditable(false);
        }
    }



    // METHODE exporter la liste des classes en fichier CSV

    private void exporter() {

        // Ouvre une fenêtre pour choisir où enregistrer le fichier
        JFileChooser choix = new JFileChooser();
        int resultat = choix.showSaveDialog(vue);

        // Si l'utilisateur a bien choisi un emplacement
        if (resultat == JFileChooser.APPROVE_OPTION) {

            try {
                // Récupère le fichier choisi par l'utilisateur
                File fichier = choix.getSelectedFile();

                // Récupère toutes les classes depuis la base
                List<Classe> classes = dao.listerTous();

                // Ligne d'en-tête du CSV (noms des colonnes)
                String[] entetes = {"Code", "Libelle", "Niveau", "Maitre"};

                // Liste des lignes de données du CSV
                List<String[]> lignes = new ArrayList<>();

                // Pour chaque classe, on crée une ligne de texte
                for (Classe classe : classes) {
                    lignes.add(new String[]{
                            classe.getCode(),
                            classe.getLibelle(),
                            classe.getNiveau().toString(),
                            classe.getMaitre() != null ? classe.getMaitre().getNomComplet() : ""
                    });
                }


                CsvExporter.exporter(fichier, entetes, lignes);


                JOptionPane.showMessageDialog(vue, "Export CSV effectué avec succès");

            } catch (IOException e) {

                JOptionPane.showMessageDialog(vue, "Erreur lors de l'export : " + e.getMessage());
            }
        }
    }



    //  récupère toutes les classes et les affiche dans le tableau

    private void lister() {
        vue.afficher(dao.listerTous());
    }



    // supprimer la classe actuellement sélectionnée

    private void supprimer() {

        try {

            // Si aucune classe n'est sélectionnée, on prévient l'utilisateur et on arrête
            if (enCours == null) {
                JOptionPane.showMessageDialog(vue, "Veuillez sélectionner une classe");
                return;
            }

            //  on demande au DAO de supprimer la classe en base
            dao.supprimer(enCours.getCode());

            JOptionPane.showMessageDialog(vue, "Suppression avec succès");

            //  vider le formulaire
            vue.reinitialiser();

            // On réactive le champ code il était bloqué pendant la sélection
            vue.getTxtcode().setEditable(true);

            //  reaffiche la liste
            lister();

            enCours = null;

        } catch (ClasseIntrouvableException e) {

            JOptionPane.showMessageDialog(vue, e.getMessage());

        } catch (SuppressionImpossibleException e) {

            JOptionPane.showMessageDialog(vue, e.getMessage());
        }
    }


    // ajouter une nouvelle classe OU modifier la classe sélectionnee

    private void enregistrer() {

        try {

           //recupere les donne saisie pat l'utilisateur
            String code = vue.getTxtcode().getText();
            String libelle = vue.getTxtlibelle().getText();


            if (code.isEmpty() || libelle.isEmpty()) {
                JOptionPane.showMessageDialog(vue, "Veuillez remplir tous les champs");
                return;
            }

            // On recupere les valeurs sélectionnees dans les comboboxes
            Niveau niveau = (Niveau) vue.getCmbNiveau().getSelectedItem();
            Maitre maitre = (Maitre) vue.getCmbMaitre().getSelectedItem();

            // verifier si unmaitre est selectionne
            if (maitre == null) {
                JOptionPane.showMessageDialog(vue, "Veuillez sélectionner un maître");
                return;
            }

            // on est en mode ajout
            if (enCours == null) {


                Classe classe = new Classe(code, libelle, niveau, maitre);

                // enregistrement par le dao dans la base base
                dao.inserer(classe);

                JOptionPane.showMessageDialog(vue, "Classe ajoutée avec succès");

            } else {
                // on est en mode modification
                enCours.setLibelle(libelle);
                enCours.setNiveau(niveau);
                enCours.setMaitre(maitre);

                // sauvegarde des changements par dao
                dao.modifier(enCours);

                JOptionPane.showMessageDialog(vue, "Classe modifiée avec succès");
            }


            vue.reinitialiser();

            // On reactive le champ code pour la prochaine saisie
            vue.getTxtcode().setEditable(true);


            lister();


            enCours = null;

        } catch (ClasseDejaExistanteException e) {

            JOptionPane.showMessageDialog(vue, e.getMessage());

        } catch (ClasseIntrouvableException e) {

            JOptionPane.showMessageDialog(vue, e.getMessage());
        }
    }


    //  rechercher des classes par leur libellé

    private void rechercher() {

        // On récupère le texte tape dans le champ de recherche
        String texte = vue.getTxtrechercher().getText();

        // Le DAO cherche les classes dont le libellé contient ce texte
        // puis on affiche le résultat dans le tableau
        vue.afficher(dao.rechercherParLibelle(texte));
    }
}