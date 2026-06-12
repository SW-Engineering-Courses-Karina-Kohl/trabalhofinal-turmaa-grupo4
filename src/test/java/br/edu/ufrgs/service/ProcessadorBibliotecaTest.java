package br.edu.ufrgs.service;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.ufrgs.io.ExportadorResultados;
import br.edu.ufrgs.io.LeitorEmprestimos;
import br.edu.ufrgs.model.ConfiguracaoMultas;
import br.edu.ufrgs.model.Emprestimo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
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

    // valida se o processador consegue trabalhar com implementacoes injetadas pelas interfaces
    @Test
    public void deveProcessarEmprestimosUsandoLeitorInjetado() throws IOException {
        Path arquivoConfig = pastaTemporaria.resolve("config.csv");
        String conteudoConfig =
            "categoria,valor_diario\n" +
            "Academico,3.50\n" +
            "Raro,8.00";

        Files.write(arquivoConfig, conteudoConfig.getBytes(StandardCharsets.UTF_8));

        LeitorFalso leitorFalso = new LeitorFalso();

        ProcessadorBiblioteca processadorComInjecao = new ProcessadorBiblioteca(
            leitorFalso,
            new ConfiguracaoMultas(),
            new ExportadorFalso()
        );

        processadorComInjecao.carregarConfiguracao(arquivoConfig.toString());

        List<Emprestimo> resultado = processadorComInjecao.processar("arquivo-nao-usado.csv");

        assertTrue(leitorFalso.foiChamado);
        assertEquals(2, resultado.size());

        assertEquals(10.50, resultado.get(0).getValorMulta(), 0.001);
        assertEquals(16.00, resultado.get(1).getValorMulta(), 0.001);

        assertEquals(2, processadorComInjecao.contarAtrasados(resultado));
        assertEquals(26.50, processadorComInjecao.somarMultas(resultado), 0.001);
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

    // leitor falso usado para testar o processador sem depender de um arquivo csv de emprestimos
    private static class LeitorFalso implements LeitorEmprestimos {
        private boolean foiChamado = false;

        @Override
        public List<Emprestimo> ler(String caminhoArquivo) {
            foiChamado = true;

            List<Emprestimo> emprestimos = new ArrayList<>();

            emprestimos.add(new Emprestimo(
                10,
                "Estruturas de Dados",
                "Academico",
                LocalDate.parse("2026-04-10"),
                LocalDate.parse("2026-04-13")
            ));

            emprestimos.add(new Emprestimo(
                12,
                "Colecao Especial",
                "Raro",
                LocalDate.parse("2026-04-01"),
                LocalDate.parse("2026-04-03")
            ));

            return emprestimos;
        }
    }

    // exportador falso usado apenas para criar o processador no teste
    private static class ExportadorFalso implements ExportadorResultados {
        @Override
        public void exportar(String caminhoArquivo, List<Emprestimo> emprestimos) {
            // nao precisa fazer nada neste teste
        }

        @Override
        public void exportarParaResposta(PrintWriter writer, List<Emprestimo> emprestimos) {
            // nao precisa fazer nada neste teste
        }
    }
}
