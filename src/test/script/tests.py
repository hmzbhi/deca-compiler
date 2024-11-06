#!/usr/bin/env python3

import os
import sys
import subprocess


def exec_test_write_result(file, test_name, expected):
    fichier_sortie = file[:-5] + ".lis"
    resultat_compilation = subprocess.run(
        ["./src/test/script/launchers/" + test_name, file],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
    )

    if expected == "success":
        if resultat_compilation.returncode != 0:
            with open(fichier_sortie, "w") as fichier_lis:
                fichier_lis.write(resultat_compilation.stdout)
                fichier_lis.write(resultat_compilation.stderr)
                raise Exception("Unexpected error in file : " + file)
        else:
            with open(fichier_sortie, "w") as fichier_lis:
                fichier_lis.write(resultat_compilation.stdout)
    elif expected == "error":
        if resultat_compilation.returncode == 0:
            with open(fichier_sortie, "w") as fichier_lis:
                fichier_lis.write(resultat_compilation.stdout)
                fichier_lis.write(resultat_compilation.stderr)
                raise Exception("Unexpected success in file : " + file)
        else:
            with open(fichier_sortie, "w") as fichier_lis:
                fichier_lis.write(resultat_compilation.stdout)
                fichier_lis.write(resultat_compilation.stderr)


def exec_test(file, test_name, expected):
    if test_name == "decac":
        compile = os.system("./src/main/bin/decac " + file)
    else:
        compile = os.system("./src/test/script/launchers/" + test_name + " " + file)

    if expected == "success":
        if compile != 0:
            raise Exception("Unexpected error in file : " + file)
        print("Expected success in file : " + file)
    elif expected == "error":
        if compile == 0:
            raise Exception("Unexpected success in file : " + file)
        print("Expected error in file : " + file)


def exec_test_decompile(file, test_name, expected):
    compile = os.system("./src/main/bin/decac -p " + file)

    if expected == "success":
        if compile != 0:
            raise Exception("Unexpected error in file : " + file)
        print("Expected success in file : " + file)
    elif expected == "error":
        if compile == 0:
            raise Exception("Unexpected success in file : " + file)
        print("Expected error in file : " + file)


def exec_test_gen(file, expected):
    fichier_sortie = file[:-5] + ".res"
    resultat_compilation = subprocess.run(
        ["./src/main/bin/decac", file],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
    )

    if expected == "success":
        if resultat_compilation.returncode != 0:
            with open(fichier_sortie, "w") as fichier_lis:
                fichier_lis.write(resultat_compilation.stdout)
                fichier_lis.write(resultat_compilation.stderr)
                raise Exception("Unexpected error in file : " + file)
        else:
            resultat_execution = subprocess.run(
                ["ima", file[:-4] + "ass"],
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
            )
            with open(fichier_sortie, "w") as fichier_lis:
                fichier_lis.write(resultat_execution.stdout)
    elif expected == "error":
        if resultat_compilation.returncode == 0:
            if file.__contains__("stack_overflow"):
                resultat_execution = subprocess.run(
                    ["ima", "-p", "3", file[:-4] + "ass"],
                    stdout=subprocess.PIPE,
                    stderr=subprocess.PIPE,
                    text=True,
                )
            else:
                resultat_execution = subprocess.run(
                    ["ima", file[:-4] + "ass"],
                    stdout=subprocess.PIPE,
                    stderr=subprocess.PIPE,
                    text=True,
                )
            with open(fichier_sortie, "w") as fichier_lis:
                print(resultat_execution)
                fichier_lis.write(resultat_execution.stdout)
                fichier_lis.write(resultat_execution.stderr)
        else:
            raise Exception("Unexpected error in file : " + file)


def get_files():
    files_sorted = {
        "lex": {"invalid": [], "valid": []},
        "synt": {"invalid": [], "valid": []},
        "context": {"invalid": [], "valid": []},
        "gen": {"invalid": [], "valid": [], "interactive": []},
    }

    for root, _, files in os.walk("src/test/deca"):
        for file in files:
            if file.endswith(".deca"):
                if "lex" in root:
                    if "invalid" in root:
                        files_sorted["lex"]["invalid"].append(os.path.join(root, file))
                    else:
                        files_sorted["lex"]["valid"].append(os.path.join(root, file))
                elif "synt" in root:
                    if "invalid" in root:
                        files_sorted["synt"]["invalid"].append(os.path.join(root, file))
                    else:
                        files_sorted["synt"]["valid"].append(os.path.join(root, file))
                elif "context" in root:
                    if "invalid" in root:
                        files_sorted["context"]["invalid"].append(os.path.join(root, file))
                    else:
                        files_sorted["context"]["valid"].append(os.path.join(root, file))
                elif "gen" in root:
                    if "invalid" in root:
                        files_sorted["gen"]["invalid"].append(os.path.join(root, file))
                    elif "valid" in root:
                        files_sorted["gen"]["valid"].append(os.path.join(root, file))
                    elif "interactive" in root:
                        files_sorted["gen"]["interactive"].append(os.path.join(root, file))
    return files_sorted


