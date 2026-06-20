package br.edu.ufrgs.controler;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import br.edu.ufrgs.controller.SmartLibraryServlet;
import br.edu.ufrgs.model.Emprestimo;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControlerTest {

    private SmartLibraryServlet servlet;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;
    @Mock private Part mockConfigPart;
    @Mock private Part mockEmprestimosPart;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new SmartLibraryServlet();
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher("index.jsp")).thenReturn(dispatcher);
    }


    // Valida o redirecionamento de rota interna do POST para a exportação
    @Test
    public void testaExportar() throws Exception {
        when(request.getServletPath()).thenReturn("/exportar");
        when(request.getMethod()).thenReturn("POST");
        
        List<Emprestimo> listaSimulada = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        listaSimulada.add(new Emprestimo(1, "Dom Casmurro", "Academico", hoje, hoje));
        
        when(session.getAttribute("emprestimosProcessados")).thenReturn(listaSimulada);
        
        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        servlet.service(request, response);

        verify(response).setContentType("text/csv");
        verify(response).setHeader(eq("Content-Disposition"), anyString());
    }

    // Valida o tratamento de erro quando o arquivo obrigatório de configuração falta
    @Test
    public void testaCOnfigAusente() throws Exception {
        when(request.getServletPath()).thenReturn("/processa");
        when(request.getMethod()).thenReturn("POST");
        when(request.getPart("config")).thenReturn(null);

        servlet.service(request, response);

        verify(request).setAttribute(eq("erro"), contains("O arquivo de configuração de taxas"));
        verify(dispatcher).forward(request, response);
    }

    // Valida o fluxo completo e bem-sucedido de upload e processamento dos arquivos
    @Test
    public void testaProcessamentoArquivo() throws Exception {
        when(request.getServletPath()).thenReturn("/processa");
        when(request.getMethod()).thenReturn("POST");
        
        // Dados ajustados para usar a coluna correta (categoria,valor_diario) e o tipo Academico
        String dadosConfig = "categoria,valor_diario\nAcademico,2.00";
        when(mockConfigPart.getSize()).thenReturn((long) dadosConfig.length());
        when(mockConfigPart.getInputStream()).thenReturn(new ByteArrayInputStream(dadosConfig.getBytes()));
        when(request.getPart("config")).thenReturn(mockConfigPart);

        // Dados de empréstimo batendo com a categoria existente nas configurações
        String dadosEmprestimos = "id,titulo,categoria,dataPrevista,dataDevolucao\n1,O Alquimista,Academico,2026-05-01,2026-05-10";
        when(mockEmprestimosPart.getSize()).thenReturn((long) dadosEmprestimos.length());
        when(mockEmprestimosPart.getInputStream()).thenReturn(new ByteArrayInputStream(dadosEmprestimos.getBytes()));
        when(request.getPart("emprestimos")).thenReturn(mockEmprestimosPart);

        servlet.service(request, response);

        verify(request).setAttribute(eq("processado"), eq(true));
        verify(request).setAttribute(eq("totalProcessados"), anyInt());
        verify(session).setAttribute(eq("emprestimosProcessados"), anyList());
        verify(dispatcher).forward(request, response);
    }


    // Valida o comportamento padrão do GET enviando o usuário para a index
    @Test
    public void testaGET() throws Exception {
        when(request.getServletPath()).thenReturn("/processa");
        when(request.getMethod()).thenReturn("GET");

        servlet.service(request, response);

        verify(response).sendRedirect("index.jsp");
    }

    // Valida o fluxo de download do CSV através da rota de exportação do GET
    @Test
    public void testaCSVExport() throws Exception {
        when(request.getServletPath()).thenReturn("/exportar");
        when(request.getMethod()).thenReturn("GET");
        
        List<Emprestimo> listaSimulada = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        listaSimulada.add(new Emprestimo(1, "Dom Casmurro", "Academico", hoje, hoje));
        
        when(session.getAttribute("emprestimosProcessados")).thenReturn(listaSimulada);

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        servlet.service(request, response);

        verify(response).setContentType("text/csv");
    }

    // Valida a proteção contra tentativas de exportação sem dados prévios na sessão
    @Test
    public void testaErro() throws Exception {
        when(request.getServletPath()).thenReturn("/exportar");
        when(request.getMethod()).thenReturn("GET");
        when(session.getAttribute("emprestimosProcessados")).thenReturn(null);

        servlet.service(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Nenhum dado processado.");
    }
}
