package br.edu.ufrgs.service;

import br.edu.ufrgs.model.ConfiguracaoMultas;
import br.edu.ufrgs.model.Emprestimo;

public class CalculadoraMultas {
    private ConfiguracaoMultas config;

    public CalculadoraMultas(ConfiguracaoMultas config) {
        this.config = config;
    }

    public double processarMulta(Emprestimo emprestimo) {
        return calcularValorMulta(emprestimo.getCategoria(), emprestimo.getDiasAtraso());
    }

    public double calcularValorMulta(String categoria, long diasAtraso) {
        double valorBase = config.getValorBase(categoria);
        return valorBase * diasAtraso;
    }
}
