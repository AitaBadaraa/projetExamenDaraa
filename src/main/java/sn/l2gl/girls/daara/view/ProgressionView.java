package sn.l2gl.girls.daara.view;

import lombok.Getter;
import sn.l2gl.girls.daara.model.models.Progression;
import sn.l2gl.girls.daara.model.models.Talibe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Getter
public class ProgressionView extends JPanel {

    private final JComboBox<Talibe> comboTalibe = new JComboBox<>();
    private final JTextField champSourate = new JTextField(12);
    private final JTextField champNombreVersets = new JTextField(6);
    private final JTextField champDateEvaluation = new JTextField(10); // yyyy-MM-dd
    private final JTextField champAppreciation = new JTextField(15);

    private final JComboBox<Talibe> comboFiltreTalibe = new JComboBox<>();

    private final JButton boutonAjouter = new JButton("Ajouter");
    private final JButton boutonModifier = new JButton("Modifier");
    private final JButton boutonSupprimer = new JButton("Supprimer");
    private final JButton boutonFiltrer = new JButton("Filtrer par talibé");
    private final JButton boutonToutAfficher = new JButton("Tout afficher");
    private final JButton boutonExporter = new JButton("Exporter");

    private final String[] colonnes = {"Id", "Talibé", "Sourate", "Versets", "Date éval.", "Appréciation"};
    private final DefaultTableModel modeleTable = new DefaultTableModel(colonnes, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(modeleTable);

    public ProgressionView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(construireFormulaire(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(construireBarreActions(), BorderLayout.SOUTH);
    }

    private JPanel construireFormulaire() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Progression"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Talibé :", "Sourate :", "Nombre de versets :", "Date éval. (AAAA-MM-JJ) :", "Appréciation :"};
        JComponent[] champs = {comboTalibe, champSourate, champNombreVersets, champDateEvaluation, champAppreciation};

        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0; c.gridy = i;
            panel.add(new JLabel(labels[i]), c);
            c.gridx = 1; c.gridy = i;
            panel.add(champs[i], c);
        }

        c.gridx = 2; c.gridy = 0;
        panel.add(new JLabel("Filtrer par talibé :"), c);
        c.gridx = 3; c.gridy = 0;
        panel.add(comboFiltreTalibe, c);

        return panel;
    }

    private JPanel construireBarreActions() {
        JPanel panel = new JPanel(new GridLayout(1, 6, 8, 0));
        panel.add(boutonAjouter);
        panel.add(boutonModifier);
        panel.add(boutonSupprimer);
        panel.add(boutonFiltrer);
        panel.add(boutonToutAfficher);
        panel.add(boutonExporter);
        return panel;
    }

    public void afficher(List<Progression> progressions) {
        modeleTable.setRowCount(0);
        for (Progression p : progressions) {
            modeleTable.addRow(new Object[]{
                    p.getId(),
                    p.getTalibe() != null ? p.getTalibe().getMatricule() : "",
                    p.getSourate(), p.getNombreVersets(), p.getDateEvaluation(), p.getAppreciation()
            });
        }
    }

    public void remplir(Progression p) {
        comboTalibe.setSelectedItem(p.getTalibe());
        champSourate.setText(p.getSourate());
        champNombreVersets.setText(String.valueOf(p.getNombreVersets()));
        champDateEvaluation.setText(p.getDateEvaluation() != null ? p.getDateEvaluation().toString() : "");
        champAppreciation.setText(p.getAppreciation());
    }

    public void reinitialiser() {
        champSourate.setText("");
        champNombreVersets.setText("");
        champDateEvaluation.setText("");
        champAppreciation.setText("");
        if (comboTalibe.getItemCount() > 0) comboTalibe.setSelectedIndex(0);
        table.clearSelection();
    }

    public void setListeTalibes(List<Talibe> talibes) {
        comboTalibe.removeAllItems();
        comboFiltreTalibe.removeAllItems();
        for (Talibe t : talibes) {
            comboTalibe.addItem(t);
            comboFiltreTalibe.addItem(t);
        }
    }
}