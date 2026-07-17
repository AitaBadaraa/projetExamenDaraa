package sn.l2gl.girls.daara.model.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

@Entity
@Table(name = "classes")
public class Classe {

    @Id //cle primaire
    private String code;

    private String libelle;

    @Enumerated(EnumType.STRING)
    private Niveau niveau;

    @ManyToOne(optional = false)//indique une relation
    @JoinColumn(name = "maitre_id")//nom de la colonne de liaison
    private Maitre maitre;

    public Classe() {}

}