package sn.l2gl.girls.daara.model.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

// Entité JPA représentant une classe (ex: CI, CP, CE1...) dans le système Daara
@Entity
@Table(name = "classes")
@Getter          // Génère tous les getters (getCode, getLibelle, getNiveau, getMaitre)
@Setter          // Génère tous les setters (setCode, setLibelle, setNiveau, setMaitre)
@NoArgsConstructor // Génère le constructeur vide, requis par Hibernate
public class Classe {

    // Identifiant unique de la classe (clé primaire, ex: "CI-A")
    @Id
    private String code;

    // Nom affiché de la classe (ex: "CI groupe A")
    private String libelle;

    // Niveau scolaire, stocké en base sous forme de texte (String) plutôt que d'entier
    @Enumerated(EnumType.STRING)
    private Niveau niveau;

    // Maître responsable de la classe
    // optional = false : une classe doit obligatoirement avoir un maître assigné
    @ManyToOne(optional = false)
    @JoinColumn(name = "maitre_id")
    private Maitre maitre;
}