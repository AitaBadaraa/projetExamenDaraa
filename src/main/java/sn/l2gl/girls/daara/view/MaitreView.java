package sn.l2gl.girls.daara.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import sn.l2gl.girls.daara.model.models.Maitre;
import lombok.Getter;

@Getter
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
    private final String[] colonnes = {"Matricule","Nom Complet", "Telephone"};
    private final DefaultTableModel modeleTable = new DefaultTableModel(colonnes,0){
        @Override
        public boolean isCellEditable(int row, int column){
            return false;
        }
    };
    private final JTable table = new JTable(modeleTable);

    public MaitreView(){
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(construireFormulaire(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(construireBarreActions(), BorderLayout.SOUTH);

    }

    private JPanel construireFormulaire(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Maitre"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
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

    private JPanel construireBarreActions(){
        JPanel panel = new JPanel(new GridLayout(1,6,8,0));
        panel.add(boutonAjouter);
        panel.add(boutonModifier);
        panel.add(boutonSupprimer);
        panel.add(boutonRechercher);
        panel.add(boutonToutAfficher);
        panel.add(boutonExporter);

        return panel;
    }

    /** Affiche la liste de maîtres passée dans la table (remplace le contenu actuel). */
    public void afficher(List<Maitre> maitres){
        modeleTable.setRowCount(0);
        for (Maitre m : maitres){
            modeleTable.addRow(new Object[]{m.getMatricule(), m.getNomComplet(), m.getTelephone()});
        }
    }

    /** Charge un maître dans le formulaire (ex : après sélection d'une ligne). */
    public void remplir(Maitre maitre){
        champMatricule.setText(maitre.getMatricule());
        champNomComplet.setText(maitre.getNomComplet());
        champTelephone.setText(maitre.getTelephone());
        champMatricule.setEditable(false); // la clé saisie n'est pas modifiable
    }

    /** Réinitialise le formulaire (retour en mode "création"). */
    public void reinitialiser(){
        champMatricule.setText("");
        champNomComplet.setText("");
        champTelephone.setText("");
        champMatricule.setEditable(true);
        table.clearSelection();
    }

}
