package br.edu.ufrgs.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import br.edu.ufrgs.model.Emprestimo;
import br.edu.ufrgs.service.ProcessadorBiblioteca;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

// servlet principal da aplicacao
// recebe os arquivos enviados, chama o processamento e encaminha o resultado para a jsp
@WebServlet(urlPatterns = {"/processa", "/exportar"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class SmartLibraryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // se a requisicao for de exportacao, chama direto o metodo de exportar csv
        if ("/exportar".equals(path)) {
            exportarCsv(request, response);
            return;
        }

        try {
            Part fileEmprestimos = request.getPart("emprestimos");
            Part fileConfig = request.getPart("config");

            // o arquivo de configuracao e obrigatorio para calcular as multas
            if (fileConfig == null || fileConfig.getSize() == 0) {
                throw new IllegalArgumentException(
                    "O arquivo de configuração de taxas (config_biblioteca.csv) é obrigatório."
                );
            }
            
            // o arquivo de emprestimos tambem e obrigatorio para o processamento
            if (fileEmprestimos == null || fileEmprestimos.getSize() == 0) {
                throw new IllegalArgumentException(
                    "O arquivo de empréstimos (emprestimos.csv) é obrigatório."
                );
            }

            // cria o processador responsavel por coordenar a leitura e o calculo
            ProcessadorBiblioteca processador = new ProcessadorBiblioteca();

            // salva temporariamente o csv de configuracao e carrega as multas
            Path tempConfig = salvarArquivoTemporario(fileConfig, "config-");
            try {
                processador.carregarConfiguracao(tempConfig.toAbsolutePath().toString());
            } finally {
                Files.deleteIfExists(tempConfig);
            }

            // processa os emprestimos enviados no csv principal
            List<Emprestimo> emprestimos;
            Path tempEmprestimos = salvarArquivoTemporario(fileEmprestimos, "emprestimos-");

            try {
                emprestimos = processador.processar(tempEmprestimos.toAbsolutePath().toString());
            } finally {
                Files.deleteIfExists(tempEmprestimos);
            }

            // pega os totais a partir do processador
            int totalAtrasados = processador.contarAtrasados(emprestimos);
            double totalMultas = processador.somarMultas(emprestimos);

            // envia os dados processados para a jsp
            request.setAttribute("emprestimos", emprestimos);
            request.setAttribute("totalProcessados", emprestimos.size());
            request.setAttribute("totalAtrasados", totalAtrasados);
            request.setAttribute("totalMultas", totalMultas);
            request.setAttribute("processado", true);

            // guarda os emprestimos processados na sessao para permitir a exportacao depois
            request.getSession().setAttribute("emprestimosProcessados", emprestimos);

        } catch (Exception e) {
            request.setAttribute("erro", "Erro ao processar: " + e.getMessage());
        }

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // se a requisicao for de exportacao, gera o csv; se nao, volta para a pagina inicial
        if ("/exportar".equals(path)) {
            exportarCsv(request, response);
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    @SuppressWarnings("unchecked")
    private void exportarCsv(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // recupera da sessao os emprestimos ja processados
        List<Emprestimo> emprestimos =
            (List<Emprestimo>) request.getSession().getAttribute("emprestimosProcessados");

        if (emprestimos == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nenhum dado processado.");
            return;
        }

        // configura a resposta http como download de csv
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"multas_processadas.csv\"");
        response.setCharacterEncoding("UTF-8");

        // usa o processador para fazer a exportacao
        ProcessadorBiblioteca processador = new ProcessadorBiblioteca();
        processador.exportarParaResposta(response.getWriter(), emprestimos);
    }

    // salva um arquivo enviado via multipart em um arquivo temporario
    private Path salvarArquivoTemporario(Part arquivo, String prefixo) throws IOException {
        Path temp = Files.createTempFile(prefixo, ".csv");
        try (InputStream is = arquivo.getInputStream()) {
            Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
        }
        return temp;
    }
}