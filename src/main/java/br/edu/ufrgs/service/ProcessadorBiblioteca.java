package br.edu.ufrgs.service;

import java.io.PrintWriter;
import java.util.List;

import br.edu.ufrgs.io.ExportadorCSV;
import br.edu.ufrgs.io.ExportadorResultados;
import br.edu.ufrgs.io.LeitorEmprestimos;
import br.edu.ufrgs.io.LeitorEmprestimosCSV;
import br.edu.ufrgs.model.ConfiguracaoMultas;
import br.edu.ufrgs.model.Emprestimo;

public class ProcessadorBiblioteca {
    private final LeitorEmprestimos leitor;
    private final ConfiguracaoMultas config;
    private CalculadoraMultas calculadora;
    private final ExportadorResultados exportador;

    public ProcessadorBiblioteca() {
        this.leitor = new LeitorEmprestimosCSV();
        this.config = new ConfiguracaoMultas();
        this.exportador = new ExportadorCSV();
    }

    public void carregarConfiguracao(String caminhoConfig) {
        config.carregar(caminhoConfig);
        this.calculadora = new CalculadoraMultas(config);
    }

    // processa o arquivo de emprestimos usando a configuracao de multas carregada
    public List<Emprestimo> processar(String caminhoEmprestimos) {
        // impede o processamento sem configuracao carregada
        if (calculadora == null) {
            throw new IllegalStateException("configuracao de multas nao carregada");
        }

        List<Emprestimo> emprestimos = leitor.ler(caminhoEmprestimos);

        for (Emprestimo emprestimo : emprestimos) {
            double multa = calculadora.processarMulta(emprestimo);
            emprestimo.setValorMulta(multa);
        }

        return emprestimos;
    }

    // conta quantos emprestimos tiveram atraso
    public int contarAtrasados(List<Emprestimo> emprestimos) {
        int totalAtrasados = 0;

        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getDiasAtraso() > 0) {
                totalAtrasados++;
            }
        }

        return totalAtrasados;
    }

    // soma o valor total das multas ja calculadas
    public double somarMultas(List<Emprestimo> emprestimos) {
        double totalMultas = 0.0;

        for (Emprestimo emprestimo : emprestimos) {
            totalMultas += emprestimo.getValorMulta();
        }

        return totalMultas;
    }

    public void exportar(String caminhoSaida, List<Emprestimo> emprestimos) {
        exportador.exportar(caminhoSaida, emprestimos);
    }

    public void exportarParaResposta(PrintWriter writer, List<Emprestimo> emprestimos) {
        exportador.exportarParaResposta(writer, emprestimos);
    }
}