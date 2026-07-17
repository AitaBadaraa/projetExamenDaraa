package sn.l2gl.girls.daara.exception;

import sn.l2gl.girls.daara.exception.DaaraException;


public class ClasseDejaExistanteException extends DaaraException {

    public ClasseDejaExistanteException(String code) {

        super("Une classe existe déjà avec le code : " + code);
    }
}
