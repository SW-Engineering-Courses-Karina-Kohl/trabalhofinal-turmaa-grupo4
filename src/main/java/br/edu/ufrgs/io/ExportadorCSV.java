package br.edu.ufrgs.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import br.edu.ufrgs.model.Emprestimo;

public class ExportadorCSV implements ExportadorResultados {

    // exporta os emprestimos processados para um arquivo csv
    public void exportar(String caminhoArquivo, List<Emprestimo> emprestimos) {
        try (PrintWriter writer = new PrintWriter(
                Files.newBufferedWriter(Paths.get(caminhoArquivo), StandardCharsets.UTF_8))) {
            escreverConteudo(writer, emprestimos);
        } catch (IOException e) {
            throw new RuntimeException("erro ao exportar arquivo: " + caminhoArquivo, e);
        }
    }

    // exporta os emprestimos processados diretamente para a resposta http
    public void exportarParaResposta(PrintWriter writer, List<Emprestimo> emprestimos) {
        escreverConteudo(writer, emprestimos);
    }

    // escreve o conteudo csv usando o valor da multa ja armazenado em cada emprestimo
    private void escreverConteudo(PrintWriter writer, List<Emprestimo> emprestimos) {
        writer.println("id,titulo,categoria,data_esperada,data_real,valor_multa");

        for (Emprestimo emprestimo : emprestimos) {
            String titulo = emprestimo.getTitulo();

            // coloca aspas no titulo caso tenha virgula
            if (titulo.contains(",")) {
                titulo = "\"" + titulo + "\"";
            }

            writer.println(String.format(
                    Locale.US,
                    "%02d,%s,%s,%s,%s,%.2f",
                    emprestimo.getId(),
                    titulo,
                    emprestimo.getCategoria(),
                    emprestimo.getDataPrevista().toString(),
                    emprestimo.getDataDevolucao().toString(),
                    emprestimo.getValorMulta()
            ));
        }
    }
}