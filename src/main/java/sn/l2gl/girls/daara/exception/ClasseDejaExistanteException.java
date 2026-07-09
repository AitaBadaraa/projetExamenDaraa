package sn.l2gl.girls.daara.exception;

import sn.l2gl.girls.daara.exception.DaaraException;

/**
 * Levée quand on tente d'insérer une Classe dont le code (clé saisie
 * par l'utilisateur) existe déjà en base.
 */

public class ClasseDejaExistanteException extends DaaraException {

    public ClasseDejaExistanteException(String code) {
        super("Une classe existe déjà avec le code : " + code);
    }
}
