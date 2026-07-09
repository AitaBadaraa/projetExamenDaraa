package sn.l2gl.girls.daara.model.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "progressions")
public class Progression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Talibe talibe;

    private String sourate;
    private int nombreVersets;
    private LocalDate dateEvaluation;
    private String appreciation;

    public Progression() {}

    // getters setters
}