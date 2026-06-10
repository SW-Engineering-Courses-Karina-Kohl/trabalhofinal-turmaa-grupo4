package br.edu.ufrgs.io;

import br.edu.ufrgs.model.Emprestimo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportadorCSVTest {

    @TempDir
    Path diretorioTemporario;

    private final ExportadorCSV exportador = new ExportadorCSV();

    @Test
    void deveExportarEmprestimosParaArquivo() throws Exception {
        Path arquivoSaida = diretorioTemporario.resolve("multas_processadas.csv");
        List<Emprestimo> emprestimos = List.of(
            criarEmprestimo(1, "Java Clean Code", "Academico", "2026-03-20", "2026-03-25", 10.0),
            criarEmprestimo(2, "Dom Casmurro", "Comum", "2026-03-15", "2026-03-15", 0.0)
        );

        exportador.exportar(arquivoSaida.toString(), emprestimos);

        String conteudo = Files.readString(arquivoSaida, StandardCharsets.UTF_8);
        assertEquals(
            "id,titulo,categoria,data_esperada,data_real,valor_multa\n" +
            "01,Java Clean Code,Academico,2026-03-20,2026-03-25,10.00\n" +
            "02,Dom Casmurro,Comum,2026-03-15,2026-03-15,0.00\n",
            conteudo
        );
    }

    @Test
    void deveExportarEmprestimosParaResposta() {
        List<Emprestimo> emprestimos = List.of(
            criarEmprestimo(3, "Manuscrito Original", "Raro", "2026-03-10", "2026-03-12", 20.0)
        );

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        exportador.exportarParaResposta(printWriter, emprestimos);
        printWriter.flush();

        assertEquals(
            "id,titulo,categoria,data_esperada,data_real,valor_multa\n" +
            "03,Manuscrito Original,Raro,2026-03-10,2026-03-12,20.00\n",
            stringWriter.toString()
        );
    }

    @Test
    void deveColocarAspasNoTituloComVirgula() {
        List<Emprestimo> emprestimos = List.of(
            criarEmprestimo(4, "Livro, Especial", "Comum", "2026-03-01", "2026-03-05", 4.0)
        );

        String conteudo = exportarParaString(emprestimos);

        assertTrue(conteudo.contains("04,\"Livro, Especial\",Comum,2026-03-01,2026-03-05,4.00"));
    }

    @Test
    void deveExportarListaVaziaApenasComCabecalho() {
        String conteudo = exportarParaString(Collections.emptyList());

        assertEquals("id,titulo,categoria,data_esperada,data_real,valor_multa\n", conteudo);
    }

    @Test
    void deveLancarRuntimeExceptionQuandoCaminhoDeSaidaEhInvalido() {
        List<Emprestimo> emprestimos = List.of(
            criarEmprestimo(1, "Java Clean Code", "Academico", "2026-03-20", "2026-03-25", 10.0)
        );

        String caminhoInvalido = diretorioTemporario.resolve("pasta_inexistente/saida.csv").toString();

        RuntimeException excecao = assertThrows(
            RuntimeException.class,
            () -> exportador.exportar(caminhoInvalido, emprestimos)
        );

        assertTrue(excecao.getMessage().contains("erro ao exportar arquivo"));
    }

    @Test
    void deveImplementarInterfaceExportadorResultados() {
        ExportadorResultados exportadorInterface = new ExportadorCSV();
        assertTrue(exportadorInterface instanceof ExportadorCSV);
    }

    @Test
    void deveManterRoundTripEntreLeituraEExportacao() throws Exception {
        Path arquivoEntrada = diretorioTemporario.resolve("entrada.csv");
        Files.writeString(
            arquivoEntrada,
            "id,titulo,categoria,data_esperada,data_real\n" +
            "01,Java Clean Code,Academico,2026-03-20,2026-03-25\n",
            StandardCharsets.UTF_8
        );

        LeitorEmprestimosCSV leitor = new LeitorEmprestimosCSV();
        List<Emprestimo> emprestimos = leitor.ler(arquivoEntrada.toString());
        emprestimos.get(0).setValorMulta(10.0);

        Path arquivoSaida = diretorioTemporario.resolve("saida.csv");
        exportador.exportar(arquivoSaida.toString(), emprestimos);

        String conteudo = Files.readString(arquivoSaida, StandardCharsets.UTF_8);
        assertTrue(conteudo.contains("01,Java Clean Code,Academico,2026-03-20,2026-03-25,10.00"));
    }

    private Emprestimo criarEmprestimo(
            int id,
            String titulo,
            String categoria,
            String dataPrevista,
            String dataDevolucao,
            double valorMulta) {

        Emprestimo emprestimo = new Emprestimo(
            id,
            titulo,
            categoria,
            LocalDate.parse(dataPrevista),
            LocalDate.parse(dataDevolucao)
        );
        emprestimo.setValorMulta(valorMulta);
        return emprestimo;
    }

    private String exportarParaString(List<Emprestimo> emprestimos) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exportador.exportarParaResposta(printWriter, emprestimos);
        printWriter.flush();
        return stringWriter.toString();
    }
}
