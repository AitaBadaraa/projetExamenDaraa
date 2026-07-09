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
    ) throws IOException {


        try(BufferedWriter w = Files.newBufferedWriter(
                fichier.toPath(),
                StandardCharsets.UTF_8
        )){


            //Ecriture de l'entete
            w.write(String.join(",", entetes));
            w.newLine();


            //Ecriture des lignes
            for(String[] ligne : lignes){

                w.write(String.join(",", ligne));
                w.newLine();
            }

        }
    }
}
