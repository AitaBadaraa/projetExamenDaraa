package sn.l2gl.girls.daara.view;

import javax.swing.*;
import java.awt.*;

public class AccueilView extends JPanel {

    // Palette de couleurs pour tout l'écran d'accueil
    private static final Color BLEU_FONCE = new Color(30, 58, 95);
    private static final Color BLEU_CLAIR = new Color(74, 111, 165);
    private static final Color GRIS_TEXTE = new Color(90, 90, 90);
    private static final Color FOND = new Color(245, 247, 250);

    public AccueilView() {
        setLayout(new GridBagLayout());
        setBackground(FOND);

        // Panneau central "carte" avec un fond blanc et des bords arrondis
        JPanel carte = new JPanel();
        carte.setLayout(new BoxLayout(carte, BoxLayout.Y_AXIS));
        carte.setBackground(Color.WHITE);
        carte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
                BorderFactory.createEmptyBorder(50, 60, 50, 60)
        ));

        // Petit bandeau décoratif au-dessus du titre
        JLabel icone = new JLabel("☪");
        icone.setFont(new Font("Serif", Font.PLAIN, 42));
        icone.setForeground(BLEU_CLAIR);
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Titre principal
        JLabel titre = new JLabel("Gestion de Daara");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titre.setForeground(BLEU_FONCE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sous-titre
        JLabel sousTitre = new JLabel("Système de gestion d'une école coranique");
        sousTitre.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sousTitre.setForeground(GRIS_TEXTE);
        sousTitre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Petite ligne de séparation
        JSeparator separateur = new JSeparator();
        separateur.setMaximumSize(new Dimension(200, 1));
        separateur.setForeground(new Color(220, 224, 230));
        separateur.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Texte d'instruction
        JLabel texte = new JLabel(
                "<html><center>Utilisez le menu <b>Affichage</b> ci-dessus<br>" +
                        "pour accéder aux différents modules :<br>" +
                        "Maîtres, Classes, Talibés et Progressions.</center></html>"
        );
        texte.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        texte.setForeground(GRIS_TEXTE);
        texte.setAlignmentX(Component.CENTER_ALIGNMENT);
        texte.setHorizontalAlignment(SwingConstants.CENTER);

        // Assemblage vertical avec des espacements entre chaque élément
        carte.add(icone);
        carte.add(Box.createRigidArea(new Dimension(0, 10)));
        carte.add(titre);
        carte.add(Box.createRigidArea(new Dimension(0, 6)));
        carte.add(sousTitre);
        carte.add(Box.createRigidArea(new Dimension(0, 20)));
        carte.add(separateur);
        carte.add(Box.createRigidArea(new Dimension(0, 20)));
        carte.add(texte);

        add(carte);
    }
}