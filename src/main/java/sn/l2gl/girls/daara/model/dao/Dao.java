package sn.l2gl.girls.daara.model.dao;

import java.util.List;
import java.util.Optional;


public interface Dao <T, ID>{
    //Ajouter un objet dans la base
    T inserer(T entity);
    //Recherche unique
    Optional<T>  trouver(ID id);
    //Retourne toute la liste
    List<T> listerTous();
    Optional<T> modifier(T entity);
    boolean supprimer(ID id);

}

