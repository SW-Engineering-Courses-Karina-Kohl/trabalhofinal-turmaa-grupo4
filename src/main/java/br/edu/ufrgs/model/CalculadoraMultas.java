package br.edu.ufrgs.model;

// a calculadora de multas recebe uma ConfiguracaoMultas (arquivo de input csv com as regras de multa)
// e tem um método processarMulta que recebe um Emprestimo e retorna o valor da multa
// esse método seria chamado em outra classe da aplicação principal que conhece o Emprestimo e a ConfiguracaoMultas
// ele nao sera chamado de Emprestimo ja que Emprestimo nao conhece as regras de multa
public class CalculadoraMultas {
    private ConfiguracaoMultas config;

    public CalculadoraMultas(ConfiguracaoMultas config) {
        this.config = config;
    }

    public double processarMulta(Emprestimo emprestimo) {
        double valorMulta = calcularValorMulta(emprestimo.getCategoria(), emprestimo.getDiasAtraso());
        return valorMulta;
    }

    private double calcularValorMulta(String categoria, long diasAtraso) {
        double valorBase = config.getValorBase(categoria);
        return valorBase * diasAtraso;
    }
}
