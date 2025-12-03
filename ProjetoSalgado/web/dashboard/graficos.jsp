<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%-- Verifica se o usuário está logado --%>
<%
    if (session.getAttribute("usuarioLogado") == null) {
        response.sendRedirect("../tela_login.jsp");
        return;
    }
%>

<jsp:include page="../navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Gráficos Financeiros" />
</jsp:include>

<div class="container mt-4">
    <div class="row">
        <div class="col-12">
            <div class="card bg-dark text-light">
                <div class.card-body p-3>
                    <h5 class="card-title">Evolução Mensal (Últimos 6 Meses)</h5>
                    <canvas id="lineChart"></canvas>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>
// 2. Garante que o script execute apenas depois que a página HTML for completamente carregada
document.addEventListener('DOMContentLoaded', function () {
    
    // 3. Busca os dados do nosso servlet. O fetch faz uma requisição GET para a URL especificada.
    fetch('${pageContext.request.contextPath}/dashboard/graficos-data')
        .then(response => response.json()) // Converte a resposta do servlet para o formato JSON
        .then(data => {
            // 'data' agora é um objeto JavaScript com os dados do gráfico (labels, entradas, saidas)
            
            // 4. Prepara a estrutura de dados que o Chart.js espera
            const chartData = {
                labels: data.labels, // Eixo X (Meses)
                datasets: [
                    {
                        label: 'Saídas (R$)',
                        data: data.saidas, // Valores de saídas
                        borderColor: 'rgba(255, 99, 132, 1)',
                        backgroundColor: 'rgba(255, 99, 132, 0.2)',
                        fill: true, // Preenche a área abaixo da linha
                        tension: 0.1 // Deixa a linha levemente curvada
                    },
                    {
                        label: 'Entradas (R$)',
                        data: data.entradas, // Valores de entradas
                        borderColor: 'rgba(54, 162, 235, 1)',
                        backgroundColor: 'rgba(54, 162, 235, 0.2)',
                        fill: true,
                        tension: 0.1
                    }
                ]
            };

            // 5. Configura as opções do gráfico (títulos, cores dos eixos, etc.)
            const config = {
                type: 'line', // Define o tipo de gráfico
                data: chartData,
                options: {
                    responsive: true, // Faz o gráfico se adaptar ao tamanho da tela
                    plugins: {
                        legend: {
                            position: 'top',
                             labels: { color: '#CCC' } // Cor do texto da legenda
                        },
                        title: {
                            display: true,
                            text: 'Comparativo de Entradas e Saídas',
                            color: '#FFF' // Cor do texto do título
                        }
                    },
                    scales: { // Configuração dos eixos X e Y
                        y: {
                            beginAtZero: true, // Eixo Y começa no zero
                            ticks: { color: '#AAA' } // Cor dos números do eixo Y
                        },
                        x: {
                            ticks: { color: '#AAA' } // Cor dos meses do eixo X
                        }
                    }
                }
            };
            
            // 6. Finalmente, cria o gráfico dentro do elemento <canvas> com o id 'lineChart'
            new Chart(
                document.getElementById('lineChart'),
                config
            );
        })
        .catch(error => console.error('Erro ao buscar dados para o gráfico:', error));
});
</script>

<jsp:include page="../navbar/rodape.jsp" />