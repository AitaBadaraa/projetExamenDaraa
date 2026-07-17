# Daara

> Application de bureau Java pour la gestion d'une école coranique (Daara) —
> maîtres, classes, talibés et progressions dans la mémorisation du Coran.

---

## 📋 Table des matières

- [Contexte](#contexte)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [Structure du projet](#structure-du-projet)
- [Modèle de données](#modèle-de-données)
- [Prérequis](#prérequis)
- [Installation et configuration](#installation-et-configuration)
- [Lancement de l'application](#lancement-de-lapplication)
- [Fonctionnalités](#fonctionnalités)
- [Gestion des exceptions](#gestion-des-exceptions)
- [Export CSV](#export-csv)
- [Équipe et répartition](#équipe-et-répartition)
- [Conventions Git](#conventions-git)

---

## Contexte

Ce projet est réalisé dans le cadre du cours de **Génie Logiciel — Licence 2** à l'**ISI (Institut Supérieur d'Informatique)** de Dakar.

Une daara (école coranique) souhaite informatiser sa gestion. L'application permet de gérer :
- les **maîtres** (serigne) encadrant les classes ;
- les **classes** (halqa) regroupant des talibés ;
- les **talibés** et leurs informations personnelles ;
- la **progression** de chaque talibé dans la mémorisation du Coran.

---

## Technologies

| Couche | Technologie |
|---|---|
| Langage | Java 21 |
| Interface graphique | Java Swing |
| Persistance | Hibernate 6 / Jakarta Persistence (JPA) |
| Build | Maven |
| Base de données | MySQL (ou PostgreSQL / Oracle) |
| Utilitaires | Lombok |

---

## Architecture

Le projet suit une **architecture MVC stricte**, calquée sur le modèle de référence :
[github.com/fayeyoussou/cours_java → java_swing/.../hibernate](https://github.com/fayeyoussou/cours_java)

```
Modèle (model/)
  ├── models/   → Entités JPA annotées. Aucune logique d'interface.
  └── dao/      → Accès aux données via Hibernate. Requêtes HQL ici uniquement.

Contrôleur (controller/)
  → Reçoit la vue, branche les écouteurs, valide les saisies, appelle les DAO,
    capture les exceptions, rafraîchit la vue.

Vue (view/)
  → Composants Swing uniquement. Aucun accès base de données. Aucune logique métier.

Exceptions (exception/)
  → Hiérarchie métier propre à chaque entité.

Utilitaires (util/)
  → HibernateUtil (SessionFactory singleton) + CsvExporter.
```

**Règle d'or :** la vue ne connaît pas les DAO, les controllers ne contiennent aucun HQL.

---

## Structure du projet

```
sn.l2gl.<prenom>.daara
├── model
│   ├── models
│   │   ├── Maitre.java
│   │   ├── Classe.java
│   │   ├── Talibe.java
│   │   ├── Progression.java
│   │   └── Niveau.java               (enum : DEBUTANT, INTERMEDIAIRE, AVANCE)
│   └── dao
│       ├── Dao.java                  (interface générique Dao<T, ID>)
│       ├── MaitreDao.java
│       ├── ClasseDao.java
│       ├── TalibeDao.java
│       └── ProgressionDao.java
├── controller
│   ├── MaitreController.java
│   ├── ClasseController.java
│   ├── TalibeController.java
│   └── ProgressionController.java
├── view
│   ├── MaitreView.java
│   ├── ClasseView.java
│   ├── TalibeView.java
│   ├── ProgressionView.java
│   └── AppDaara.java                 (JFrame principal + menu de navigation)
├── exception
│   ├── DaaraException.java           (racine — extends RuntimeException)
│   ├── MaitreIntrouvableException.java
│   ├── MaitreDejaExistantException.java
│   ├── ClasseIntrouvableException.java
│   ├── ClasseDejaExistanteException.java
│   ├── TalibeIntrouvableException.java
│   ├── TalibeDejaExistantException.java
│   ├── ProgressionIntrouvableException.java
│   ├── ProgressionInvalideException.java
│   └── SuppressionImpossibleException.java
└── util
    ├── HibernateUtil.java
    └── CsvExporter.java
```

---

## Modèle de données

Les quatre entités forment une chaîne de relations :

```
Maitre  ←── (1) ───  Classe  ←── (1) ───  Talibe  ←── (1) ───  Progression
```

| Entité | Clé primaire | Type de clé | Table |
|---|---|---|---|
| `Maitre` | `matricule` | String saisie par l'utilisateur | `maitres` |
| `Classe` | `code` | String saisie par l'utilisateur | `classes` |
| `Talibe` | `matricule` | String saisie par l'utilisateur | `talibes` |
| `Progression` | `id` | Long auto-générée (`@GeneratedValue`) | `progressions` |

### Relations Hibernate

```java
// Classe → Maitre
@ManyToOne(optional = false)
@JoinColumn(name = "maitre_matricule")
private Maitre maitre;

// Talibe → Classe
@ManyToOne(optional = false)
@JoinColumn(name = "classe_code")
private Classe classe;

// Progression → Talibe
@ManyToOne(optional = false)
@JoinColumn(name = "talibe_matricule")
private Talibe talibe;
```

> **Note :** la suppression d'un talibé entraîne la suppression en cascade de toutes ses progressions (`CascadeType.ALL`).

---

## Prérequis

- **Java 21** ou supérieur installé (`java -version`)
- **Maven 3.8+** installé (`mvn -version`)
- Un SGBD au choix : **MySQL**, **PostgreSQL** ou **Oracle**
- Une base de données nommée `daara` créée au préalable

```sql
-- MySQL
CREATE DATABASE daara CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- PostgreSQL
CREATE DATABASE daara ENCODING 'UTF8';
```

---

## Installation et configuration

### 1. Cloner le dépôt

```bash
git clone https://github.com/<votre-repo>/daara-manager.git
cd daara-manager
```

### 2. Configurer la base de données

Éditer le fichier `src/main/resources/hibernate.cfg.xml` selon votre SGBD :

**MySQL**
```xml
<property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
<property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/daara</property>
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">VOTRE_MOT_DE_PASSE</property>
```

**PostgreSQL**
```xml
<property name="hibernate.dialect">org.hibernate.dialect.PostgreSQLDialect</property>
<property name="hibernate.connection.driver_class">org.postgresql.Driver</property>
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/daara</property>
<property name="hibernate.connection.username">postgres</property>
<property name="hibernate.connection.password">VOTRE_MOT_DE_PASSE</property>
```

> ⚠️ **Ne jamais commiter `hibernate.cfg.xml` avec vos identifiants.** Ce fichier est dans `.gitignore`.

### 3. Compiler le projet

```bash
mvn clean package
```

Les tables sont créées automatiquement au premier lancement grâce à `hbm2ddl.auto=update`.

---

## Lancement de l'application

```bash
mvn exec:java -Dexec.mainClass="sn.l2gl.<prenom>.daara.view.AppDaara"
```

Ou depuis votre IDE (IntelliJ IDEA / Eclipse) : exécuter la classe `AppDaara.java`.

---

## Fonctionnalités

Chaque entité dispose d'un cycle CRUD complet accessible depuis le menu **Affichage** :

| Action | Description |
|---|---|
| **Lister** | Affiche tous les enregistrements dans une JTable |
| **Ajouter** | Saisie du formulaire + validation + insertion en base |
| **Modifier** | Sélection d'une ligne → chargement dans le formulaire → enregistrement |
| **Supprimer** | Confirmation + suppression (avec contrôle des relations) |
| **Rechercher** | Filtrage de la table par nom, libellé ou matricule |
| **Tout afficher** | Réinitialise le filtre de recherche |
| **Exporter** | Génère un fichier CSV de la liste affichée |

### Règles métier

- Un **maître** ne peut pas être supprimé s'il encadre au moins une classe.
- Une **classe** ne peut pas être supprimée si elle contient au moins un talibé.
- Un **talibé** doit obligatoirement être rattaché à une classe existante (JComboBox).
- La suppression d'un talibé supprime automatiquement toutes ses progressions (cascade).
- Une **progression** requiert un nombre de versets ≥ 0 et une sourate non vide.

---

## Gestion des exceptions

Toutes les exceptions héritent de `DaaraException` (qui étend `RuntimeException`) :

```
DaaraException
 ├── MaitreIntrouvableException        → recherche par matricule sans résultat
 ├── MaitreDejaExistantException       → insertion d'un matricule déjà existant
 ├── ClasseIntrouvableException
 ├── ClasseDejaExistanteException
 ├── TalibeIntrouvableException
 ├── TalibeDejaExistantException
 ├── ProgressionIntrouvableException
 ├── ProgressionInvalideException      → versets < 0 ou sourate vide
 └── SuppressionImpossibleException    → violation de clé étrangère (relation active)
```

**Principe :** les DAO lèvent les exceptions, les controllers les capturent et affichent un message clair via `JOptionPane`. Les vues n'attrapent jamais d'exception.

---

## Export CSV

Chaque page comporte un bouton **Exporter** qui :

1. Ouvre un `JFileChooser` pour choisir l'emplacement du fichier ;
2. Écrit une ligne d'en-tête (noms des colonnes) ;
3. Écrit une ligne par enregistrement, valeurs séparées par des virgules ;
4. Sauvegarde en **UTF-8** ;
5. Affiche un message de confirmation.

Exemple de fichier généré pour les talibés :

```csv
matricule,prenom,nom,dateNaissance,nomTuteur,telephoneTuteur,classe
T0001,Modou,Fall,2012-05-10,Ibrahima Fall,77 123 45 67,CL-DEB
T0002,Awa,Sow,2011-09-02,Mariama Sow,76 987 65 43,CL-INT
```

---

## Équipe et répartition

| Membre | Rôle principal | Packages |
|---|---|---|
| **Membre 1** | Fondations & entité Maître | `model.models`, `model.dao` (interface + MaitreDao), `exception`, `util.HibernateUtil` |
| **Membre 2** | Entité Classe & UI Maître | `model.dao.ClasseDao`, `controller.MaitreController`, `view.MaitreView` |
| **Membre 3** | Entité Talibé & UI Classe | `model.dao.TalibeDao`, `controller.ClasseController`, `view.ClasseView` |
| **Membre 4** | Progression & intégration | `model.dao.ProgressionDao`, tous les controllers/vues restants, `util.CsvExporter`, `view.AppDaara` |

---

## Conventions Git

### Branches

```
main                              → code stable, merge par PR uniquement
feature/m1-fondations             → Membre 1
feature/m2-classe-maitreui        → Membre 2
feature/m3-talibe-classeui        → Membre 3
feature/m4-progression-integration → Membre 4
```

### Format des messages de commit

```
[M<n>] <type>: <description courte>

Exemples :
[M1] feat: entités JPA Maitre + Classe avec @ManyToOne
[M2] feat: MaitreView JPanel + JTable + boutons
[M3] fix: ClasseController capture SuppressionImpossibleException
[M4] feat: CsvExporter UTF-8 avec en-têtes
```

Types : `feat` · `fix` · `refactor` · `test` · `docs`

### Fichiers exclus du dépôt (`.gitignore`)

```
target/
*.class
.idea/
*.iml
*.iws
src/main/resources/hibernate.cfg.xml
*.csv
```

---

*Projet réalisé dans le cadre du cours de Génie Logiciel — ISI Dakar, Licence 2.*
