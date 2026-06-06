package br.edu.ufrgs.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// classe responsavel por ler o arquivo csv de configuracao das multas
// e armazenar o valor base de multa para cada categoria
public class ConfiguracaoMultas {
    // mapa que associa categoria com o valor base da multa diaria
    private Map<String, Double> valoresBase;

    // construtor da classe
    public ConfiguracaoMultas() {
        this.valoresBase = new HashMap<>();
    }

    // metodo que le o arquivo csv de configuracao e preenche o mapa de multas
    public void carregar(String caminhoArquivo) {
        try {
            // le todas as linhas do arquivo
            List<String> linhas = Files.readAllLines(Path.of(caminhoArquivo));

            // percorre o arquivo a partir da segunda linha
            // comeca em 1 para ignorar o cabecalho
            for (int i = 1; i < linhas.size(); i++) {
                String linha = linhas.get(i).trim();

                // ignora linhas vazias
                if (linha.isEmpty()) {
                    continue;
                }

                // separa os as partes da linha pela virgula (categoria e valor)
                String[] partes = linha.split(",");

                // verifica se a linha tem exatamente 2 colunas
                // categoria e valor diario
                if (partes.length != 2) {
                    throw new IllegalArgumentException(
                        "linha invalida no arquivo de configuracao: " + linha
                    );
                }

                // extrai e limpa os dados da linha
                String categoria = partes[0].trim();
                double valorBase = Double.parseDouble(partes[1].trim());

                // salva no mapa a categoria com o valor base
                valoresBase.put(categoria, valorBase);
            }

        } catch (IOException e) {
            // erro caso o arquivo nao possa ser lido
            throw new RuntimeException(
                "erro ao ler arquivo de configuracao: " + caminhoArquivo,
                e
            );
        }
    }

    // metodo que retorna o valor base da multa para uma categoria
    public double getValorBase(String categoria) {
        // verifica se a categoria existe no mapa
        if (!valoresBase.containsKey(categoria)) {
            throw new IllegalArgumentException(
                "categoria nao encontrada na configuracao: " + categoria
            );
        }

        // retorna o valor da categoria caso ela exista
        return valoresBase.get(categoria);
    }
}