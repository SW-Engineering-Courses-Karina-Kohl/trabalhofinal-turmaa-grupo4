package br.edu.ufrgs.model;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculadoraMultasTest {
    @Test
    void deveCalcularMultaCorretamente() {
        // definicao de uma configuracao de multas e emprestimo mockados
        ConfiguracaoMultas config = mock(ConfiguracaoMultas.class);
        when(config.getValorBase("Academico")).thenReturn(2.5);

        Emprestimo emprestimo = mock(Emprestimo.class);
        when(emprestimo.getCategoria()).thenReturn("Academico");
        when(emprestimo.getDiasAtraso()).thenReturn(4L);

        // instancia a calculadora de multas com a configuracao mockada
        CalculadoraMultas calculadora = new CalculadoraMultas(config);

        double resultado = calculadora.processarMulta(emprestimo);
        assertEquals(10.0, resultado);
    }  
}
