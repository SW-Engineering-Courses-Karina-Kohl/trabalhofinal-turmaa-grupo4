package br.edu.ufrgs.io;

import br.edu.ufrgs.model.Emprestimo;
import br.edu.ufrgs.service.CalculadoraMultas;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ExportadorCSV {

    public void exportar(String caminhoArquivo, List<Emprestimo> emprestimos, CalculadoraMultas calculadora) {
        try (PrintWriter writer = new PrintWriter(
                Files.newBufferedWriter(Paths.get(caminhoArquivo), StandardCharsets.UTF_8))) {
            escreverConteudo(writer, emprestimos, calculadora);
        } catch (IOException e) {
            throw new RuntimeException("erro ao exportar arquivo: " + caminhoArquivo, e);
        }
    }

    public void exportarParaResposta(PrintWriter writer, List<Emprestimo> emprestimos) {
        escreverConteudo(writer, emprestimos, null);
    }

    private void escreverConteudo(PrintWriter writer, List<Emprestimo> emprestimos, CalculadoraMultas calculadora) {
        writer.println("id,titulo,categoria,data_esperada,data_real,valor_multa");
        for (Emprestimo emprestimo : emprestimos) {
            double valorMulta = calculadora != null
                    ? calculadora.processarMulta(emprestimo)
                    : emprestimo.getValorMulta();

            writer.println(String.format("%02d,%s,%s,%s,%s,%.2f",
                    emprestimo.getId(),
                    emprestimo.getTitulo().contains(",") ? "\"" + emprestimo.getTitulo() + "\"" : emprestimo.getTitulo(),
                    emprestimo.getCategoria(),
                    emprestimo.getDataPrevista().toString(),
                    emprestimo.getDataDevolucao().toString(),
                    valorMulta
            ));
        }
    }
}
