<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.time.LocalDate, br.com.finantrack.controller.*"%>

<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return;

    // --- 1. DEFINIR PERÍODOS ---
    int ano = LocalDate.now().getYear();
    int mes = LocalDate.now().getMonthValue();
    
    if (request.getParameter("ano") != null) ano = Integer.parseInt(request.getParameter("ano"));
    if (request.getParameter("mes") != null) mes = Integer.parseInt(request.getParameter("mes"));

    LocalDate dataBase = LocalDate.of(ano, mes, 1);
    String mesAnoString = String.format("%d-%02d", ano, mes);

    TransacaoDAO dao = new TransacaoDAO();
    ContaRecorrenteDAO rDao = new ContaRecorrenteDAO();

    List<Transacao> transacoes = dao.listarLancamentosDoMes(usuario.getId(), ano, mes);
    List<ContaRecorrente> recorrentes = rDao.listar(usuario.getId());

    // --- 2. DADOS ROSCA (Categorias) ---
    Map<String, Double> mapCategorias = new HashMap<>();
    for (Transacao t : transacoes) {
        if ("saida".equals(t.getTipo())) {
            mapCategorias.put(t.getCategoria(), mapCategorias.getOrDefault(t.getCategoria(), 0.0) + t.getValor());
        }
    }

    StringBuilder lblCat = new StringBuilder("["); StringBuilder datCat = new StringBuilder("[");
    int c = 0;
    for (Map.Entry<String, Double> e : mapCategorias.entrySet()) {
        if (c++ > 0) { lblCat.append(","); datCat.append(","); }
        lblCat.append("'").append(e.getKey()).append("'"); datCat.append(e.getValue());
    }
    lblCat.append("]"); datCat.append("]");

    // --- 3. DADOS LINHA (Saldo Acumulado "Extrato") ---
    int maxDias = dataBase.lengthOfMonth();
    double[] movimentacaoDiaria = new double[maxDias + 1];

    // A. Soma Transações Reais
    for (Transacao t : transacoes) {
        int dia = t.getData().getDayOfMonth();
        if (dia <= maxDias) {
            if ("entrada".equals(t.getTipo())) movimentacaoDiaria[dia] += t.getValor();
            else movimentacaoDiaria[dia] -= t.getValor();
        }
    }

    // B. Soma Contas Recorrentes (Pendentes)
    for (ContaRecorrente r : recorrentes) {
        boolean iniciou = !r.getDataInicio().isAfter(dataBase.withDayOfMonth(dataBase.lengthOfMonth()));
        boolean naoAcabou = r.getDataFim() == null || !r.getDataFim().isBefore(dataBase);
        
        if (iniciou && naoAcabou) {
            boolean jaPaga = mesAnoString.equals(r.getUltimoMesPago());
            if (!jaPaga) {
                int diaVenc = r.getDiaVencimento();
                if (diaVenc > maxDias) diaVenc = maxDias;
                movimentacaoDiaria[diaVenc] -= r.getValor();
            }
        }
    }

    // C. Calcula Linha do Tempo (Acumulado)
    // Começa com o Salário definido no perfil
    double saldoAtual = usuario.getSalario();
    
    StringBuilder lblDias = new StringBuilder("[");
    StringBuilder datSal = new StringBuilder("[");
    
    // Loop dia a dia acumulando o valor
    for (int i = 1; i <= maxDias; i++) {
        saldoAtual += movimentacaoDiaria[i]; // Soma ou subtrai o que aconteceu no dia

        if (i > 1) { lblDias.append(","); datSal.append(","); }
        lblDias.append("'").append(i).append("'");
        // Formata para o gráfico não quebrar com muitas casas decimais
        datSal.append(String.format(Locale.US, "%.2f", saldoAtual));
    }
    lblDias.append("]"); datSal.append("]");

    boolean isMinimal = "true".equals(request.getParameter("minimal"));
%>

