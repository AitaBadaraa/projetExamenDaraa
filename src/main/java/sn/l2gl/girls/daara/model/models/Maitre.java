package sn.l2gl.girls.daara.model.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "maitres")
public class Maitre {

    @Id
    private String matricule;

    private String nomComplet;
    private String telephone;

    public Maitre() {}

    public Maitre(String matricule, String nomComplet, String telephone) {
        this.matricule = matricule;
        this.nomComplet = nomComplet;
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return nomComplet;
    }
}