package br.edu.ufrgs.controller;

import br.edu.ufrgs.io.ExportadorCSV;
import br.edu.ufrgs.model.Emprestimo;
import br.edu.ufrgs.service.ProcessadorBiblioteca;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

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
        if ("/exportar".equals(path)) {
            exportarCsv(request, response);
            return;
        }

        try {
            Part fileEmprestimos = request.getPart("emprestimos");
            Part fileConfig = request.getPart("config");

            if (fileConfig == null || fileConfig.getSize() == 0) {
                throw new IllegalArgumentException(
                    "O arquivo de configuração de taxas (config_biblioteca.csv) é obrigatório."
                );
            }

            ProcessadorBiblioteca processador = new ProcessadorBiblioteca();

            Path tempConfig = salvarArquivoTemporario(fileConfig, "config-");
            try {
                processador.carregarConfiguracao(tempConfig.toAbsolutePath().toString());
            } finally {
                Files.deleteIfExists(tempConfig);
            }

            List<Emprestimo> emprestimos = new ArrayList<>();
            if (fileEmprestimos != null && fileEmprestimos.getSize() > 0) {
                Path tempEmprestimos = salvarArquivoTemporario(fileEmprestimos, "emprestimos-");
                try {
                    emprestimos = processador.processar(tempEmprestimos.toAbsolutePath().toString());
                } finally {
                    Files.deleteIfExists(tempEmprestimos);
                }
            }

            int totalAtrasados = 0;
            double totalMultas = 0.0;

            for (Emprestimo emprestimo : emprestimos) {
                if (emprestimo.getDiasAtraso() > 0) {
                    totalAtrasados++;
                }
                totalMultas += emprestimo.getValorMulta();
            }

            request.setAttribute("emprestimos", emprestimos);
            request.setAttribute("totalProcessados", emprestimos.size());
            request.setAttribute("totalAtrasados", totalAtrasados);
            request.setAttribute("totalMultas", totalMultas);
            request.setAttribute("processado", true);

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
        if ("/exportar".equals(path)) {
            exportarCsv(request, response);
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    @SuppressWarnings("unchecked")
    private void exportarCsv(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        List<Emprestimo> emprestimos =
            (List<Emprestimo>) request.getSession().getAttribute("emprestimosProcessados");

        if (emprestimos == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nenhum dado processado.");
            return;
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"multas_processadas.csv\"");
        response.setCharacterEncoding("UTF-8");

        ExportadorCSV exportador = new ExportadorCSV();
        exportador.exportarParaResposta(response.getWriter(), emprestimos);
    }

    private Path salvarArquivoTemporario(Part arquivo, String prefixo) throws IOException {
        Path temp = Files.createTempFile(prefixo, ".csv");
        try (InputStream is = arquivo.getInputStream()) {
            Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
        }
        return temp;
    }
}
