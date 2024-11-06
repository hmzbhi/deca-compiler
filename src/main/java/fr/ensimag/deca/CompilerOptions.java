package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl42
 * @date 01/01/2024
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;

    public int getDebug() {
        return debug;
    }

    public int getRegMax() {
        return regMax;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    
    public boolean getParse() {
        return parse;
    }
    
    public boolean getVerif() {
        return verif;
    }
    
    public boolean getCheck() {
        return check;
    }

    public Set<File> getSourceFiles() {
        return Collections.unmodifiableSet(sourceFiles);
    }
    
    private int debug = 0;
    private int regMax = 15;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean parse = false;
    private boolean verif = false;
    private boolean check = true;
    private Set<File> sourceFiles = new HashSet<File>();

    public void parseArgs(String[] args) throws CLIException {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case "-b":
                    printBanner = true;
                    break;
                case "-p":
                    parse = true;
                    break;
                case "-v":
                    verif = true;
                    break;
                case "-n":
                    check = false;
                    break;
                case "-r":
                    try {
                        regMax = Integer.valueOf(args[++i]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new CLIException("L'argument X de l'option -r est manquant");
                    } catch (NumberFormatException e) {
                        throw new CLIException("L'argument X de l'option -r doit être un entier");
                    }

                    if (4 <= regMax && regMax <= 16) {
                        regMax--;
                    } else {
                        throw new CLIException("L'argument X de l'option -r doit être compris entre 4 et 16");
                    }

                    break;
                case "-d":
                    debug++;
                    break;
                case "-P":
                    parallel = true;
                    break;
                default:
                    if (!arg.endsWith(".deca")) {
                        throw new CLIException("<fichier deca> doit avoir l'extension .deca");
                    }

                    sourceFiles.add(new File(arg));
                    break;
            }
        }

        if (parse && verif) {
            throw new CLIException("Les options -p et -v sont incompatibles");
        }

        if (printBanner && !sourceFiles.isEmpty()) {
            throw new CLIException("L'option -b doit être utilisée sans fichier source");
        }
        
        if (debug != 0 || regMax != 15 || parallel || parse || verif || !check) {
            if (printBanner) {
                throw new CLIException("L'option -b doit être utilisée sans autre option");
            }
        
            if (sourceFiles.isEmpty()) {
                throw new CLIException("<fichier deca> est manquant");
            }
        }

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
            case QUIET:
                break; // keep default
            case INFO:
                logger.setLevel(Level.INFO);
                break;
            case DEBUG:
                logger.setLevel(Level.DEBUG);
                break;
            case TRACE:
                logger.setLevel(Level.TRACE);
                break;
            default:
                logger.setLevel(Level.ALL);
                break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
    }

    protected void displayUsage() {
        System.out.println("\nSYNTAXE");
        System.out.println("\tdecac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]");
        System.out.println("\nOPTIONS");
        System.out.println("\t-b (banner) : affiche une bannière indiquant le nom de l'équipe");
        System.out.println("\n\t-p (parse) : arrête decac après l'étape de construction de l'arbre, et affiche la décompilation de ce dernier" +
                        "             \n\t(i.e. s'il n'y a qu'un fichier source à compiler, la sortie doit être un programme deca syntaxiquement correct)");
        System.out.println("\n\t-v (verification) : arrête decac après l'étape de vérifications (ne produit aucune sortie en l'absence d'erreur)");
        System.out.println("\n\t-n (no check) : supprime les tests à l'exécution spécifiés dans les points 11.1 et 11.3 de la sémantique de Deca.");
        System.out.println("\n\t-r X (registers) : limite les registres banalisés disponibles à R0 ... R{X-1}, avec 4 <= X <= 16");
        System.out.println("\n\t-d (debug) : active les traces de debug. Répéter l'option plusieurs fois pour avoir plus de traces.");
        System.out.println("\n\t-P (parallel) : s'il y a plusieurs fichiers sources, lance la compilation des fichiers en parallèle (pour accélérer la compilation)\n");
    }
}
