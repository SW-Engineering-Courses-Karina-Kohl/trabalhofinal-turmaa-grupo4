<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="br.edu.ufrgs.model.Emprestimo" %>
<!DOCTYPE html>
<html class="light" lang="pt-BR">
<head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>BiblioTech - Sistema de Devoluções e Multas</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&amp;family=Outfit:wght@500;600&amp;display=swap" rel="stylesheet"/>
    <script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "status-success": "#10B981",
                        "on-secondary-fixed": "#0b1c30",
                        "primary-container": "#2563eb",
                        "secondary-fixed": "#d3e4fe",
                        "tertiary-container": "#bc4800",
                        "surface-container-high": "#e2e7ff",
                        "on-surface-variant": "#434655",
                        "error": "#ba1a1a",
                        "surface-container-highest": "#dae2fd",
                        "on-tertiary-fixed": "#360f00",
                        "status-warning": "#F59E0B",
                        "outline": "#737686",
                        "tertiary": "#943700",
                        "on-background": "#131b2e",
                        "background": "#faf8ff",
                        "primary": "#004ac6",
                        "outline-variant": "#c3c6d7",
                        "surface": "#faf8ff",
                        "on-tertiary": "#ffffff",
                        "secondary-container": "#d0e1fb",
                        "on-primary-fixed-variant": "#003ea8",
                        "on-primary-fixed": "#00174b",
                        "on-primary-container": "#eeefff",
                        "surface-bright": "#faf8ff",
                        "surface-container-low": "#f2f3ff",
                        "surface-container": "#eaedff",
                        "inverse-on-surface": "#eef0ff",
                        "category-rare": "#7C3AED",
                        "secondary": "#505f76",
                        "on-tertiary-container": "#ffede6",
                        "primary-fixed-dim": "#b4c5ff",
                        "on-secondary-fixed-variant": "#38485d",
                        "on-secondary-container": "#54647a",
                        "surface-container-lowest": "#ffffff",
                        "surface-background": "#F8FAFC",
                        "inverse-surface": "#283044",
                        "on-error-container": "#93000a",
                        "on-primary": "#ffffff",
                        "surface-variant": "#dae2fd",
                        "surface-tint": "#0053db",
                        "on-error": "#ffffff",
                        "surface-dim": "#d2d9f4",
                        "error-container": "#ffdad6",
                        "primary-fixed": "#dbe1ff",
                        "status-error": "#EF4444",
                        "secondary-fixed-dim": "#b7c8e1",
                        "surface-border": "#E2E8F0",
                        "category-academic": "#0891B2",
                        "tertiary-fixed": "#ffdbcd",
                        "on-secondary": "#ffffff",
                        "on-tertiary-fixed-variant": "#7d2d00",
                        "on-surface": "#131b2e",
                        "inverse-primary": "#b4c5ff",
                        "tertiary-fixed-dim": "#ffb596"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.125rem",
                        "lg": "0.25rem",
                        "xl": "0.5rem",
                        "full": "0.75rem"
                    },
                    "spacing": {
                        "lg": "1.5rem",
                        "gutter": "1.5rem",
                        "xl": "2.5rem",
                        "md": "1rem",
                        "container-max": "1280px",
                        "sm": "0.5rem",
                        "base": "4px",
                        "xs": "0.25rem"
                    },
                    "fontFamily": {
                        "headline-sm": ["Outfit"],
                        "body-lg": ["Inter"],
                        "label-md": ["Inter"],
                        "mono-data": ["Inter"],
                        "headline-lg": ["Outfit"],
                        "headline-md": ["Outfit"],
                        "body-md": ["Inter"]
                    },
                    "fontSize": {
                        "headline-sm": ["18px", {"lineHeight": "1.4", "fontWeight": "500"}],
                        "body-lg": ["16px", {"lineHeight": "1.6", "fontWeight": "400"}],
                        "label-md": ["12px", {"lineHeight": "1", "letterSpacing": "0.05em", "fontWeight": "600"}],
                        "mono-data": ["13px", {"lineHeight": "1.2", "letterSpacing": "-0.01em", "fontWeight": "500"}],
                        "headline-lg": ["32px", {"lineHeight": "1.2", "letterSpacing": "-0.02em", "fontWeight": "600"}],
                        "headline-md": ["24px", {"lineHeight": "1.3", "fontWeight": "600"}],
                        "body-md": ["14px", {"lineHeight": "1.5", "fontWeight": "400"}]
                    }
                }
            }
        }
    </script>