<% if (isMinimal) { %>
    <!DOCTYPE html>
    <html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <style>
            html, body { margin: 0; padding: 0; overflow: hidden; width: 100%; height: 100%; }
            body { background-color: #f8f9fa !important; padding: 15px; font-family: sans-serif; }
            .card-dark { background-color: #212529; color: white; border: none; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.15); }
            .card-header { border-bottom: 1px solid #495057; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px; }
            .chart-container { position: relative; height: 220px; width: 100%; }
        </style>
    </head>
    <body>
<% } else { %>
    <jsp:include page="../navbar/cabecalho.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <div class="container-fluid mt-4">
<% } %>

    <div class="row mb-2">
        <div class="col-12 px-3">
            <h5 class="fw-bold text-secondary mb-0"><i class="bi bi-graph-up"></i> Análise <small class="text-muted fs-6 ms-2"><%= mes %>/<%= ano %></small></h5>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-lg-5 col-md-12">
            <div class="card card-dark h-100">
                <div class="card-header fw-bold text-center text-warning py-2">Por Categoria</div>
                <div class="card-body p-2 d-flex align-items-center justify-content-center">
                    <% if (mapCategorias.isEmpty()) { %><div class="text-muted small py-5">Sem dados.</div><% 
                    } else { %>
                        <div class="chart-container"><canvas id="chartCategorias"></canvas></div>
                    <% } %>
                </div>
            </div>
        </div>
        
        <div class="col-lg-7 col-md-12">
            <div class="card card-dark h-100">
                <div class="card-header fw-bold text-center text-info py-2">Evolução do Saldo</div>
                <div class="card-body p-2">
                    <div class="chart-container"><canvas id="chartFluxo"></canvas></div>
                </div>
            </div>
        </div>
    </div>

    <script>
        Chart.defaults.color = '#e0e0e0';
        Chart.defaults.borderColor = '#495057';
        
        const ctxPie = document.getElementById('chartCategorias');
        if (ctxPie) {
            new Chart(ctxPie, {
                type: 'doughnut',
                data: {
                    labels: <%= lblCat.toString() %>,
                    datasets: [{
                        data: <%= datCat.toString() %>,
                        backgroundColor: ['#0d6efd', '#dc3545', '#ffc107', '#198754', '#6f42c1', '#fd7e14', '#20c997'],
                        borderWidth: 0, hoverOffset: 10
                    }]
                },
                options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'right', labels: { usePointStyle: true, boxWidth: 10, font: { size: 10 } } } } }
            });
        }

        const ctxLine = document.getElementById('chartFluxo');
        if (ctxLine) {
            new Chart(ctxLine, {
                type: 'line',
                data: {
                    labels: <%= lblDias.toString() %>,
                    datasets: [
                        { 
                            label: 'Saldo Acumulado', 
                            data: <%= datSal.toString() %>, 
                            borderColor: '#0dcaf0', 
                            backgroundColor: (context) => {
                                const ctx = context.chart.ctx;
                                const gradient = ctx.createLinearGradient(0, 0, 0, 200);
                                gradient.addColorStop(0, 'rgba(13, 202, 240, 0.4)');
                                gradient.addColorStop(1, 'rgba(13, 202, 240, 0.0)');
                                return gradient;
                            },
                            fill: true, 
                            tension: 0.1, // Linha um pouco mais reta para mostrar os "degraus" de gasto
                            pointRadius: 3, pointHoverRadius: 6 
                        }
                    ]
                },
                options: {
                    responsive: true, maintainAspectRatio: false, 
                    interaction: { mode: 'index', intersect: false },
                    scales: { 
                        x: { grid: { display: false }, ticks: { maxTicksLimit: 10 } }, 
                        y: { 
                            beginAtZero: false, 
                            grid: { color: '#343a40' } 
                        } 
                    },
                    plugins: { 
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    let label = context.dataset.label || '';
                                    if (label) { label += ': '; }
                                    if (context.parsed.y !== null) {
                                        label += new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(context.parsed.y);
                                    }
                                    return label;
                                }
                            }
                        }
                    }
                }
            });
        }
    </script>

<% if (isMinimal) { %></body></html><% } else { %></div><jsp:include page="../navbar/rodape.jsp" /><% } %>