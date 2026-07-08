package sn.l2gl.girls.daara.exception;

public class ClasseIntrouvableException extends DaaraException {
    public ClasseIntrouvableException(String code) {
        super("Classe introuvable : " +  code);
    }
}