</head>
<body class="bg-surface-background text-on-surface font-body-md min-h-screen">

    <!-- SideNavBar -->
    <nav class="bg-surface-container-lowest text-primary font-body-md w-[260px] h-screen fixed left-0 top-0 border-r border-surface-border flex flex-col py-xl px-md z-20">
        <div class="mb-xl px-sm">
            <h1 class="font-headline-md text-headline-md font-semibold text-primary mb-xs">Painel Administrativo</h1>
            <p class="font-label-md text-label-md text-secondary">Controlador da Biblioteca</p>
        </div>
        <div class="flex-1 space-y-sm">
            <a class="flex items-center gap-md px-sm py-sm rounded-lg text-primary font-semibold border-r-4 border-primary bg-surface-container-low transition-colors duration-200 cursor-pointer active:scale-95" href="index.jsp">
                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">upload_file</span>
                <span>Carregar CSV</span>
            </a>

        </div>
    </nav>

    <!-- TopNavBar -->
    <header class="bg-surface-container-lowest text-primary font-headline-md h-16 fixed top-0 right-0 left-[260px] border-b border-surface-border flex justify-between items-center px-xl z-10">
        <div class="flex items-center gap-md">
            <!-- Icone de Livro SVG como logo -->
            <svg class="w-6 h-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
            </svg>
            <span class="font-headline-md text-headline-md font-bold text-on-surface cursor-pointer">BiblioTech</span>
        </div>
        <div class="flex items-center gap-lg">

            <div class="w-8 h-8 rounded-full bg-primary text-on-primary overflow-hidden cursor-pointer border border-surface-border flex items-center justify-center font-bold">
                U
            </div>
        </div>
    </header>

    <!-- Main Content Canvas -->
    <main class="ml-[260px] pt-24 px-xl pb-xl max-w-container-max mx-auto">
        
        <% if (request.getAttribute("erro") != null) { %>
            <div class="mb-lg p-md rounded-xl bg-error-container text-error border border-error/20 font-medium flex items-center gap-sm">
                <span class="material-symbols-outlined">error</span>
                <span><%= request.getAttribute("erro") %></span>
            </div>
        <% } %>

        <form id="upload-form" action="processa" method="POST" enctype="multipart/form-data">
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-lg mb-xl">
                <!-- Upload Zone -->
                <div class="col-span-2 bg-surface-container-lowest border border-surface-border rounded-xl p-lg flex flex-col">
                    <h2 class="font-headline-sm text-headline-sm text-on-surface mb-md">Upload e Processamento</h2>
                    
                    <div class="flex gap-lg flex-1">
                        <!-- Dropzone de Empréstimos -->
                        <div id="dropzone-emprestimos" class="flex-1 border-2 border-dashed border-primary-fixed-dim rounded-lg flex flex-col items-center justify-center p-xl hover:bg-surface-container-low transition-colors cursor-pointer group">
                            <span class="material-symbols-outlined text-[48px] text-primary mb-md group-hover:scale-110 transition-transform">cloud_upload</span>
                            <p id="label-emprestimos" class="font-body-md text-body-md text-on-surface font-medium text-center mb-xs">Arraste e solte 'emprestimos.csv'</p>
                            <p class="font-label-md text-label-md text-secondary text-center">ou clique para navegar</p>
                            <input type="file" name="emprestimos" id="file-emprestimos" accept=".csv" class="hidden" required/>
                        </div>
                        
                        <!-- Lado Direito: Arquivo de Configuração e Botão -->
                        <div class="w-1/3 flex flex-col gap-md">
                            <div id="btn-select-config" class="bg-surface-background border border-surface-border rounded-lg p-md cursor-pointer hover:bg-surface-container transition-colors">
                                <label class="font-label-md text-label-md text-on-secondary-container block mb-sm cursor-pointer">Taxas Diárias (Obrigatório)</label>
                                <div class="flex items-center gap-sm bg-surface-container-lowest border border-outline-variant rounded p-xs">
                                    <span class="material-symbols-outlined text-secondary text-[18px]">description</span>
                                    <span id="label-config" class="font-body-md text-body-md text-secondary truncate flex-1">Clique para selecionar</span>
                                    <span class="material-symbols-outlined text-[18px] text-secondary">edit</span>
                                </div>
                                <input type="file" name="config" id="file-config" accept=".csv" class="hidden" required/>
                            </div>
                            
                            <button type="submit" class="mt-auto w-full bg-primary text-on-primary font-label-md text-label-md h-12 rounded-lg hover:bg-on-primary-fixed-variant transition-all flex items-center justify-center gap-sm shadow-sm active:scale-95">
                                <span class="material-symbols-outlined text-[18px]">play_arrow</span>
                                <span>Processar Devoluções</span>
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Metrics Dashboard -->
                <% 
                    int totalProcessados = request.getAttribute("totalProcessados") != null ? (Integer)request.getAttribute("totalProcessados") : 0;
                    int totalAtrasados = request.getAttribute("totalAtrasados") != null ? (Integer)request.getAttribute("totalAtrasados") : 0;
                    double totalMultas = request.getAttribute("totalMultas") != null ? (Double)request.getAttribute("totalMultas") : 0.0;
                %>
                <div class="col-span-1 flex flex-col gap-md">
                    <!-- Total Livros -->
                    <div class="bg-surface-container-lowest border border-surface-border rounded-xl p-md flex items-center gap-md relative overflow-hidden">
                        <div class="absolute left-0 top-0 bottom-0 w-1 bg-primary"></div>
                        <div class="w-10 h-10 rounded-full bg-surface-container-low flex items-center justify-center text-primary">
                            <span class="material-symbols-outlined">library_books</span>
                        </div>
                        <div>
                            <p class="font-label-md text-label-md text-secondary mb-xs">Total de Livros</p>
                            <p class="font-headline-lg text-headline-lg text-on-surface font-semibold"><%= String.format("%02d", totalProcessados) %></p>
                        </div>
                    </div>
                    <!-- Atrasados -->
                    <div class="bg-surface-container-lowest border border-surface-border rounded-xl p-md flex items-center gap-md relative overflow-hidden">
                        <div class="absolute left-0 top-0 bottom-0 w-1 bg-status-warning"></div>
                        <div class="w-10 h-10 rounded-full bg-error-container flex items-center justify-center text-status-warning">
                            <span class="material-symbols-outlined">schedule</span>
                        </div>
                        <div>
                            <p class="font-label-md text-label-md text-secondary mb-xs">Livros em Atraso</p>
                            <p class="font-headline-lg text-headline-lg text-on-surface font-semibold"><%= totalAtrasados %></p>
                        </div>
                    </div>
                    <!-- Total Multa -->
                    <div class="bg-surface-container-lowest border border-surface-border rounded-xl p-md flex items-center gap-md relative overflow-hidden">
                        <div class="absolute left-0 top-0 bottom-0 w-1 bg-error"></div>
                        <div class="w-10 h-10 rounded-full bg-error-container flex items-center justify-center text-error">
                            <span class="material-symbols-outlined">payments</span>
                        </div>
                        <div>
                            <p class="font-label-md text-label-md text-secondary mb-xs">Valor Total de Multas</p>
                            <p class="font-headline-lg text-headline-lg text-on-surface font-semibold">R$ <%= String.format("%.2f", totalMultas) %></p>
                        </div>
                    </div>
                </div>
            </div>
        </form>

        <!-- Data Table -->
        <div class="bg-surface-container-lowest border border-surface-border rounded-xl overflow-hidden flex flex-col">
            <div class="p-md border-b border-surface-border flex justify-between items-center bg-surface-background">
                <h2 class="font-headline-sm text-headline-sm text-on-surface">Resultados de Processamento</h2>
                
                <% if (request.getAttribute("processado") != null) { %>
                    <a href="exportar" class="bg-surface-container-lowest border border-surface-border text-on-surface-variant font-label-md text-label-md h-8 px-md rounded flex items-center gap-sm hover:bg-surface-container transition-colors shadow-sm cursor-pointer">
                        <span class="material-symbols-outlined text-[16px]">download</span>
                        <span>Exportar CSV Final</span>
                    </a>
                <% } else { %>
                    <button disabled class="bg-surface-container-low border border-surface-border text-secondary/50 font-label-md text-label-md h-8 px-md rounded flex items-center gap-sm cursor-not-allowed shadow-none">
                        <span class="material-symbols-outlined text-[16px]">download</span>
                        <span>Exportar CSV Final</span>
                    </button>
                <% } %>
            </div>
            
            <div class="overflow-x-auto">
                <table class="w-full text-left border-collapse">
                    <thead>
                        <tr class="border-b-2 border-surface-border font-label-md text-label-md text-secondary">
                            <th class="py-sm px-md font-semibold">ID</th>
                            <th class="py-sm px-md font-semibold">Título</th>
                            <th class="py-sm px-md font-semibold">Categoria</th>
                            <th class="py-sm px-md font-semibold">Data Esperada</th>
                            <th class="py-sm px-md font-semibold">Data Real</th>
                            <th class="py-sm px-md font-semibold text-right">Dias de Atraso</th>
                            <th class="py-sm px-md font-semibold text-right">Valor da Multa</th>
                        </tr>
                    </thead>
                    <tbody class="font-mono-data text-mono-data text-on-surface">
                        <% 
                            List<Emprestimo> emprestimos = (List<Emprestimo>) request.getAttribute("emprestimos");
                            if (emprestimos != null && !emprestimos.isEmpty()) {
                                int count = 0;
                                for (Emprestimo e : emprestimos) {
                                    count++;
                                    String rowBg = (count % 2 == 0) ? "bg-surface-background" : "";
                                    
                                    // Determina badges de categoria baseadas na especificação
                                    String badgeClass = "bg-surface-container text-secondary"; // default Comum
                                    String cat = e.getCategoria();
                                    if (cat != null) {
                                        if (cat.equalsIgnoreCase("Academico") || cat.equalsIgnoreCase("Acadêmico")) {
                                            badgeClass = "bg-secondary-container text-category-academic";
                                        } else if (cat.equalsIgnoreCase("Raro")) {
                                            badgeClass = "bg-surface-variant text-category-rare";
                                        }
                                    }
                                    
                                    // Cores para dias de atraso e multas
                                    String atrasoColor = e.getDiasAtraso() > 0 ? "text-status-warning" : "text-status-success";
                                    String multaColor = e.getValorMulta() > 0 ? "text-error font-semibold" : "text-status-success";
                        %>
                                    <tr class="border-b border-surface-border hover:bg-surface-container-lowest transition-colors <%= rowBg %>">
                                        <td class="py-md px-md"><%= String.format("%02d", e.getId()) %></td>
                                        <td class="py-md px-md font-medium text-primary"><%= e.getTitulo() %></td>
                                        <td class="py-md px-md">
                                            <span class="inline-flex items-center px-2 py-1 rounded <%= badgeClass %> text-[10px] uppercase font-bold tracking-wider">
                                                <%= cat %>
                                            </span>
                                        </td>
                                        <td class="py-md px-md text-secondary"><%= e.getDataPrevista() %></td>
                                        <td class="py-md px-md text-secondary"><%= e.getDataDevolucao() %></td>
                                        <td class="py-md px-md text-right <%= atrasoColor %> font-semibold"><%= e.getDiasAtraso() %></td>
                                        <td class="py-md px-md text-right <%= multaColor %>">R$ <%= String.format("%.2f", e.getValorMulta()) %></td>
                                    </tr>
                        <% 
                                } 
                            } else { 
                        %>
                            <tr class="border-b border-surface-border hover:bg-surface-container-lowest transition-colors bg-surface-background">
                                <td colspan="7" class="py-lg text-center text-secondary font-medium font-body-md">
                                    Nenhum resultado processado. Faça o upload dos arquivos CSV acima para começar.
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            
            <div class="p-xs bg-surface-background border-t border-surface-border">
                <div class="h-2 rounded-full overflow-hidden bg-surface-container">
                    <div class="h-full bg-gradient-to-r from-primary to-primary-container w-[100%]"></div>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="bg-surface-container-lowest text-primary font-label-md h-12 fixed bottom-0 right-0 left-[260px] border-t border-surface-border flex items-center justify-between px-xl z-10">
        <span class="font-label-md text-label-md text-on-surface-variant cursor-default">© 2026 BiblioTech Data Systems</span>
        <div class="flex gap-lg">
            <% if (request.getAttribute("processado") != null) { %>
                <a class="text-on-secondary-container hover:underline hover:text-primary" href="exportar">Exportar CSV</a>
            <% } else { %>
                <span class="text-secondary/50 cursor-not-allowed">Exportar CSV</span>
            <% } %>
        </div>
    </footer>

    <!-- Scripts para Drag & Drop e Seleção de Arquivos -->
    <script>
        const dropzoneEmprestimos = document.getElementById('dropzone-emprestimos');
        const fileEmprestimos = document.getElementById('file-emprestimos');
        const labelEmprestimos = document.getElementById('label-emprestimos');

        const btnSelectConfig = document.getElementById('btn-select-config');
        const fileConfig = document.getElementById('file-config');
        const labelConfig = document.getElementById('label-config');

        // Dropzone Click
        dropzoneEmprestimos.addEventListener('click', () => {
            fileEmprestimos.click();
        });

        // Config Area Click
        btnSelectConfig.addEventListener('click', (e) => {
            if (e.target.tagName !== 'INPUT') {
                fileConfig.click();
            }
        });

        // Change Emprestimos
        fileEmprestimos.addEventListener('change', () => {
            if (fileEmprestimos.files.length > 0) {
                labelEmprestimos.innerText = "Selecionado: " + fileEmprestimos.files[0].name;
                labelEmprestimos.classList.add("text-primary");
            }
        });

        // Change Config
        fileConfig.addEventListener('change', () => {
            if (fileConfig.files.length > 0) {
                labelConfig.innerText = fileConfig.files[0].name;
                labelConfig.classList.remove("text-secondary");
                labelConfig.classList.add("text-primary", "font-semibold");
            }
        });

        // Drag and drop events
        ['dragenter', 'dragover'].forEach(eventName => {
            dropzoneEmprestimos.addEventListener(eventName, (e) => {
                e.preventDefault();
                dropzoneEmprestimos.classList.add('bg-surface-container-high', 'border-primary');
            }, false);
        });

        ['dragleave', 'drop'].forEach(eventName => {
            dropzoneEmprestimos.addEventListener(eventName, (e) => {
                e.preventDefault();
                dropzoneEmprestimos.classList.remove('bg-surface-container-high', 'border-primary');
            }, false);
        });

        dropzoneEmprestimos.addEventListener('drop', (e) => {
            const dt = e.dataTransfer;
            const files = dt.files;
            if (files.length > 0 && files[0].name.endsWith('.csv')) {
                fileEmprestimos.files = files;
                labelEmprestimos.innerText = "Selecionado: " + files[0].name;
                labelEmprestimos.classList.add("text-primary");
            } else {
                alert("Por favor, selecione apenas arquivos .csv!");
            }
        });
    </script>
</body>
</html>