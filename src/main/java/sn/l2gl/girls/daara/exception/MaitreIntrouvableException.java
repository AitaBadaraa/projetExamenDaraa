package sn.l2gl.girls.daara.exception;

public class MaitreIntrouvableException extends DaaraException {
    public MaitreIntrouvableException(String id) {
        super("Maitre introuvable : " + id);
    }
}