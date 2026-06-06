package br.edu.ufrgs.model;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfiguracaoMultasTest {

    @Test
    void deveCarregarValoresPorCategoria() throws Exception {
        Path arquivoTemp = Files.createTempFile("config_biblioteca", ".csv");

        Files.write(
            arquivoTemp,
            (
                "categoria,valor_diario\n" +
                "Academico,2.00\n" +
                "Comum,1.00\n" +
                "Raro,10.00\n"
            ).getBytes(StandardCharsets.UTF_8)
        );

        ConfiguracaoMultas config = new ConfiguracaoMultas();
        config.carregar(arquivoTemp.toString());

        assertEquals(2.0, config.getValorBase("Academico"));
        assertEquals(1.0, config.getValorBase("Comum"));
        assertEquals(10.0, config.getValorBase("Raro"));
    }
}