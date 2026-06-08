package br.edu.ufrgs.io;

import br.edu.ufrgs.model.Emprestimo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LeitorEmprestimosCSV {

    public List<Emprestimo> ler(String caminhoArquivo) {
        List<Emprestimo> emprestimos = new ArrayList<>();

        try {
            List<String> linhas = Files.readAllLines(Paths.get(caminhoArquivo), StandardCharsets.UTF_8);
            boolean primeiraLinha = true;

            for (String linhaBruta : linhas) {
                String linha = linhaBruta.trim();
                if (linha.isEmpty()) {
                    continue;
                }
                if (linha.startsWith("\uFEFF")) {
                    linha = linha.substring(1);
                }

                String[] partes = linha.split(",");
                if (partes.length < 5) {
                    primeiraLinha = false;
                    continue;
                }

                String idStr = partes[0].trim();
                if (primeiraLinha && (idStr.equalsIgnoreCase("id") || idStr.equalsIgnoreCase("\uFEFFid"))) {
                    primeiraLinha = false;
                    continue;
                }

                try {
                    int id = Integer.parseInt(idStr);
                    String titulo = partes[1].trim();
                    String categoria = partes[2].trim();
                    LocalDate dataEsperada = LocalDate.parse(partes[3].trim());
                    LocalDate dataReal = LocalDate.parse(partes[4].trim());
                    emprestimos.add(new Emprestimo(id, titulo, categoria, dataEsperada, dataReal));
                } catch (Exception e) {
                    // ignora erros de parse
                }

                primeiraLinha = false;
            }
        } catch (IOException e) {
            throw new RuntimeException("erro ao ler arquivo de emprestimos: " + caminhoArquivo, e);
        }

        return emprestimos;
    }
}
