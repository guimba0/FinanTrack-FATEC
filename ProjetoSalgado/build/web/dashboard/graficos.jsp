<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.time.LocalDate, br.com.finantrack.controller.*"%>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return;
    int ano = LocalDate.now().getYear(), mes = LocalDate.now().getMonthValue();
    if (request.getParameter("ano") != null) ano = Integer.parseInt(request.getParameter("ano"));
    if (request.getParameter("mes") != null) mes = Integer.parseInt(request.getParameter("mes"));

    TransacaoDAO dao = new TransacaoDAO();
    List<Transacao> transacoes = dao.listarLancamentosDoMes(usuario.getId(), ano, mes);
    Map<String, Double> mapCategorias = new HashMap<>();
    for (Transacao t : transacoes) {
        if ("saida".equals(t.getTipo())) mapCategorias.put(t.getCategoria(), mapCategorias.getOrDefault(t.getCategoria(), 0.0) + t.getValor());
    }
    StringBuilder lblCat = new StringBuilder("["), datCat = new StringBuilder("[");
    int c = 0;
    for (Map.Entry<String, Double> e : mapCategorias.entrySet()) {
        if (c++ > 0) { lblCat.append(","); datCat.append(","); }
        lblCat.append("'").append(e.getKey()).append("'"); datCat.append(e.getValue());
    }
    lblCat.append("]"); datCat.append("]");

    double[] diasEntrada = new double[31], diasSaida = new double[31];
    for (Transacao t : transacoes) {
        int dia = t.getData().getDayOfMonth();
        if (dia >= 1 && dia <= 31) {
            if ("entrada".equals(t.getTipo())) diasEntrada[dia-1] += t.getValor(); else diasSaida[dia-1] += t.getValor();
        }
    }
    StringBuilder lblDias = new StringBuilder("["), datEnt = new StringBuilder("["), datSai = new StringBuilder("[");
    int maxDias = LocalDate.of(ano, mes, 1).lengthOfMonth();
    for (int i = 0; i < maxDias; i++) {
        if (i > 0) { lblDias.append(","); datEnt.append(","); datSai.append(","); }
        lblDias.append("'").append(i+1).append("'"); datEnt.append(diasEntrada[i]); datSai.append(diasSaida[i]);
    }
    lblDias.append("]"); datEnt.append("]"); datSai.append("]");
    boolean isMinimal = "true".equals(request.getParameter("minimal"));
%>
<% if (isMinimal) { %>
    <!DOCTYPE html><html lang="pt-BR"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"><script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        html, body { margin: 0; padding: 0; overflow-x: hidden; width: 100%; }
        body { background-color: #f8f9fa !important; padding: 15px; font-family: sans-serif; }
        .card-chart { background-color: #fff; color: #212529; border: none; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        .card-header { border-bottom: 1px solid #dee2e6; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px; }
        .chart-container { position: relative; height: 220px; width: 100%; }
    </style></head><body>
<% } else { %>
    <jsp:include page="../navbar/cabecalho.jsp" /><script src="https://cdn.jsdelivr.net/npm/chart.js"></script><div class="container-fluid mt-4">
<% } %>

    <div class="row mb-2"><div class="col-12 px-3"><h5 class="fw-bold text-secondary mb-0"><i class="bi bi-graph-up"></i> Análise <small class="text-muted fs-6 ms-2"><%= mes %>/<%= ano %></small></h5></div></div>

    <div class="row g-3">
        <div class="col-lg-5 col-md-12"><div class="card card-chart h-100"><div class="card-header fw-bold text-center text-secondary py-2">Por Categoria</div><div class="card-body p-2 d-flex align-items-center justify-content-center">
            <% if (mapCategorias.isEmpty()) { %><div class="text-muted small py-5">Sem dados.</div><% } else { %><div class="chart-container"><canvas id="chartCategorias"></canvas></div><% } %>
        </div></div></div>
        <div class="col-lg-7 col-md-12"><div class="card card-chart h-100"><div class="card-header fw-bold text-center text-secondary py-2">Fluxo Diário</div><div class="card-body p-2"><div class="chart-container"><canvas id="chartFluxo"></canvas></div></div></div></div>
    </div>

    <script>
        // Configuração de cores para TEMA CLARO
        Chart.defaults.color = '#495057'; Chart.defaults.borderColor = '#dee2e6';
        
        const ctxPie = document.getElementById('chartCategorias');
        if (ctxPie) { new Chart(ctxPie, { type: 'doughnut', data: { labels: <%= lblCat.toString() %>, datasets: [{ data: <%= datCat.toString() %>, backgroundColor: ['#0d6efd', '#dc3545', '#ffc107', '#198754', '#6f42c1', '#fd7e14', '#20c997'], borderWidth: 2, borderColor: '#fff', hoverOffset: 10 }] }, options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'right', labels: { usePointStyle: true, boxWidth: 10, font: { size: 10 } } } } } }); }
        
        const ctxLine = document.getElementById('chartFluxo');
        if (ctxLine) { new Chart(ctxLine, { type: 'line', data: { labels: <%= lblDias.toString() %>, datasets: [{ label: 'Entradas', data: <%= datEnt.toString() %>, borderColor: '#198754', backgroundColor: 'rgba(25, 135, 84, 0.1)', fill: true, yAxisID: 'y', tension: 0.4, pointRadius: 0, pointHoverRadius: 4 }, { label: 'Saídas', data: <%= datSai.toString() %>, borderColor: '#dc3545', backgroundColor: 'rgba(220, 53, 69, 0.1)', fill: true, yAxisID: 'y1', tension: 0.4, pointRadius: 0, pointHoverRadius: 4 }] }, options: { responsive: true, maintainAspectRatio: false, interaction: { mode: 'index', intersect: false }, scales: { x: { grid: { display: false }, ticks: { maxTicksLimit: 10 } }, y: { type: 'linear', display: true, position: 'left', grid: { color: '#dee2e6' } }, y1: { type: 'linear', display: true, position: 'right', grid: { drawOnChartArea: false } } }, plugins: { legend: { position: 'top', labels: { usePointStyle: true, boxWidth: 8, font: { size: 10 } } } } } }); }
    </script>
<% if (isMinimal) { %></body></html><% } else { %></div><jsp:include page="../navbar/rodape.jsp" /><% } %>