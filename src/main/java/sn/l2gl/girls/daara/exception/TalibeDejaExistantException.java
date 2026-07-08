package sn.l2gl.girls.daara.exception;

public class TalibeDejaExistantException extends DaaraException {
    public TalibeDejaExistantException(String matricule) {
        super("Un talibé existe déjà avec le matricule : " + matricule);
    }
}