package sn.l2gl.girls.daara.model.models;

import jakarta.persistence.*;

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

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}