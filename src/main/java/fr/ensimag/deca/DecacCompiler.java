package fr.ensimag.deca;

import fr.ensimag.deca.codegen.VTable;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.ima.pseudocode.AbstractLine;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Logger;

/**
 * Decac compiler instance.
 *
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 *
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl42
 * @date 01/01/2024
 */
public class DecacCompiler {
    private int d;
    private int idReg;
    private boolean[] errors;
    private int numLabel;
    private int tstoCurr;
    private int tstoMax;
    private int addSP;
    private Map<Symbol,DAddr> classAdresses;
    private VTable vTable;

	private static final Logger LOG = Logger.getLogger(DecacCompiler.class);

    /**
     * Portable newline character.
     */
    private static final String nl = System.getProperty("line.separator", "\n");

    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();
        this.compilerOptions = compilerOptions;
        this.source = source;
        setCurrentProgram(program);
        this.d = 1;
        this.idReg = 2;
        this.errors = new boolean[5];

        for (int i = 0; i < errors.length; i++) {
            this.errors[i] = false;
        }
        
        this.numLabel = 0;
        this.tstoCurr = 2;
        this.tstoMax = 2;
        this.addSP = 2;
        this.classAdresses = new HashMap<>();
        this.vTable= new VTable(this);
    }

    /**
     * Source file associated with this compiler instance.
     */
    public int getD(){
        return this.d;
    }

    public void incrD(){
        this.d++;
    }
    public void decrD(){
        this.d--;
    }

    public int getIdReg(){
        return this.idReg;
    }

    public void setIdReg(int i) {
        this.idReg = i;
    }

    public void setError(int i) {
        this.errors[i] = true;
    }

    public boolean getError(int i) {
        return this.errors[i];
    }
    
    public int getLabelNumber() {
        return this.numLabel;
    }

    public void incrLabelNumber() {
        this.numLabel++;
    }

    public int getTSTOCurr() {
        return this.tstoCurr;
    }

    public void setTSTOCurr(int i) {
        this.tstoCurr = i;
    }

    public int getTSTOMax() {
        return this.tstoMax;
    }

    public void setTSTOMax(int i) {
        this.tstoMax = i;
    }

    public int getADDSP() {
        return this.addSP;
    }

    public void setADDSP(int i) {
        this.addSP = i;
    }

    public Map<Symbol, DAddr> getClassAdresses() {
		return classAdresses;
	}

    public VTable getVTable() {
        return vTable;
    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }

    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        getCurrentProgram().add(line);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        getCurrentProgram().addComment(comment);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        getCurrentProgram().addLabel(label);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction instruction) {
        getCurrentProgram().addInstruction(instruction);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction,
     *      java.lang.String)
     */
    public void addInstruction(Instruction instruction, String comment) {
        getCurrentProgram().addInstruction(instruction, comment);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addFirst(java.lang.String)
     */
    public void addFirst(String comment) {
        getCurrentProgram().addFirst(comment);
    }
    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addFirst(fr.ensimag.ima.pseudocode.Label)
     */
    public void addFirst(Label label) {
        getCurrentProgram().addFirst(label);
    }
    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addFirst(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addFirst(Instruction instruction) {
        getCurrentProgram().addFirst(instruction);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return getCurrentProgram().display();
    }

    private final CompilerOptions compilerOptions;
    private final File source;
    /**
     * The main program. Every instruction generated will eventually end up here.
     */
    private final IMAProgram program = new IMAProgram();
    
    private IMAProgram currentProgram;

    public IMAProgram getCurrentProgram() {
		return currentProgram;
	}

	public void setCurrentProgram(IMAProgram currentProgram) {
		this.currentProgram = currentProgram;
	}

    public void beginBlock() {
        setCurrentProgram(new IMAProgram());
    }

    public void endBlock() {
        program.append(getCurrentProgram());
        setCurrentProgram(program);
    }

	/** The global environment for types (and the symbolTable) */
    public final SymbolTable symbolTable = new SymbolTable();
    public final EnvironmentType environmentType = new EnvironmentType(this);

    public final EnvironmentExp environmentExp = new EnvironmentExp(null);

    public Symbol createSymbol(String name) {
        return symbolTable.create(name);
    }

    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        String sourceFile = source.getAbsolutePath();
        String destFile = sourceFile.replace(".deca", ".ass");
        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);

        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName   name of the destination (assembly) file
     * @param out        stream to use for standard output (output of decac -p)
     * @param err        stream to use to display compilation errors
     *
     * @return true on error
     */
    private boolean doCompile(String sourceName, String destName,
            PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);

        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }
        assert (prog.checkAllLocations());

        if (compilerOptions.getParse()) {
            prog.decompile(out);
            return false;
        }

        prog.verifyProgram(this);
        assert (prog.checkAllDecorations());
            
        if (compilerOptions.getVerif()) {
            return false;
        }

        prog.codeGenProgram(this);
        prog.codeGenError(this);
        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);
        
        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }
        
        LOG.info("Writing assembler file ...");
        
        program.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");

        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     *
     * @param sourceName Name of the file to parse
     * @param err        Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError    When an error prevented opening the source file
     * @throws DecacInternalError When an inconsistency was detected in the
     *                            compiler.
     * @throws LocationException  When a compilation error (incorrect program)
     *                            occurs.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err)
            throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
            lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);
    }

}
