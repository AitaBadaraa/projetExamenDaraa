package sn.l2gl.girls.daara.model.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "talibes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Talibe {

    @Id
    @Column(length = 200, unique = true)
    private String matricule;

    private String prenom;
    private String nom;
    private LocalDate dateNaissance;
    private String nomTuteur;
    private String telephoneTuteur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classe_code")
    private Classe classe;
}