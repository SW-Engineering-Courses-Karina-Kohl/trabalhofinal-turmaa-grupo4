package br.edu.ufrgs.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfiguracaoMultasTest {

    @TempDir
    Path diretorioTemporario;

    @Test
    void deveCarregarValoresPorCategoria() throws Exception {
        Path arquivo = criarArquivoConfig(
            "categoria,valor_diario\n" +
            "Academico,2.00\n" +
            "Comum,1.00\n" +
            "Raro,10.00\n"
        );

        ConfiguracaoMultas config = new ConfiguracaoMultas();
        config.carregar(arquivo.toString());

        assertEquals(2.0, config.getValorBase("Academico"));
        assertEquals(1.0, config.getValorBase("Comum"));
        assertEquals(10.0, config.getValorBase("Raro"));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoExiste() {
        ConfiguracaoMultas config = new ConfiguracaoMultas();

        IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class,
            () -> config.getValorBase("NaoExiste")
        );

        assertEquals("categoria nao encontrada na configuracao: NaoExiste", excecao.getMessage());
    }

    @Test
    void deveIgnorarLinhasVaziasNoArquivo() throws Exception {
        Path arquivo = criarArquivoConfig(
            "categoria,valor_diario\n" +
            "\n" +
            "Academico,2.00\n" +
            "   \n" +
            "Comum,1.00\n"
        );

        ConfiguracaoMultas config = new ConfiguracaoMultas();
        config.carregar(arquivo.toString());

        assertEquals(2.0, config.getValorBase("Academico"));
        assertEquals(1.0, config.getValorBase("Comum"));
    }

    @Test
    void deveRemoverEspacosNasColunas() throws Exception {
        Path arquivo = criarArquivoConfig(
            "categoria,valor_diario\n" +
            " Academico , 2.50 \n"
        );

        ConfiguracaoMultas config = new ConfiguracaoMultas();
        config.carregar(arquivo.toString());

        assertEquals(2.5, config.getValorBase("Academico"));
    }

    @Test
    void deveCarregarArquivoApenasComCabecalho() throws Exception {
        Path arquivo = criarArquivoConfig("categoria,valor_diario\n");

        ConfiguracaoMultas config = new ConfiguracaoMultas();
        config.carregar(arquivo.toString());

        assertThrows(IllegalArgumentException.class, () -> config.getValorBase("Academico"));
    }

    @Test
    void deveLancarExcecaoQuandoLinhaPossuiColunasInvalidas() throws Exception {
        Path arquivo = criarArquivoConfig(
            "categoria,valor_diario\n" +
            "Academico,2.00,extra\n"
        );

        ConfiguracaoMultas config = new ConfiguracaoMultas();

        IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class,
            () -> config.carregar(arquivo.toString())
        );

        assertTrue(excecao.getMessage().contains("linha invalida no arquivo de configuracao"));
    }

    @Test
    void deveLancarExcecaoQuandoLinhaPossuiApenasUmaColuna() throws Exception {
        Path arquivo = criarArquivoConfig(
            "categoria,valor_diario\n" +
            "Academico\n"
        );

        ConfiguracaoMultas config = new ConfiguracaoMultas();

        assertThrows(IllegalArgumentException.class, () -> config.carregar(arquivo.toString()));
    }

    @Test
    void deveLancarRuntimeExceptionQuandoArquivoNaoExiste() {
        ConfiguracaoMultas config = new ConfiguracaoMultas();
        String caminhoInexistente = diretorioTemporario.resolve("config_inexistente.csv").toString();

        RuntimeException excecao = assertThrows(
            RuntimeException.class,
            () -> config.carregar(caminhoInexistente)
        );

        assertTrue(excecao.getMessage().contains("erro ao ler arquivo de configuracao"));
        assertTrue(excecao.getMessage().contains(caminhoInexistente));
    }

    @Test
    void deveLancarExcecaoQuandoValorNaoEhNumerico() throws Exception {
        Path arquivo = criarArquivoConfig(
            "categoria,valor_diario\n" +
            "Academico,dois\n"
        );

        ConfiguracaoMultas config = new ConfiguracaoMultas();

        assertThrows(NumberFormatException.class, () -> config.carregar(arquivo.toString()));
    }

    @Test
    void deveSobrescreverValorQuandoCategoriaApareceMaisDeUmaVez() throws Exception {
        Path arquivo = criarArquivoConfig(
            "categoria,valor_diario\n" +
            "Academico,2.00\n" +
            "Academico,3.50\n"
        );

        ConfiguracaoMultas config = new ConfiguracaoMultas();
        config.carregar(arquivo.toString());

        assertEquals(3.5, config.getValorBase("Academico"));
    }

    private Path criarArquivoConfig(String conteudo) throws Exception {
        Path arquivo = diretorioTemporario.resolve("config_biblioteca.csv");
        Files.write(arquivo, conteudo.getBytes(StandardCharsets.UTF_8));
        return arquivo;
    }
}
