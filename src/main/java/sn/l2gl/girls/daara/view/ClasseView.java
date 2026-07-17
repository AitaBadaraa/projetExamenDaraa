package sn.l2gl.girls.daara.view;


import lombok.Getter;
import java.util.List;
import sn.l2gl.girls.daara.model.models.Classe;
import sn.l2gl.girls.daara.model.models.Maitre;
import sn.l2gl.girls.daara.model.models.Niveau;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
@Getter
public class ClasseView extends JPanel {

    private JTextField txtcode = new JTextField();
    private JTextField txtlibelle = new JTextField();
    //Permet de faire un select de Niveau
    private JComboBox<Niveau> cmbNiveau = new JComboBox<>(Niveau.values());

    private JComboBox<Maitre> cmbMaitre = new JComboBox<>();
    //Champ de recherche
    private JTextField txtrechercher = new JTextField();

    //Les Boutons
    private JButton btnChercher = new JButton("Chercher");
    private JButton btnEnregistrer = new JButton("Enregistrer");
    private JButton btnSupprimer = new JButton("Supprimer");
    private JButton btnExporter = new JButton("Exporter");
    private JButton btnToutAfficher = new JButton("Tout afficher");

    private JTable table = new JTable();
    private JScrollPane scroll = new JScrollPane(table);

    private DefaultTableModel model;

    //Constructeur
    public ClasseView() {
        setLayout(new BorderLayout());
        setBackground(Color.white);
        JPanel panelForm = new JPanel(new GridLayout(5, 2,10,10));

        panelForm.add(new JLabel("Code"));
        panelForm.add(txtcode);

        panelForm.add(new JLabel("Libellé"));
        panelForm.add(txtlibelle);

        panelForm.add(new JLabel("Niveau"));
        panelForm.add(cmbNiveau);

        panelForm.add(new JLabel("Maître"));
        panelForm.add(cmbMaitre);
        panelForm.add(new JLabel("Rechercher"));
        panelForm.add(txtrechercher);

        add(panelForm, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Code", "Libellé", "Niveau", "Maître"}, 0
        );
        table.setModel(model);
        add(scroll, BorderLayout.CENTER);

        //Zone bouton pour la visibilite
        JPanel panelButtons = new JPanel();
        panelButtons.add(btnChercher);
        panelButtons.add(btnEnregistrer);
        panelButtons.add(btnSupprimer);
        panelButtons.add(btnExporter);
        panelButtons.add(btnToutAfficher);
        add(panelButtons, BorderLayout.SOUTH);

    }
    //METHODE AFFICHER
    public void afficher(List<Classe> liste) {

        model.setRowCount(0);
        //Pour chaque classe de la liste
        for (Classe c : liste) {

            model.addRow(new Object[]{
                    c.getCode(),
                    c.getLibelle(),
                    c.getNiveau(),
                    (c.getMaitre() != null ? c.getMaitre().getNomComplet() : "")
            });
        }
    }

    //METHODE REMPLIR
    public void remplir(Classe c) {

        txtcode.setText(c.getCode());
        txtlibelle.setText(c.getLibelle());
        cmbNiveau.setSelectedItem(c.getNiveau());
        cmbMaitre.setSelectedItem(c.getMaitre());
    }

    //METHODE REINITIALISER
    public void reinitialiser() {

        txtcode.setText("");
        txtlibelle.setText("");
        txtrechercher.setText("");
        cmbNiveau.setSelectedIndex(0);
        // On ne réinitialise l'index que si la liste des maîtres n'est pas vide
        if (cmbMaitre.getItemCount() > 0) {
            cmbMaitre.setSelectedIndex(0);
        }
    }

    // CHARGER LISTE DES MAITRES
    public void setListeMaitres(List<Maitre> maitres) {

        cmbMaitre.removeAllItems();

        for (Maitre m : maitres) {
            cmbMaitre.addItem(m);
        }
    }
}
