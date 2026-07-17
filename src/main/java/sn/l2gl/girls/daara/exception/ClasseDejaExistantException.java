package sn.l2gl.girls.daara.exception;

public class ClasseDejaExistantException extends DaaraException {
    public ClasseDejaExistantException(String code) {
        super("Classe deja existant : " + code);
    }
}