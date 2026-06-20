package br.edu.ufrgs.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufrgs.model.Emprestimo;

// classe responsavel por ler o arquivo csv de emprestimos
public class LeitorEmprestimosCSV implements LeitorEmprestimos {

    // le o arquivo csv e retorna uma lista de emprestimos
    public List<Emprestimo> ler(String caminhoArquivo) {
        List<Emprestimo> emprestimos = new ArrayList<>();

        try {
            List<String> linhas = Files.readAllLines(Paths.get(caminhoArquivo), StandardCharsets.UTF_8);
            boolean primeiraLinha = true;
            int numeroLinha = 0;

            for (String linhaBruta : linhas) {
                numeroLinha++;

                String linha = linhaBruta.trim();

                // ignora linhas vazias
                if (linha.isEmpty()) {
                    continue;
                }

                // remove o caractere bom se ele vier no inicio do arquivo
                if (linha.startsWith("\uFEFF")) {
                    linha = linha.substring(1);
                }

                // ignora o cabecalho, se existir
                if (primeiraLinha) {
                    primeiraLinha = false;
                    if (linha.toLowerCase().startsWith("id,")) {
                        continue;
                    }
                }

                String[] partes = linha.split(",");

                // cada linha valida deve ter exatamente 5 colunas
                if (partes.length != 5) {
                    throw new IllegalArgumentException(
                        "linha invalida no arquivo de emprestimos (linha " + numeroLinha + "): " + linha
                    );
                }

                try {
                    int id = Integer.parseInt(partes[0].trim());
                    String titulo = partes[1].trim();
                    String categoria = partes[2].trim();
                    LocalDate dataEsperada = LocalDate.parse(partes[3].trim());
                    LocalDate dataReal = LocalDate.parse(partes[4].trim());

                    emprestimos.add(new Emprestimo(id, titulo, categoria, dataEsperada, dataReal));
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "erro ao interpretar a linha " + numeroLinha + " do arquivo de emprestimos: " + linha,
                        e
                    );
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("erro ao ler arquivo de emprestimos: " + caminhoArquivo, e);
        }

        return emprestimos;
    }
}