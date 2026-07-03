package sn.l2gl.girls.daara.model.models;

import jakarta.persistence.*;

@Entity
@Table(name = "classes")
public class Classe {

    @Id
    private String code;

    private String libelle;

    @Enumerated(EnumType.STRING)
    private Niveau niveau;

    @ManyToOne(optional = false)
    @JoinColumn(name = "maitre_id")
    private Maitre maitre;

    public Classe() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public Niveau getNiveau() { return niveau; }
    public void setNiveau(Niveau niveau) { this.niveau = niveau; }

    public Maitre getMaitre() { return maitre; }
    public void setMaitre(Maitre maitre) { this.maitre = maitre; }
}