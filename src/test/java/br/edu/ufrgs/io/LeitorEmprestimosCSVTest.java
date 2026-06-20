package br.edu.ufrgs.io;

import br.edu.ufrgs.model.Emprestimo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeitorEmprestimosCSVTest {

    @TempDir
    Path diretorioTemporario;

    private final LeitorEmprestimosCSV leitor = new LeitorEmprestimosCSV();

    @Test
    void deveLerEmprestimosComCabecalho() throws Exception {
        Path arquivo = criarArquivo(
            "id,titulo,categoria,data_esperada,data_real\n" +
            "01,Java Clean Code,Academico,2026-03-20,2026-03-25\n" +
            "02,Dom Casmurro,Comum,2026-03-15,2026-03-15\n"
        );

        List<Emprestimo> emprestimos = leitor.ler(arquivo.toString());

        assertEquals(2, emprestimos.size());

        Emprestimo primeiro = emprestimos.get(0);
        assertEquals(1, primeiro.getId());
        assertEquals("Java Clean Code", primeiro.getTitulo());
        assertEquals("Academico", primeiro.getCategoria());
        assertEquals(LocalDate.of(2026, 3, 20), primeiro.getDataPrevista());
        assertEquals(LocalDate.of(2026, 3, 25), primeiro.getDataDevolucao());
        assertEquals(5, primeiro.getDiasAtraso());

        Emprestimo segundo = emprestimos.get(1);
        assertEquals(2, segundo.getId());
        assertEquals("Dom Casmurro", segundo.getTitulo());
        assertEquals(0, segundo.getDiasAtraso());
    }

    @Test
    void deveLerEmprestimosSemCabecalho() throws Exception {
        Path arquivo = criarArquivo(
            "03,Manuscrito Original,Raro,2026-03-10,2026-03-12\n"
        );

        List<Emprestimo> emprestimos = leitor.ler(arquivo.toString());

        assertEquals(1, emprestimos.size());
        assertEquals(3, emprestimos.get(0).getId());
        assertEquals("Manuscrito Original", emprestimos.get(0).getTitulo());
        assertEquals(2, emprestimos.get(0).getDiasAtraso());
    }

    @Test
    void deveIgnorarLinhasVazias() throws Exception {
        Path arquivo = criarArquivo(
            "id,titulo,categoria,data_esperada,data_real\n" +
            "\n" +
            "01,Java Clean Code,Academico,2026-03-20,2026-03-25\n" +
            "   \n"
        );

        List<Emprestimo> emprestimos = leitor.ler(arquivo.toString());

        assertEquals(1, emprestimos.size());
    }

    @Test
    void deveRemoverBomDoInicioDoArquivo() throws Exception {
        Path arquivo = diretorioTemporario.resolve("emprestimos_bom.csv");
        String conteudo = "\uFEFFid,titulo,categoria,data_esperada,data_real\n" +
            "01,Java Clean Code,Academico,2026-03-20,2026-03-25\n";
        Files.write(arquivo, conteudo.getBytes(StandardCharsets.UTF_8));

        List<Emprestimo> emprestimos = leitor.ler(arquivo.toString());

        assertEquals(1, emprestimos.size());
        assertEquals("Java Clean Code", emprestimos.get(0).getTitulo());
    }

    @Test
    void deveRemoverEspacosNasColunas() throws Exception {
        Path arquivo = criarArquivo(
            "id,titulo,categoria,data_esperada,data_real\n" +
            " 01 , Java Clean Code , Academico , 2026-03-20 , 2026-03-25 \n"
        );

        List<Emprestimo> emprestimos = leitor.ler(arquivo.toString());

        assertEquals(1, emprestimos.size());
        assertEquals(1, emprestimos.get(0).getId());
        assertEquals("Java Clean Code", emprestimos.get(0).getTitulo());
        assertEquals("Academico", emprestimos.get(0).getCategoria());
    }

    @Test
    void deveRetornarListaVaziaQuandoArquivoPossuiApenasCabecalho() throws Exception {
        Path arquivo = criarArquivo("id,titulo,categoria,data_esperada,data_real\n");

        List<Emprestimo> emprestimos = leitor.ler(arquivo.toString());

        assertTrue(emprestimos.isEmpty());
    }

    @Test
    void deveLancarExcecaoQuandoLinhaPossuiColunasInvalidas() throws Exception {
        Path arquivo = criarArquivo(
            "id,titulo,categoria,data_esperada,data_real\n" +
            "01,Java Clean Code,Academico,2026-03-20\n"
        );

        IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class,
            () -> leitor.ler(arquivo.toString())
        );

        assertTrue(excecao.getMessage().contains("linha invalida no arquivo de emprestimos"));
    }

    @Test
    void deveLancarExcecaoQuandoIdNaoEhNumerico() throws Exception {
        Path arquivo = criarArquivo(
            "id,titulo,categoria,data_esperada,data_real\n" +
            "abc,Java Clean Code,Academico,2026-03-20,2026-03-25\n"
        );

        IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class,
            () -> leitor.ler(arquivo.toString())
        );

        assertTrue(excecao.getMessage().contains("erro ao interpretar a linha"));
    }

    @Test
    void deveLancarExcecaoQuandoDataEhInvalida() throws Exception {
        Path arquivo = criarArquivo(
            "id,titulo,categoria,data_esperada,data_real\n" +
            "01,Java Clean Code,Academico,data-invalida,2026-03-25\n"
        );

        IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class,
            () -> leitor.ler(arquivo.toString())
        );

        assertTrue(excecao.getMessage().contains("erro ao interpretar a linha"));
    }

    @Test
    void deveLancarRuntimeExceptionQuandoArquivoNaoExiste() {
        String caminhoInexistente = diretorioTemporario.resolve("emprestimos_inexistente.csv").toString();

        RuntimeException excecao = assertThrows(
            RuntimeException.class,
            () -> leitor.ler(caminhoInexistente)
        );

        assertTrue(excecao.getMessage().contains("erro ao ler arquivo de emprestimos"));
        assertTrue(excecao.getMessage().contains(caminhoInexistente));
    }

    @Test
    void deveImplementarInterfaceLeitorEmprestimos() {
        LeitorEmprestimos leitorInterface = new LeitorEmprestimosCSV();
        assertTrue(leitorInterface instanceof LeitorEmprestimosCSV);
    }

    private Path criarArquivo(String conteudo) throws Exception {
        Path arquivo = diretorioTemporario.resolve("emprestimos.csv");
        Files.write(arquivo, conteudo.getBytes(StandardCharsets.UTF_8));
        return arquivo;
    }
}
