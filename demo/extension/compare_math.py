import subprocess
import math

def lire_valeurs_de_sortie_deca():
    process = subprocess.Popen(["ima", "./test_include_math.ass"], stdout=subprocess.PIPE)
    output, _ = process.communicate()
    output = output.decode("utf-8")  # Décodez les bytes en str
    valeurs = [float(ligne.strip()) for ligne in output.split('\n') if ligne.strip()]
    return valeurs

def comparer_valeurs(valeurs_deca):

    valeurs_math = []
    for i in range(10):
        valeurs_math.append(math.cos(4 * math.pi /(i+1)));    
    # for i in range(0, 10):
    #     valeurs_math.append(math.atan(-(i /100.0 + 0.85)));
    
    # Comparer les valeurs
    for i in range(10):
        # print(valeurs_deca[i])
        # print(valeurs_math[i])
        print(abs(valeurs_deca[i] - valeurs_math[i]))

if __name__ == "__main__":
    valeurs_deca = lire_valeurs_de_sortie_deca()
    comparer_valeurs(valeurs_deca)

