package sn.l2gl.girls.daara.model.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "talibes")
public class Talibe {

    @Id
    private String matricule;

    private String prenom;
    private String nom;
    private LocalDate dateNaissance;
    private String nomTuteur;
    private String telephoneTuteur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classe_code")
    private Classe classe;

    // Cascade : supprimer le talibé supprime aussi ses progressions
    @OneToMany(mappedBy = "talibe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Progression> progressions = new ArrayList<>();

    public Talibe() {}

    @Override
    public String toString() {
        return prenom + " " + nom + " (" + matricule + ")";
    }
}