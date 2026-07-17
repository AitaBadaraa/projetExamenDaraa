package sn.l2gl.girls.daara.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import sn.l2gl.girls.daara.model.models.Maitre;
import lombok.Getter;

@Getter

// Le MaitreView se contente d'exister et d'afficher les boutons — c'est MaitreController qui,
// dans son constructeur, viendra "brancher" chaque bouton à une action (addActionListener)
// Swing est la bibliothèque graphique de Java
public class MaitreView extends JPanel {
    private final JTextField champMatricule = new JTextField(15);
    private final JTextField champNomComplet = new JTextField(15);
    private final JTextField champTelephone = new JTextField(15);

    //Barre de recherche
    private final JTextField champRecherche = new JTextField(15);

    //Boutons
    private final JButton boutonAjouter = new JButton("Ajouter");
    private final JButton boutonModifier = new JButton("Modifier");
    private final JButton boutonSupprimer = new JButton("Supprimer");
    private final JButton boutonRechercher = new JButton("Rechercher");
    private final JButton boutonToutAfficher = new JButton("Tout afficher");
    private final JButton boutonExporter = new JButton("Exporter");

    //Table (lecture seule)
    private final String[] colonnes = {"Matricule", "Nom Complet", "Telephone"};
    // DefaultTableModel (le "cerveau" derrière, qui stocke les données)

    /* Par défaut, un JTable Swing permet à l'utilisateur de double-cliquer directement dans une case du tableau pour la modifier
    * — comme dans Excel. Mais dans votre projet, on a décidé une règle : toute modification doit obligatoirement
    *  passer par le formulaire (les champs champMatricule, champNomComplet...), jamais en tapant à la main dans une cellule du tableau.
    * Il faut donc désactiver ce comportement par défaut
    */

    private final DefaultTableModel modeleTable = new DefaultTableModel(colonnes, 0) {
        @Override
        // à chaque fois que l'utilisateur essaye de double cliquer Swing va poser la question s'il a le droit ou pas
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    // JTable (le tableau visuel qu'on voit à l'écran) se contente juste d'afficher
    // afficher automatiquement tout ce que modeleTable contient
    private final JTable table = new JTable(modeleTable);

    public MaitreView() {
        // setLayout organise les composants
        // BorderLayout découpe l'espace disponible en 5 zones fixes
        setLayout(new BorderLayout(10, 10));

        //crée une marge invisible
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(construireFormulaire(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(construireBarreActions(), BorderLayout.SOUTH);

    }

    // construireFormulaire() → GridBagLayout, positionnement précis case par case via le "post-it" GridBagConstraints
    private JPanel construireFormulaire() {
        // servira uniquement à contenir le formulaire
        // GridBagLayout place les composants sur une grille invisible
        JPanel panel = new JPanel(new GridBagLayout());

        // Le titre du formulaire
        panel.setBorder(BorderFactory.createTitledBorder("Maitre"));

        // il sert à décrire, avant chaque add(...), où et comment le prochain composant doit être placé dans la grille
        GridBagConstraints c = new GridBagConstraints();

        //un petit espace de 4 pixels autour de chaque composant individuel placé dans la grille
        c.insets = new Insets(4, 4, 4, 4);
        // dit à chaque composant de s'étirer horizontalement pour remplir toute la largeur de sa cellule de grille
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new javax.swing.JLabel("Matricule :"), c);

        c.gridx = 1;
        c.gridy = 0;
        panel.add(champMatricule, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new javax.swing.JLabel("Nom complet :"), c);

        c.gridx = 1;
        c.gridy = 1;
        panel.add(champNomComplet, c);

        c.gridx = 0;
        c.gridy = 2;
        panel.add(new javax.swing.JLabel("Téléphone :"), c);

        c.gridx = 1;
        c.gridy = 2;
        panel.add(champTelephone, c);

        c.gridx = 2;
        c.gridy = 0;
        panel.add(new javax.swing.JLabel("Recherche (nom) :"), c);

        c.gridx = 3;
        c.gridy = 0;
        panel.add(champRecherche, c);

        return panel;
    }

    // construireBarreActions() → GridLayout simple, position automatique selon l'ordre d'ajout
    private JPanel construireBarreActions() {
        // organise tout en 1 ligne, 6 colonnes, chaque case de même taille,
        // avec 8 pixels d'espace horizontal entre elles et 0 pixel vertical
        JPanel panel = new JPanel(new GridLayout(1, 6, 8, 0));
        panel.add(boutonAjouter);
        panel.add(boutonModifier);
        panel.add(boutonSupprimer);
        panel.add(boutonRechercher);
        panel.add(boutonToutAfficher);
        panel.add(boutonExporter);

        return panel;
    }

    /**
     * rafraîchit la table après n'importe quelle opération
     * Elle ne sait rien sur la base de données. Elle prend juste une liste déjà toute prête,
     * MaitreView ne doit jamais appeler MaitreDao directement
     */
    public void afficher(List<Maitre> maitres) {
        modeleTable.setRowCount(0); // remets tout à 0
        for (Maitre m : maitres) {
            // ajoute une ligne au tableau, avec 3 valeurs correspondant
            // Elle attend précisément un Object[]
            modeleTable.addRow(new Object[]{m.getMatricule(), m.getNomComplet(), m.getTelephone()});
        }
    }

    /**
     * charge un maître existant dans le formulaire pour le modifier (verrouille le matricule)
     */
    public void remplir(Maitre maitre) {
        champMatricule.setText(maitre.getMatricule());
        champNomComplet.setText(maitre.getNomComplet());
        champTelephone.setText(maitre.getTelephone());
        champMatricule.setEditable(false); // la clé saisie n'est pas modifiable
    }

    /**
     * vide tout, redonne le droit d'éditer le matricule, prêt pour une nouvelle action
     * vide le formulaire (champs texte)
     */
    public void reinitialiser() {
        champMatricule.setText("");
        champNomComplet.setText("");
        champTelephone.setText("");
        champMatricule.setEditable(true);
        // Désélectionne la ligne actuellement surlignée dans la table
        table.clearSelection();
    }

}
