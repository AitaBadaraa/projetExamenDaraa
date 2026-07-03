package sn.l2gl.girls.daara.exception;

import sn.l2gl.girls.daara.exception.DaaraException;

public class ClasseIntrouvableException extends DaaraException{

    public ClasseIntrouvableException(String code) {
        super("Aucune classe pour le code : " + code);
    }
}
