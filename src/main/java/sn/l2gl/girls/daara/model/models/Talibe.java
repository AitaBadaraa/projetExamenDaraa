package sn.l2gl.girls.daara.model.models;


import jakarta.persistence.*;
import java.time.LocalDate;

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


    public Talibe() {}

    // getters setters
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getNomTuteur() { return nomTuteur; }
    public void setNomTuteur(String nomTuteur) { this.nomTuteur = nomTuteur; }

    public String getTelephoneTuteur() { return telephoneTuteur; }
    public void setTelephoneTuteur(String telephoneTuteur) { this.telephoneTuteur = telephoneTuteur; }

    public Classe getClasse() { return classe; }
    public void setClasse(Classe classe) { this.classe = classe; }

}