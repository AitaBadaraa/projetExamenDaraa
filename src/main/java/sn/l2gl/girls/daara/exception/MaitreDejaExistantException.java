package sn.l2gl.girls.daara.exception;

public class MaitreDejaExistantException extends DaaraException {
    public MaitreDejaExistantException(String id) {
        super("Maitre deja existant : " + id);
    }
}