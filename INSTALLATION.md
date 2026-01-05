# Installation et Configuration du Projet JavaFX

## Prérequis

### 1. Vérifier Java
Ouvrez PowerShell et exécutez :
```powershell
java -version
```
Vous devez avoir Java 11 ou supérieur. Sinon, téléchargez-le depuis [Oracle](https://www.oracle.com/java/technologies/downloads/) ou [Adoptium](https://adoptium.net/).

### 2. Installer Maven
Vérifiez si Maven est installé :
```powershell
mvn -version
```

Si Maven n'est pas installé :
- **Option A : Chocolatey** (recommandé)
  ```powershell
  choco install maven
  ```
  
- **Option B : Installation manuelle**
  1. Téléchargez Maven depuis https://maven.apache.org/download.cgi
  2. Extrayez dans `C:\Program Files\Apache\Maven`
  3. Ajoutez au PATH : `C:\Program Files\Apache\Maven\bin`
  4. Redémarrez PowerShell

## Installation du Projet

### Étape 1 : Nettoyer et compiler
À la racine du projet, exécutez :
```powershell
mvn clean install
```

Cette commande va :
- Télécharger toutes les dépendances JavaFX
- Compiler le projet
- Résoudre toutes les erreurs d'import

### Étape 2 : Exécuter l'application
```powershell
mvn javafx:run
```

## Commandes Maven Utiles

| Commande | Description |
|----------|-------------|
| `mvn clean` | Nettoie les fichiers compilés |
| `mvn compile` | Compile le code source |
| `mvn package` | Crée un fichier JAR |
| `mvn javafx:run` | Lance l'application JavaFX |
| `mvn clean install` | Nettoie, compile et installe |

## Résolution de Problèmes

### Les erreurs "javafx cannot be resolved" persistent
1. Supprimez le dossier `.m2` dans votre répertoire utilisateur
2. Relancez `mvn clean install`

### L'application ne se lance pas
Vérifiez que votre `JAVA_HOME` pointe vers Java 11+ :
```powershell
echo $env:JAVA_HOME
```

Pour le configurer :
```powershell
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Java\jdk-17', 'User')
```

### Erreur de module JavaFX
Si vous voyez des erreurs de modules, assurez-vous que le plugin `javafx-maven-plugin` est bien dans le `pom.xml`.

## Configuration VS Code (optionnel)

Pour un meilleur support Java dans VS Code, installez ces extensions :
1. Extension Pack for Java
2. Maven for Java

## Structure du Projet

```
java_front/
├── pom.xml                  # Configuration Maven
├── src/
│   └── main/
│       ├── java/            # Code source
│       │   └── fr/netwok/
│       └── resources/       # Ressources (FXML, CSS, images)
│           └── fr/netwok/
└── target/                  # Fichiers compilés (généré par Maven)
```

## Prochaines Étapes

Une fois Maven configuré :
1. Exécutez `mvn clean install` pour télécharger les dépendances
2. Toutes les erreurs d'import JavaFX devraient disparaître
3. Lancez l'application avec `mvn javafx:run`
