package sn.l2gl.girls.daara.exception;

public class TalibeIntrouvableException extends DaaraException {
    public TalibeIntrouvableException(String matricule) {
        super("Aucun talibé pour le matricule : " + matricule);
    }
}