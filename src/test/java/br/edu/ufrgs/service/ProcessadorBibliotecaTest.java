package br.edu.ufrgs.service;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.ufrgs.model.Emprestimo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProcessadorBibliotecaTest {

    private ProcessadorBiblioteca processador;

    @TempDir
    Path pastaTemporaria;

    // Inicializa o serviço limpo antes de cada cenário de teste
    @BeforeEach
    public void setUp() {
        processador = new ProcessadorBiblioteca();
    }

    // --- TESTES DE CONFIGURAÇÃO E ERROS ---

    // Valida a proteção do sistema contra tentativas de processamento sem regras carregadas
    @Test
    public void deveLancarExcecaoAoProcessarSemAntesCarregarConfiguracao() {
        String caminhoFalso = pastaTemporaria.resolve("emprestimos.csv").toString();

        assertThrows(IllegalStateException.class, () -> {
            processador.processar(caminhoFalso);
        });
    }

    // --- TESTES DE PROCESSAMENTO DE ARQUIVOS ---

    // Valida o fluxo completo de leitura, amarração de regras de multa e cálculo dos registros
    @Test
    public void deveProcessarEmprestimosEInjetarMultasComSucesso() throws IOException {
        Path arquivoConfig = pastaTemporaria.resolve("config.csv");
        String conteudoConfig = "categoria,valor_diario\nAcademico,2.00";
        Files.write(arquivoConfig, conteudoConfig.getBytes(StandardCharsets.UTF_8));

        Path arquivoEmprestimos = pastaTemporaria.resolve("emprestimos.csv");
        String conteudoEmprestimos = "id,titulo,categoria,dataPrevista,dataDevolucao\n1,Livro A,Academico,2026-06-01,2026-06-04";
        Files.write(arquivoEmprestimos, conteudoEmprestimos.getBytes(StandardCharsets.UTF_8));

        processador.carregarConfiguracao(arquivoConfig.toString());
        List<Emprestimo> resultado = processador.processar(arquivoEmprestimos.toString());

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        
        Emprestimo emprestimoProcessado = resultado.get(0);
        assertEquals(3, emprestimoProcessado.getDiasAtraso());
        assertEquals(6.00, emprestimoProcessado.getValorMulta()); // 3 dias * R$ 2.00
    }

    // --- TESTES DE CONTABILIZAÇÃO E SOMA MÁTEMÁTICA ---

    // Valida se o contador ignora livros no prazo e incrementa apenas os atrasados
    @Test
    public void deveContarQuantidadeCorretaDeEmprestimosAtrasados() {
        List<Emprestimo> lista = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        
        lista.add(new Emprestimo(1, "Livro 1", "Academico", hoje, hoje.plusDays(3))); // 3 dias atraso
        lista.add(new Emprestimo(2, "Livro 2", "Academico", hoje, hoje));            // 0 dias atraso
        lista.add(new Emprestimo(3, "Livro 3", "Academico", hoje, hoje.plusDays(1))); // 1 dia atraso

        int totalAtrasados = processador.contarAtrasados(lista);

        assertEquals(2, totalAtrasados);
    }

    // Valida o somatório matemático de ponto flutuante do acumulador de multas
    @Test
    public void deveSomarValorTotalDasMultasDosEmprestimos() {
        List<Emprestimo> lista = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        Emprestimo emp1 = new Emprestimo(1, "Livro 1", "Academico", hoje, hoje);
        emp1.setValorMulta(10.50);
        
        Emprestimo emp2 = new Emprestimo(2, "Livro 2", "Academico", hoje, hoje);
        emp2.setValorMulta(5.25);

        lista.add(emp1);
        lista.add(emp2);

        double totalMultas = processador.somarMultas(lista);

        assertEquals(15.75, totalMultas);
    }
}
