<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.time.*, java.time.format.DateTimeFormatter, br.com.finantrack.controller.*"%>
<%
    Usuario u = (Usuario) session.getAttribute("usuario");
    if (u == null) { response.sendRedirect("../tela_login.jsp"); return; }

    int ano = LocalDate.now().getYear(), mes = LocalDate.now().getMonthValue();
    String pParam = request.getParameter("periodo");
    if (pParam != null && !pParam.isEmpty()) {
        try { String[] parts = pParam.split("-");
        ano = Integer.parseInt(parts[0]); mes = Integer.parseInt(parts[1]); } catch(Exception e){}
    } else {
        if (request.getParameter("ano") != null) ano = Integer.parseInt(request.getParameter("ano"));
        if (request.getParameter("mes") != null) mes = Integer.parseInt(request.getParameter("mes"));
    }
    LocalDate dataAtual = LocalDate.of(ano, mes, 1);
    TransacaoDAO tDao = new TransacaoDAO();
    List<Transacao> lista = tDao.listarLancamentosDoMes(u.getId(), ano, mes);
    ContaRecorrenteDAO rDao = new ContaRecorrenteDAO();
    List<ContaRecorrente> recorrentes = rDao.listar(u.getId());
    double ent = 0, sai = 0;
    for (Transacao t : lista) { if ("entrada".equals(t.getTipo())) ent += t.getValor();
    else sai += t.getValor(); }
    double saldo = ent - sai;
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"><title>FinanTrack</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <style>
        body { overflow-x: hidden; background-color: #f8f9fa; }
        
        /* Layout */
        :root { --header-height: 72px; }
        .offcanvas-start, #graphics-wrapper { top: var(--header-height) !important; height: calc(100vh - var(--header-height)) !important; border-top: 1px solid rgba(0,0,0,0.1); }
        .offcanvas-backdrop { top: var(--header-height) !important; height: calc(100vh - var(--header-height)) !important; }
        #graphics-wrapper { width: 100vw; background-color: #f8f9fa; position: fixed; left: 100vw; transition: all 0.5s ease; z-index: 1040; overflow-y: auto; }
        #wrapper { display: flex; width: 100%; transition: 0.5s; }
        #main { width: 100vw; transition: 0.5s; }
        body.show-gfx #main { transform: translateX(-100vw); } body.show-gfx #graphics-wrapper { left: 0; }
        
        /* Modais */
        #modalConfirm { z-index: 1090 !important; }
        .modal-backdrop { z-index: 1050; } .modal-backdrop.show:nth-of-type(2) { z-index: 1080 !important; }

        /* Botões */
        .btn-float { position: fixed; top: 55%; transform: translateY(-50%); z-index: 1050; border-radius: 0 50% 50% 0; padding: 15px 8px; border: none; opacity: 0.9; transition: 0.3s; color: #fff; box-shadow: 2px 2px 10px rgba(0,0,0,0.2); }
        .btn-float:hover { opacity: 1; padding-left: 15px; width: 60px; }
        .btn-left { left: 0; background: #198754; } 
        .btn-right { right: 0; border-radius: 50% 0 0 50%; background: #0d6efd; }
        
        .input-dark { background: #343a40; border: 1px solid #495057; color: #fff; }
        .status-btn { cursor: pointer; transition: 0.2s; } .status-btn:hover { transform: scale(1.2); }
        .full-frame { width: 100%; height: 100%; border: none; }
    </style>
</head>
<body>
    
    <div style="position: sticky; top: 0; z-index: 1060; width: 100%;">
        <jsp:include page="../navbar/cabecalho.jsp" />
    </div>

    <button id="btnRelatorios" class="btn-float btn-left" data-bs-toggle="offcanvas" data-bs-target="#offRelatorios" title="Relatórios / Excel">
        <i class="bi bi-file-earmark-spreadsheet fs-4"></i>
    </button>
    <button id="btnV" class="btn-float btn-left" style="display:none; background:#343a40" title="Voltar"><i class="bi bi-arrow-left fs-5"></i></button>
    <button id="btnX" class="btn-float btn-right" title="Gráficos"><i class="bi bi-bar-chart-line-fill fs-5"></i></button>

    <div id="wrapper">
        <div id="main">
            <div class="container mt-4 mb-5">
                <div class="row mb-4 align-items-center">
                    <div class="col-md-5"><h3 class="text-secondary fw-bold text-capitalize"><i class="bi bi-calendar-event"></i> <%= dataAtual.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt","BR"))) %></h3></div>
                    <div class="col-md-7 text-end" id="resumoValores">
                        <span class="badge bg-success p-2 shadow-sm" id="valEnt">Entrada: <%= String.format("%.2f", ent) %></span>
                        <span class="badge bg-danger p-2 shadow-sm" id="valSai">Saída: <%= String.format("%.2f", sai) %></span>
                        <span class="badge bg-primary p-2 shadow-sm" id="valSal">Saldo: <%= String.format("%.2f", saldo) %></span>
                    </div>
                </div>

                <div class="card bg-dark text-white shadow-sm mb-4 border-0">
                    <div class="card-body d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <form action="detalhes_mes.jsp" method="GET" class="d-flex align-items-center gap-2">
                            <a href="detalhes_mes.jsp?mes=<%= dataAtual.minusMonths(1).getMonthValue() %>&ano=<%= dataAtual.minusMonths(1).getYear() %>" class="btn btn-outline-light btn-sm rounded-circle"><i class="bi bi-chevron-left"></i></a>
                            <input type="month" name="periodo" class="form-control form-control-sm input-dark fw-bold" value="<%= String.format("%d-%02d", ano, mes) %>" onchange="this.form.submit()">
                            <a href="detalhes_mes.jsp?mes=<%= dataAtual.plusMonths(1).getMonthValue() %>&ano=<%= dataAtual.plusMonths(1).getYear() %>" class="btn btn-outline-light btn-sm rounded-circle"><i class="bi bi-chevron-right"></i></a>
                        </form>
                        <div>
                            <button class="btn btn-outline-light btn-sm me-2" data-bs-toggle="modal" data-bs-target="#modalRecorrentes"><i class="bi bi-arrow-repeat"></i> Assinaturas</button>
                            <button class="btn btn-light text-dark btn-sm fw-bold" onclick="abrirModalCriar()"><i class="bi bi-plus-lg"></i> Novo</button>
                        </div>
                    </div>
                </div>

                <div class="card shadow border-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0" id="tabelaTransacoes">
                            <thead class="bg-light text-secondary">
                                <tr><th class="ps-4">Dia</th><th>Descrição</th><th>Categoria</th><th>Status</th><th class="text-end">Valor</th><th class="text-center no-export">Ações</th></tr>
                            </thead>
                            <tbody>
                                <% if(lista.isEmpty()) { %><tr><td colspan="6" class="text-center py-4 text-muted">Sem lançamentos.</td></tr><% } %>
                                <% for(Transacao t : lista) { %>
                                <tr>
                                    <td class="ps-4 fw-bold text-muted"><%= t.getData().format(DateTimeFormatter.ofPattern("dd")) %></td>
                                    <td><%= t.getDescricao() %> <% if("Recorrente".equals(t.getOrigem())){ %><small class="text-muted">(Fixa)</small><% } %></td>
                                    <td><span class="badge bg-light text-dark border"><%= t.getCategoria() %></span></td>
                                    <td data-status="<%= t.isPago() ? "Pago" : "Pendente" %>">
                                        <i class="<%= t.isPago() ? "bi bi-check-circle-fill text-success" : "bi bi-circle text-warning" %> fs-5 status-btn" 
                                           onclick="confirmarAcao('Alterar Status', 'Mudar status para <%= t.isPago() ? "Pendente" : "Pago" %>?', '${pageContext.request.contextPath}/TransacaoStatusServlet?id=<%= t.getId() %>&pago=<%= !t.isPago() %>&mes=<%= mes %>&ano=<%= ano %>')"></i>
                                    </td>
                                    <td class="text-end fw-bold <%= t.getTipo().equals("entrada")?"text-success":"text-danger" %>"><%= t.getTipo().equals("entrada")?"+":"-" %> <%= String.format("%.2f", t.getValor()) %></td>
                                    <td class="text-center no-export">
                                        <button class="btn btn-link text-primary btn-sm p-0 me-2" onclick="editarT('<%= t.getId() %>','<%= t.getDescricao() %>','<%= t.getValor() %>','<%= t.getData() %>','<%= t.getTipo() %>','<%= t.getCategoria() %>', <%= t.isPago() %>)"><i class="bi bi-pencil"></i></button>
                                        <button class="btn btn-link text-danger btn-sm p-0" onclick="confirmarAcao('Excluir', 'Apagar lançamento?', '${pageContext.request.contextPath}/TransacaoDeleteServlet?id=<%= t.getId() %>&mes=<%= mes %>&ano=<%= ano %>')"><i class="bi bi-trash"></i></button>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div> 
            <jsp:include page="../navbar/rodape.jsp" />
        </div>
        <div id="graphics-wrapper"><iframe src="${pageContext.request.contextPath}/dashboard/graficos.jsp?minimal=true&mes=<%= mes %>&ano=<%= ano %>" class="full-frame"></iframe></div>
    </div>

    <div class="modal fade" id="modalConfirm" tabindex="-1"><div class="modal-dialog modal-sm modal-dialog-centered"><div class="modal-content text-center p-3 border-0 shadow-lg"><div class="mb-2"><i class="bi bi-question-circle-fill text-primary display-4"></i></div><h5 id="confTitle" class="fw-bold">Confirma?</h5><p id="confMsg" class="text-muted small mb-4">Ação irreversível.</p><div class="d-flex gap-2"><button type="button" class="btn btn-light flex-fill" data-bs-dismiss="modal">Não</button><a id="confBtn" href="#" class="btn btn-primary flex-fill">Sim</a></div></div></div></div>
    <div class="modal fade" id="modalLanc" tabindex="-1"><div class="modal-dialog modal-dialog-centered"><div class="modal-content"><form id="formL" action="${pageContext.request.contextPath}/transacao" method="POST"><input type="hidden" name="origem" value="detalhes_mes"><input type="hidden" name="mes" value="<%= mes %>"><input type="hidden" name="ano" value="<%= ano %>"><input type="hidden" name="id" id="lid"><input type="hidden" name="split" id="lsplit" value="false"><div class="modal-header bg-dark text-white"><h5 class="modal-title" id="ltit">Novo</h5><button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div><div class="modal-body"><div id="alertRec" class="alert alert-warning small mb-2" style="display:none"><i class="bi bi-info-circle"></i> Editando deste mês em diante.</div><div class="row g-2 mb-2"><div class="col-8"><input type="text" name="descricao" id="ldesc" class="form-control" placeholder="Descrição" required></div><div class="col-4"><input type="number" name="valor" id="lval" step="0.01" class="form-control" placeholder="0.00" required></div></div><div class="row g-2 mb-2"><div class="col-6"><input type="date" name="data" id="ldata" class="form-control" required></div><div class="col-6"><select name="tipo" id="ltipo" class="form-select"><option value="saida">Saída</option><option value="entrada">Entrada</option></select></div></div><div class="mb-2"><select name="categoria" id="lcat" class="form-select" onchange="verifOutro()"><option>Alimentação</option><option>Transporte</option><option>Moradia</option><option>Lazer</option><option>Saúde</option><option>Salário</option><option value="Outro">Outro...</option></select><input type="text" name="categoriaOutro" id="lout" class="form-control mt-1" style="display:none" placeholder="Qual?"></div><div class="bg-light p-2 rounded border"><div class="form-check form-switch"><input class="form-check-input" type="checkbox" id="lrec" name="recorrente" onchange="toggleRec()"><label class="form-check-label fw-bold" for="lrec">Conta Recorrente</label></div><div id="divFim" style="display:none" class="mt-2 small text-muted"><label>Data Fim (Opcional):</label><input type="date" name="dataFim" class="form-control form-control-sm"></div><div class="form-check mt-2" id="divPago"><input class="form-check-input" type="checkbox" name="pago" id="lpago" checked><label class="form-check-label" for="lpago">Já pago?</label></div></div></div><div class="modal-footer p-1"><button type="submit" class="btn btn-dark w-100">Salvar</button></div></form></div></div></div>
    <div class="modal fade" id="modalRecorrentes" tabindex="-1"><div class="modal-dialog modal-dialog-centered"><div class="modal-content"><div class="modal-header bg-primary text-white py-2"><h6 class="mb-0">Minhas Assinaturas</h6><button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div><div class="modal-body p-0"><% if(recorrentes != null && !recorrentes.isEmpty()) { %><ul class="list-group list-group-flush"><% for(ContaRecorrente r : recorrentes) { %><li class="list-group-item d-flex justify-content-between align-items-center"><div><strong><%= r.getDescricao() %></strong> <br><small class="text-muted">Dia <%= r.getDiaVencimento() %> • R$ <%= r.getValor() %></small></div><div><button class="btn btn-sm btn-outline-primary border-0" onclick="editarRec('<%= r.getId() %>','<%= r.getDescricao() %>','<%= r.getValor() %>','<%= r.getCategoria() %>','<%= r.getDiaVencimento() %>')"><i class="bi bi-pencil"></i></button><button class="btn btn-sm btn-outline-danger border-0" onclick="confirmarAcao('Cancelar Assinatura', 'Deseja parar esta conta?', '${pageContext.request.contextPath}/ContaRecorrenteDeleteServlet?id=<%= r.getId() %>')"><i class="bi bi-trash"></i></button></div></li><% } %></ul><% } else { %><p class="text-center p-3 text-muted">Nenhuma conta fixa.</p><% } %></div></div></div></div>

    <div class="offcanvas offcanvas-start" tabindex="-1" id="offRelatorios">
        <div class="offcanvas-header bg-success text-white">
            <h5 class="offcanvas-title"><i class="bi bi-file-earmark-text"></i> Relatórios</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas"></button>
        </div>
        <div class="offcanvas-body d-flex flex-column justify-content-center">
            <div class="text-center mb-4">
                <i class="bi bi-file-earmark-spreadsheet display-1 text-success"></i>
                <h5 class="mt-3">Exportar Tabela</h5>
                <p class="text-muted small">Referência: <%= mes %>/<%= ano %></p>
            </div>
            
            <div class="mb-3 text-start">
                <label class="form-label small fw-bold text-muted">Nome do Arquivo (Opcional):</label>
                <div class="input-group">
                    <input type="text" id="nomeArquivoExcel" class="form-control" placeholder="Ex: Minha Planilha">
                    <span class="input-group-text text-muted">.xls</span>
                </div>
            </div>

            <button class="btn btn-success w-100 shadow-sm py-3 mb-2" onclick="exportarTabelaExcel()">
                <i class="bi bi-download me-2"></i> Baixar Excel
            </button>
            <div class="alert alert-light mt-3 text-center border small">
                <i class="bi bi-info-circle"></i> O arquivo incluirá resumo financeiro e lançamentos.
            </div>
        </div>
    </div>

    <script>
        const body = document.body, btnX = document.getElementById('btnX'), btnV = document.getElementById('btnV'), btnRel = document.getElementById('btnRelatorios');
        function toggleGfx() {
            body.classList.toggle('show-gfx');
            const show = body.classList.contains('show-gfx');
            btnRel.style.display = show ? 'none' : 'block';
            btnV.style.display = show ? 'block' : 'none';
            btnX.innerHTML = show ? '<i class="bi bi-x-lg"></i>' : '<i class="bi bi-bar-chart-line-fill fs-5"></i>';
        }
        btnX.onclick = toggleGfx;
        btnV.onclick = toggleGfx;

        function verifOutro() { document.getElementById("lout").style.display = (document.getElementById("lcat").value === "Outro") ? "block" : "none"; }
        function toggleRec() { const isRec = document.getElementById("lrec").checked; document.getElementById("divFim").style.display = isRec ? "block" : "none"; document.getElementById("divPago").style.display = isRec ? "none" : "block"; }

        document.addEventListener('hidden.bs.modal', function () {
            if (!document.querySelector('.modal.show')) {
                document.querySelectorAll('.modal-backdrop').forEach(el => el.remove());
                document.body.classList.remove('modal-open'); document.body.style = "";
            }
        });
        document.addEventListener('hidden.bs.offcanvas', function () { document.querySelectorAll('.offcanvas-backdrop').forEach(el => el.remove()); });

        function abrirModalCriar() {
            document.getElementById("formL").reset();
            document.getElementById("lid").value = ""; document.getElementById("lsplit").value = "false"; document.getElementById("alertRec").style.display = "none";
            
            // Lógica de Data Atual
            let ano = <%= ano %>, mes = <%= mes %>;
            let hoje = new Date();
            let dia = "01";
            if (hoje.getFullYear() === ano && (hoje.getMonth() + 1) === mes) {
                let d = hoje.getDate();
                dia = d < 10 ? "0" + d : d;
            }
            let mf = mes < 10 ? "0"+mes : mes;
            document.getElementById("ldata").value = ano + "-" + mf + "-" + dia;
            
            document.getElementById("ltit").innerText = "Novo Lançamento";
            document.getElementById("formL").action = "${pageContext.request.contextPath}/transacao";
            new bootstrap.Modal(document.getElementById('modalLanc')).show();
        }

        function editarT(id, desc, val, data, tipo, cat, pago) {
            document.getElementById("lid").value = id;
            document.getElementById("ldesc").value = desc; document.getElementById("lval").value = val; document.getElementById("ldata").value = data; document.getElementById("ltipo").value = tipo; 
            let c = document.getElementById("lcat"); c.value = cat;
            if(c.value === "") { c.value="Outro"; document.getElementById("lout").style.display="block"; document.getElementById("lout").value=cat; }
            document.getElementById("lpago").checked = pago;
            
            if (parseInt(id) < 0) {
                document.getElementById("lsplit").value = "true";
                document.getElementById("alertRec").style.display = "block";
                document.getElementById("lrec").checked = true; document.getElementById("lrec").disabled = true;
                // Edição de Recorrentes usa outro servlet
                document.getElementById("formL").action = "${pageContext.request.contextPath}/ContaRecorrenteEditServlet";
            } else {
                document.getElementById("lsplit").value = "false";
                document.getElementById("alertRec").style.display = "none";
                document.getElementById("lrec").checked = false; document.getElementById("lrec").disabled = false;
                
                // --- CORREÇÃO AQUI: mudado de /TransacaoEditServlet para /transacao-editar ---
                document.getElementById("formL").action = "${pageContext.request.contextPath}/transacao-editar";
            }
            toggleRec(); document.getElementById("ltit").innerText = "Editar Lançamento"; new bootstrap.Modal(document.getElementById('modalLanc')).show();
        }

        function editarRec(id, desc, val, cat, dia) {
            bootstrap.Modal.getInstance(document.getElementById('modalRecorrentes')).hide();
            setTimeout(() => {
                document.getElementById("lid").value = id; document.getElementById("ldesc").value = desc; document.getElementById("lval").value = val;
                let c = document.getElementById("lcat"); c.value = cat; if(c.value === "") { c.value="Outro"; document.getElementById("lout").style.display="block"; document.getElementById("lout").value=cat; }
                document.getElementById("lrec").checked = true; document.getElementById("lsplit").value = "false"; document.getElementById("alertRec").style.display = "none"; toggleRec();
                document.getElementById("ltit").innerText = "Editar Assinatura";
                let ano = <%= ano %>, mes = <%= mes %>, df = dia < 10 ? "0"+dia : dia, mf = mes < 10 ? "0"+mes : mes;
                document.getElementById("ldata").value = ano + "-" + mf + "-" + df;
                document.getElementById("formL").action = "${pageContext.request.contextPath}/ContaRecorrenteEditServlet";
                new bootstrap.Modal(document.getElementById('modalLanc')).show();
            }, 300);
        }

        function confirmarAcao(titulo, texto, link) {
            document.getElementById("confTitle").innerText = titulo;
            document.getElementById("confMsg").innerText = texto; document.getElementById("confBtn").href = link;
            new bootstrap.Modal(document.getElementById('modalConfirm')).show();
        }

        function exportarTabelaExcel() {
            var nomeInput = document.getElementById('nomeArquivoExcel').value.trim();
            var nomeLimpo = nomeInput.replace(/[^a-z0-9_\-\s]/gi, '_');
            var filename = nomeLimpo ? nomeLimpo + '.xls' : 'FT - Planilha Pessoal - <%= mes %>-<%= ano %>.xls';

            var originalTable = document.getElementById('tabelaTransacoes');
            var cloneTable = originalTable.cloneNode(true);
            var rows = cloneTable.rows;
            for (var i = 0; i < rows.length; i++) {
                rows[i].deleteCell(-1);
                if(i > 0) { 
                    var st = rows[i].cells[3];
                    st.innerHTML = st.getAttribute('data-status');
                }
            }
            var resumo = document.createElement("table");
            var r1 = resumo.insertRow(0);
            var c1 = r1.insertCell(0); c1.colSpan = 5; c1.innerHTML = "<b>Relatório FinanTrack - <%= mes %>/<%= ano %></b>";
            c1.style.backgroundColor = "#d1e7dd"; c1.style.textAlign = "center"; c1.style.fontSize = "14px";
            resumo.insertRow(1).insertCell(0).innerHTML = "";
            var r2 = resumo.insertRow(2);
            r2.insertCell(0).innerHTML = "<b>Entradas:</b> " + document.getElementById('valEnt').innerText;
            r2.insertCell(1).innerHTML = "<b>Saídas:</b> " + document.getElementById('valSai').innerText;
            r2.insertCell(2).innerHTML = "<b>Saldo Final:</b> " + document.getElementById('valSal').innerText;
            resumo.insertRow(3).insertCell(0).innerHTML = "";
            var html = resumo.outerHTML + cloneTable.outerHTML;
            html = html.replace(/ /g, '%20');

            var downloadLink = document.createElement("a");
            document.body.appendChild(downloadLink);
            
            if (navigator.msSaveOrOpenBlob) {
                var blob = new Blob(['\ufeff', html], { type: 'application/vnd.ms-excel' });
                navigator.msSaveOrOpenBlob(blob, filename);
            } else {
                downloadLink.href = 'data:application/vnd.ms-excel,' + html;
                downloadLink.download = filename;
                downloadLink.click();
            }
        }
    </script>
</body>
</html>