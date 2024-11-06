package tools;

import org.apache.log4j.Logger;

/**
 * Example program, just to experiment. Greets the user, in english by default
 * and in French in case a parameter is given.
 *
 * DONE : Alice et Bob rajoutent leurs noms sur la ligne suivante
 *
 * @author Alice and Bob
 * @date 01/01/2024
 */
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        // DONE : lire et modifier le fichier src/main/resources/log4j.properties
        // DONE : pour controler le comportement de ces affichages.
        LOG.info("Entering main method in App");
        SayHello sayHello;
        String name = null;
        if (args.length == 0) {
            LOG.debug("We're going to speak english");
            sayHello = new SayHello();
            name = "John Doe";
        } else {
            LOG.debug("Nous allons parler français");
            sayHello = new DireBonjour();
            // Oops, forgot to set the variable 'name' :-(
            name = "Pierre Dupont";
        }
        LOG.info("object sayHello instanciated");
        sayHello.sayIt();

        LOG.trace("name = " + name);

        // cette méthode vérifie comme précondition que "name"
        // n'est pas null. Dans la version anglaise, la précondition
        // est vérifiée, mais pas dans la version française. Vérifiez
        // que l'exception est bien levée dans ce cas :
        sayHello.sayItTo(name);

        computeAnswer();
    }

    private static void computeAnswer() {
        // DONE : Alice remplace "print" par "println"
        System.out.println("I'm computing the answer");

        System.out.println("I'm thinking");

        System.out.println("I'm thinking again ...");

        // DONE : Bob remplace 43 par 42
        System.out.println("The answer is 42");
    }
}
