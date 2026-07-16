package sn.l2gl.girls.daara.view;

import sn.l2gl.girls.daara.controller.ClasseController;
import sn.l2gl.girls.daara.controller.MaitreController;
import sn.l2gl.girls.daara.controller.ProgressionController;
import sn.l2gl.girls.daara.controller.TalibeController;

import javax.swing.*;
import java.awt.*;

public class AppDaara extends JFrame {

    private final CardLayout cartes = new CardLayout();
    private final JPanel conteneur = new JPanel(cartes);

    public AppDaara() {
        super("Gestion de la Daara");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);

        MaitreView maitreView = new MaitreView();
        new MaitreController(maitreView);

        ClasseView classeView = new ClasseView();
        new ClasseController(classeView);

        TalibeView talibeView = new TalibeView();
        new TalibeController(talibeView);

        ProgressionView progressionView = new ProgressionView();
        new ProgressionController(progressionView);

        conteneur.add(maitreView, "MAITRE");
        conteneur.add(classeView, "CLASSE");
        conteneur.add(talibeView, "TALIBE");
        conteneur.add(progressionView, "PROGRESSION");

        setContentPane(conteneur);
        setJMenuBar(construireMenu());
        cartes.show(conteneur, "MAITRE");
    }

    private JMenuBar construireMenu() {
        JMenuBar barre = new JMenuBar();
        JMenu menuAffichage = new JMenu("Affichage");

        JMenuItem itemMaitre = new JMenuItem("Maîtres");
        itemMaitre.addActionListener(e -> cartes.show(conteneur, "MAITRE"));

        JMenuItem itemClasse = new JMenuItem("Classes");
        itemClasse.addActionListener(e -> cartes.show(conteneur, "CLASSE"));

        JMenuItem itemTalibe = new JMenuItem("Talibés");
        itemTalibe.addActionListener(e -> cartes.show(conteneur, "TALIBE"));

        JMenuItem itemProgression = new JMenuItem("Progressions");
        itemProgression.addActionListener(e -> cartes.show(conteneur, "PROGRESSION"));

        menuAffichage.add(itemMaitre);
        menuAffichage.add(itemClasse);
        menuAffichage.add(itemTalibe);
        menuAffichage.add(itemProgression);
        barre.add(menuAffichage);
        return barre;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppDaara().setVisible(true));
    }
}