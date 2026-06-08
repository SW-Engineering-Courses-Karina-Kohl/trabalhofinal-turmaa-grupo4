package br.edu.ufrgs.service;

import br.edu.ufrgs.model.ConfiguracaoMultas;
import br.edu.ufrgs.model.Emprestimo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CalculadoraMultasTest {

    @Test
    void deveCalcularMultaCorretamente() {
        ConfiguracaoMultas config = mock(ConfiguracaoMultas.class);
        when(config.getValorBase("Academico")).thenReturn(2.5);

        Emprestimo emprestimo = mock(Emprestimo.class);
        when(emprestimo.getCategoria()).thenReturn("Academico");
        when(emprestimo.getDiasAtraso()).thenReturn(4L);

        CalculadoraMultas calculadora = new CalculadoraMultas(config);

        double resultado = calculadora.processarMulta(emprestimo);
        assertEquals(10.0, resultado);
    }
}
