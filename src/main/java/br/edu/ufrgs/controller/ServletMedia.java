package br.edu.ufrgs.controller;

import br.edu.ufrgs.model.CalculadoraMultas;
import br.edu.ufrgs.model.ConfiguracaoMultas;
import br.edu.ufrgs.model.Emprestimo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/processa", "/exportar"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class ServletMedia extends HttpServlet {

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

            ConfiguracaoMultas configuracao = new ConfiguracaoMultas();
            if (fileConfig == null || fileConfig.getSize() == 0) {
                throw new IllegalArgumentException("O arquivo de configuração de taxas (config_biblioteca.csv) é obrigatório.");
            }
            Path tempConfig = Files.createTempFile("config-", ".csv");
            try (InputStream is = fileConfig.getInputStream()) {
                Files.copy(is, tempConfig, StandardCopyOption.REPLACE_EXISTING);
            }
            configuracao.carregar(tempConfig.toAbsolutePath().toString());
            Files.deleteIfExists(tempConfig);


            List<Emprestimo> emprestimos = new ArrayList<>();
            if (fileEmprestimos != null && fileEmprestimos.getSize() > 0) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(fileEmprestimos.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    boolean firstLine = true;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        if (line.startsWith("\uFEFF")) {
                            line = line.substring(1);
                        }
                        String[] partes = line.split(",");
                        if (partes.length >= 5) {
                            String idStr = partes[0].trim();
                            if (firstLine && (idStr.equalsIgnoreCase("id") || idStr.equalsIgnoreCase("﻿id"))) {
                                firstLine = false;
                                continue;
                            }
                            try {
                                int id = Integer.parseInt(idStr);
                                String titulo = partes[1].trim();
                                String categoria = partes[2].trim();
                                LocalDate dataEsperada = LocalDate.parse(partes[3].trim());
                                LocalDate dataReal = LocalDate.parse(partes[4].trim());
                                emprestimos.add(new Emprestimo(id, titulo, categoria, dataEsperada, dataReal));
                            } catch (Exception e) {
                                // ignora erros de parse
                            }
                        }
                        firstLine = false;
                    }
                }
            }

            CalculadoraMultas calculadora = new CalculadoraMultas(configuracao);
            int totalAtrasados = 0;
            double totalMultas = 0.0;

            for (Emprestimo e : emprestimos) {
                double multa = calculadora.processarMulta(e);
                e.setValorMulta(multa);
                if (e.getDiasAtraso() > 0) {
                    totalAtrasados++;
                }
                totalMultas += multa;
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
        List<Emprestimo> emprestimos = (List<Emprestimo>) request.getSession().getAttribute("emprestimosProcessados");
        if (emprestimos == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nenhum dado processado.");
            return;
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"multas_processadas.csv\"");
        response.setCharacterEncoding("UTF-8");

        try (java.io.PrintWriter writer = response.getWriter()) {
            writer.println("id,titulo,categoria,data_esperada,data_real,valor_multa");
            for (Emprestimo e : emprestimos) {
                writer.println(String.format("%d,%s,%s,%s,%s,%.2f",
                    e.getId(),
                    e.getTitulo().contains(",") ? "\"" + e.getTitulo() + "\"" : e.getTitulo(),
                    e.getCategoria(),
                    e.getDataPrevista().toString(),
                    e.getDataDevolucao().toString(),
                    e.getValorMulta()
                ));
            }
        }
    }
}