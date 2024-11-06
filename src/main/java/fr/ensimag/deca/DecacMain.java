package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl42
 * @date 01/01/2024
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);

    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();

        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n" + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }

        if (options.getPrintBanner()) {
            System.out.println();
            System.out.println("=========== ProjetGL2024, GR9, GL42 ===========");
            System.out.println(" ahjaous, bouihih, guessouo, senameg, thiongam ");
            System.out.println("===============================================");
            System.out.println();
            System.exit(0);
        }

        if (options.getSourceFiles().isEmpty()) {
            options.displayUsage();
            System.exit(0);
        }

        if (options.getParallel()) {
            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future<Boolean>> futures = new ArrayList<>();

            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                Future<Boolean> future = pool.submit(() -> compiler.compile());
                futures.add(future);
            }

            for (Future<Boolean> future : futures) {
                try {
                    if (future.get()) {
                        error = true;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println(e.getMessage());
                }
            }

            pool.shutdown();
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }

        System.exit(error ? 1 : 0);
    }
}
