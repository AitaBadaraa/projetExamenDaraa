package sn.l2gl.girls.daara.exception;

public class ProgressionIntrouvableException extends DaaraException {
    public ProgressionIntrouvableException(Long id) {
        super("Aucune progression pour l'id : " + id);
    }
}