def runTestLex(execFunction):
    print("########## TEST LEX ##########")
    files = get_files()

    for file in files["lex"]["valid"]:
        execFunction(file, "test_lex", "success")

    for file in files["lex"]["invalid"]:
        execFunction(file, "test_lex", "error")

    print("\033[32m" + "########## ALL TEST LEX PASSED ##########" + "\033[0m")


def runTestSynt(execFunction):
    print("########## TEST SYNT ##########")
    files = get_files()

    for file in files["synt"]["valid"]:
        execFunction(file, "test_synt", "success")

    for file in files["synt"]["invalid"]:
        execFunction(file, "test_synt", "error")

    print("\033[32m" + "########## ALL TEST SYNT PASSED ##########" + "\033[0m")


def runTestContext(execFunction):
    print("########## TEST CONTEXT ##########")
    files = get_files()

    for file in files["context"]["valid"]:
        execFunction(file, "test_context", "success")

    for file in files["context"]["invalid"]:
        execFunction(file, "test_context", "error")

    print("\033[32m" + "########## ALL TEST CONTEXT PASSED ##########" + "\033[0m")


def runTestGen(execFunction, pipeline=False):
    print("########## TEST GEN ##########")
    files = get_files()

    for file in files["gen"]["valid"]:
        if pipeline:
            if not(file.__contains__("notimpl")):
                execFunction(file, "decac", "success")
        else:
            execFunction(file, "success")

    for file in files["gen"]["invalid"]:
        if not (pipeline):
            execFunction(file, "error")

    # for file in files["gen"]["interactive"]:
    #     execFunction(file, "success")

    print("\033[32m" + "########## ALL TEST GEN PASSED ##########" + "\033[0m")


def runTestDecompile():
    print("########## TEST DECOMPILE ##########")
    files = get_files()

    for file in files["synt"]["valid"]:
        exec_test_decompile(file, "decac -p", "success")

    for file in files["synt"]["invalid"]:
        exec_test_decompile(file, "decac -p", "error")

    print("\033[32m" + "########## ALL TEST DECOMPILE PASSED ##########" + "\033[0m")


def runAllTests(execFunction, pipeline=False):
    runTestLex(execFunction)
    runTestSynt(execFunction)
    runTestContext(execFunction)

    if not pipeline:
        runTestGen(exec_test_gen, pipeline)
    else:
        runTestGen(execFunction, pipeline)

    runTestDecompile()


def runTestsDev(execFunction):
    runTestLex(execFunction)
    runTestSynt(execFunction)
    runTestContext(execFunction)
    runTestGen(exec_test_gen)
    runTestDecompile()
    print("\033[32m" + "########## ALL TESTS PASSED ##########" + "\033[0m")

def countAllTest():
    files = get_files()
    return len(files["lex"]["valid"]) + len(files["lex"]["invalid"]) + len(files["synt"]["valid"]) + len(files["synt"]["invalid"]) + len(files["context"]["valid"]) + len(files["context"]["invalid"]) + len(files["gen"]["valid"]) + len(files["gen"]["invalid"]) + len(files["gen"]["interactive"])

if __name__ == "__main__":
    if len(sys.argv) == 1:
        runAllTests(exec_test)
        sys.exit(0)

    if sys.argv.__contains__("-pipeline"):
        runAllTests(exec_test, True)
        sys.exit(0)
    
    if sys.argv.__contains__("-count"):
        print(countAllTest())
        sys.exit(0)

    if sys.argv.__contains__("-h"):
        print("Usage : python3 tests.py [options]")
        print("Options :")
        print("no option : run all tests")
        print("-h : help")
        print("-w : write result in .lis and .res files instead of printing them in the terminal")
        print("-dev : run all tests in developpement mode")
        print("-lex : run only lex tests")
        print("-synt : run only synt tests")
        print("-context : run only context tests")
        print("-gen : run only gen tests")
        print("-decompile : run only decompile tests")
        sys.exit(0)

    if sys.argv.__contains__("-w"):
        execMode = exec_test_write_result
    else:
        execMode = exec_test
    if sys.argv.__contains__("-dev"):
        runTestsDev(execMode)
    elif sys.argv.__contains__("-lex"):
        runTestLex(execMode)
    elif sys.argv.__contains__("-synt"):
        runTestSynt(execMode)
    elif sys.argv.__contains__("-context"):
        runTestContext(execMode)
    elif sys.argv.__contains__("-gen"):
        runTestGen(exec_test_gen)
    elif sys.argv.__contains__("-decompile"):
        runTestDecompile()
    else:
        print("Usage : python3 tests.py [options]")
        print("Try 'python3 tests.py -h' for more information.")
        sys.exit(1)
