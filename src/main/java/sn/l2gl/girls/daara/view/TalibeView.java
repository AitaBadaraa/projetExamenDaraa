package sn.l2gl.girls.daara.view;

import lombok.Getter;
import sn.l2gl.girls.daara.model.models.Classe;
import sn.l2gl.girls.daara.model.models.Talibe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Getter
public class TalibeView extends JPanel {

    private final JTextField champMatricule = new JTextField(12);
    private final JTextField champPrenom = new JTextField(12);
    private final JTextField champNom = new JTextField(12);
    private final JTextField champDateNaissance = new JTextField(10); // format yyyy-MM-dd
    private final JTextField champNomTuteur = new JTextField(12);
    private final JTextField champTelephoneTuteur = new JTextField(12);
    private final JComboBox<Classe> comboClasse = new JComboBox<>();

    private final JTextField champRecherche = new JTextField(12);

    private final JButton boutonAjouter = new JButton("Ajouter");
    private final JButton boutonModifier = new JButton("Modifier");
    private final JButton boutonSupprimer = new JButton("Supprimer");
    private final JButton boutonRechercher = new JButton("Rechercher");
    private final JButton boutonToutAfficher = new JButton("Tout afficher");
    private final JButton boutonExporter = new JButton("Exporter");

    private final String[] colonnes = {"Matricule", "Prénom", "Nom", "Date naissance", "Classe"};
    private final DefaultTableModel modeleTable = new DefaultTableModel(colonnes, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(modeleTable);

    public TalibeView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(construireFormulaire(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(construireBarreActions(), BorderLayout.SOUTH);
    }

    private JPanel construireFormulaire() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Talibé"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Matricule :", "Prénom :", "Nom :", "Date naissance (AAAA-MM-JJ) :",
                "Nom tuteur :", "Téléphone tuteur :", "Classe :"};
        JComponent[] champs = {champMatricule, champPrenom, champNom, champDateNaissance,
                champNomTuteur, champTelephoneTuteur, comboClasse};

        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0; c.gridy = i;
            panel.add(new JLabel(labels[i]), c);
            c.gridx = 1; c.gridy = i;
            panel.add(champs[i], c);
        }

        c.gridx = 2; c.gridy = 0;
        panel.add(new JLabel("Recherche (nom) :"), c);
        c.gridx = 3; c.gridy = 0;
        panel.add(champRecherche, c);

        return panel;
    }

    private JPanel construireBarreActions() {
        JPanel panel = new JPanel(new GridLayout(1, 6, 8, 0));
        panel.add(boutonAjouter);
        panel.add(boutonModifier);
        panel.add(boutonSupprimer);
        panel.add(boutonRechercher);
        panel.add(boutonToutAfficher);
        panel.add(boutonExporter);
        return panel;
    }

    public void afficher(List<Talibe> talibes) {
        modeleTable.setRowCount(0);
        for (Talibe t : talibes) {
            modeleTable.addRow(new Object[]{
                    t.getMatricule(), t.getPrenom(), t.getNom(),
                    t.getDateNaissance(),
                    t.getClasse() != null ? t.getClasse().getLibelle() : ""
            });
        }
    }

    public void remplir(Talibe t) {
        champMatricule.setText(t.getMatricule());
        champPrenom.setText(t.getPrenom());
        champNom.setText(t.getNom());
        champDateNaissance.setText(t.getDateNaissance() != null ? t.getDateNaissance().toString() : "");
        champNomTuteur.setText(t.getNomTuteur());
        champTelephoneTuteur.setText(t.getTelephoneTuteur());
        comboClasse.setSelectedItem(t.getClasse());
        champMatricule.setEditable(false);
    }

    public void reinitialiser() {
        champMatricule.setText("");
        champPrenom.setText("");
        champNom.setText("");
        champDateNaissance.setText("");
        champNomTuteur.setText("");
        champTelephoneTuteur.setText("");
        if (comboClasse.getItemCount() > 0) comboClasse.setSelectedIndex(0);
        champMatricule.setEditable(true);
        table.clearSelection();
    }

    public void setListeClasses(List<Classe> classes) {
        comboClasse.removeAllItems();
        for (Classe c : classes) comboClasse.addItem(c);
    }
}