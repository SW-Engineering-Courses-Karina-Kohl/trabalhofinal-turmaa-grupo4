package br.edu.ufrgs.service;

import java.util.List;

import br.edu.ufrgs.io.ExportadorCSV;
import br.edu.ufrgs.io.LeitorEmprestimosCSV;
import br.edu.ufrgs.model.ConfiguracaoMultas;
import br.edu.ufrgs.model.Emprestimo;

public class ProcessadorBiblioteca {
    private final LeitorEmprestimosCSV leitor;
    private final ConfiguracaoMultas config;
    private CalculadoraMultas calculadora;
    private final ExportadorCSV exportador;

    public ProcessadorBiblioteca() {
        this.leitor = new LeitorEmprestimosCSV();
        this.config = new ConfiguracaoMultas();
        this.exportador = new ExportadorCSV();
    }

    public void carregarConfiguracao(String caminhoConfig) {
        config.carregar(caminhoConfig);
        this.calculadora = new CalculadoraMultas(config);
    }

    public List<Emprestimo> processar(String caminhoEmprestimos) {
        List<Emprestimo> emprestimos = leitor.ler(caminhoEmprestimos);

        for (Emprestimo emprestimo : emprestimos) {
            double multa = calculadora.processarMulta(emprestimo);
            emprestimo.setValorMulta(multa);
        }

        return emprestimos;
    }

    public void exportar(String caminhoSaida, List<Emprestimo> emprestimos) {
        exportador.exportar(caminhoSaida, emprestimos);
    }
}
