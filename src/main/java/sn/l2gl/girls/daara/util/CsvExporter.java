package sn.l2gl.girls.daara.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class CsvExporter {


    public static void exporter(
            File fichier,
            String[] entetes,
            List<String[]> lignes
    ) throws IOException {// Peut échouer si l'écriture du fichier rencontre un problème

        //creattion d'un outil pour ecrire un texte  dans un fichier
        try(BufferedWriter w = Files.newBufferedWriter(
                //transformation du fichier en objet path
                fichier.toPath(),
                StandardCharsets.UTF_8
        )){


            //Ecriture de l'entete
            w.write(String.join(",", entetes));
            //passer a la ligne suivante
            w.newLine();


            //Ecriture des lignes
            // parcours de  chaque enregistrement à écrire
            for(String[] ligne : lignes){

                w.write(String.join(",", ligne));
                w.newLine();
            }

        }
    }
}
