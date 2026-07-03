package sn.l2gl.girls.daara.exception;

/**
 * STUB PARTAGÉ — normalement fourni une seule fois pour tout le groupe
 * (peu importe qui la crée en premier, elle ne doit exister qu'UNE fois
 * dans le projet final ; vérifiez avec vos coéquipiers avant de fusionner).
 * Exception de base de toute la hiérarchie métier de la Daara.
 * Toutes les exceptions métier (Classe, Maitre, Talibe, Progression...)
 * doivent en hériter.
 */

public class DaaraException extends RuntimeException {

    public DaaraException(String message) {
        super(message);
    }

    public DaaraException(String message, Throwable cause) {
        super(message, cause);
    }
}
