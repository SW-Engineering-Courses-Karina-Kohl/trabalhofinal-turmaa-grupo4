package br.edu.ufrgs.io;

import java.util.List;

import br.edu.ufrgs.model.Emprestimo;

// interface para leitura de emprestimos
public interface LeitorEmprestimos {

    // le um arquivo e retorna a lista de emprestimos
    List<Emprestimo> ler(String caminhoArquivo);
}