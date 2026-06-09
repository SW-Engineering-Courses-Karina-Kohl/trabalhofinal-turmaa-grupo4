package br.edu.ufrgs.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmprestimoTest {

    // Valida o cálculo correto quando a devolução passa da data prevista
    @Test
    void calculaAtraso() {
        LocalDate dataPrevista = LocalDate.of(2026, 6, 1);
        LocalDate dataDevolucao = LocalDate.of(2026, 6, 6); // 5 dias de atraso

        Emprestimo emprestimo = new Emprestimo(1, "Dom Casmurro", "Academico", dataPrevista, dataDevolucao);

        assertEquals(5, emprestimo.getDiasAtraso());
    }

    // Valida que os dias de atraso devem ser zero quando devolvido no dia correto
    @Test
    void retornadoNoPrazo() {
        LocalDate dataPrevista = LocalDate.of(2026, 6, 1);
        LocalDate dataDevolucao = LocalDate.of(2026, 6, 1);

        Emprestimo emprestimo = new Emprestimo(1, "Dom Casmurro", "Academico", dataPrevista, dataDevolucao);

        assertEquals(0, emprestimo.getDiasAtraso());
    }

    // Valida que os dias de atraso devem ser zero mesmo se o livro for entregue antes
    @Test
    void retornadoAdiantado() {
        LocalDate dataPrevista = LocalDate.of(2026, 6, 5);
        LocalDate dataDevolucao = LocalDate.of(2026, 6, 1);

        Emprestimo emprestimo = new Emprestimo(1, "Dom Casmurro", "Academico", dataPrevista, dataDevolucao);

        assertEquals(0, emprestimo.getDiasAtraso());
    }

    // Valida o funcionamento simples do getter e setter do valor da multa
    @Test
    void calculaValorDaMulta() {
        LocalDate hoje = LocalDate.now();
        Emprestimo emprestimo = new Emprestimo(1, "Dom Casmurro", "Academico", hoje, hoje);

        emprestimo.setValorMulta(15.50);

        assertEquals(15.50, emprestimo.getValorMulta());
    }
}
