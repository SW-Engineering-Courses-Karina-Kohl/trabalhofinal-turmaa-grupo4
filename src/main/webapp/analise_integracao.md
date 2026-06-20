# Análise de Integração: Front-End e Back-End

Esta análise detalha a compatibilidade e a integração entre a interface do usuário (front-end em JSP) e os serviços e modelos de negócios desenvolvidos no back-end (Java Servlets, Serviços e I/O).

---

## Visão Geral da Arquitetura

O sistema foi estruturado de forma a separar estritamente a interface de usuário (visualização) das regras de cálculo de multas e processamento de arquivos (lógica de negócio):

1. **Front-End (`index.jsp`):**
   - Atua puramente como camada de apresentação.
   - Não realiza nenhum cálculo de multa, contagem de atrasos ou leitura/gravação direta de arquivos CSV.
   - Apresenta os dados dinamicamente a partir dos atributos de requisição enviados pelo Servlet.

2. **Controller (`SmartLibraryServlet.java`):**
   - Recebe as requisições HTTP POST (`/processa`) e GET (`/exportar`).
   - Gerencia a recepção e o armazenamento temporário dos arquivos de entrada (`emprestimos.csv` e `config_biblioteca.csv`).
   - Delega a lógica ao processador central do back-end e encaminha os resultados ao JSP.

3. **Lógica de Negócio (`ProcessadorBiblioteca.java` e colaboradores):**
   - **`ProcessadorBiblioteca`:** Centraliza a coordenação das operações.
   - **`LeitorEmprestimosCSV`** (via interface `LeitorEmprestimos`): Realiza o parse das linhas do arquivo de empréstimos.
   - **`ConfiguracaoMultas`**: Carrega e expõe os valores diários das taxas para cada categoria de livro.
   - **`CalculadoraMultas`**: Contém a fórmula de cálculo da multa com base nos dias atrasados e categoria.
   - **`ExportadorCSV`** (via interface `ExportadorResultados`): Formata e gera o arquivo CSV de saída.

---

## Detalhamento da Integração de Classes

Abaixo está o mapeamento de como as classes do back-end são utilizadas para alimentar a interface do usuário:

| Componente na JSP | Origem dos Dados no Back-End | Classe/Método Utilizado |
| :--- | :--- | :--- |
| **Total de Livros** | Atributo `totalProcessados` | Tamanho da lista retornada por `ProcessadorBiblioteca.processar()` |
| **Livros em Atraso** | Atributo `totalAtrasados` | `ProcessadorBiblioteca.contarAtrasados()` |
| **Valor Total de Multas**| Atributo `totalMultas` | `ProcessadorBiblioteca.somarMultas()` |
| **Tabela de Devoluções** | Atributo `emprestimos` (Lista) | Iteração sobre objetos `Emprestimo` |
| **ID do Empréstimo** | Coluna "ID" na tabela | `Emprestimo.getId()` (formatado com `%02d`) |
| **Título do Livro** | Coluna "Título" na tabela | `Emprestimo.getTitulo()` |
| **Categoria** | Badge de Categoria | `Emprestimo.getCategoria()` |
| **Data Esperada** | Coluna "Data Esperada" | `Emprestimo.getDataPrevista()` |
| **Data Real** | Coluna "Data Real" | `Emprestimo.getDataDevolucao()` |
| **Dias de Atraso** | Coluna "Dias de Atraso" | `Emprestimo.getDiasAtraso()` |
| **Valor da Multa** | Coluna "Valor da Multa" | `Emprestimo.getValorMulta()` |
