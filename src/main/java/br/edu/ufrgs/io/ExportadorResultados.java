package br.edu.ufrgs.io;

import java.io.PrintWriter;
import java.util.List;

import br.edu.ufrgs.model.Emprestimo;

// interface para exportacao de resultados processados
public interface ExportadorResultados {

    // exporta os emprestimos processados para um arquivo csv
    void exportar(String caminhoArquivo, List<Emprestimo> emprestimos);

    // exporta os emprestimos processados diretamente para a resposta http
    void exportarParaResposta(PrintWriter writer, List<Emprestimo> emprestimos);
}