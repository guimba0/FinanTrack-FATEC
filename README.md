# FinanTrack - FATEC

Sistema de controle financeiro pessoal e compartilhado desenvolvido como projeto acadêmico. O objetivo é permitir que usuários gerenciem seus ganhos, gastos e organizem finanças em conjunto (família, repúblicas, viagens).

## 🚀 Funcionalidades Principais

### 👤 Módulo Pessoal
* **Gestão de Lançamentos:** Registro de receitas e despesas com categorização.
* **Contas Recorrentes:** Cadastro de contas fixas (aluguel, internet) com data de vencimento.
* **Dashboard:** Visão geral do saldo atual e movimentações do mês.
* **Segurança:** Login e cadastro de usuários com proteção de dados básicos.

### 👥 Módulo de Finanças Compartilhadas (Planilha em Grupo)
*Funcionalidade exclusiva para gestão coletiva de despesas.*

* **Criação de Grupos:** Usuários podem criar grupos ilimitados (ex: "Casa de Praia", "Despesas do Apê") e tornam-se administradores.
* **Convite de Membros:** O administrador pode convidar outros usuários cadastrados para integrar o grupo.
* **Planilha de Transações:**
    * Todos os membros visualizam uma lista comum de despesas (a "Planilha").
    * Registro de despesas compartilhadas (ex: "Conta de Luz", "Mercado da semana").
    * **Definição de Pagante:** É possível indicar qual membro do grupo pagou a conta.
    * **Status de Pagamento:** Controle visual se a despesa está "Pendente" ou "Paga".
* **Transparência:** O saldo e as dívidas ficam visíveis para todos os participantes do grupo.

---

## 🛠️ Tecnologias Utilizadas

* **Java (JDK 17+)**
* **JSP (JavaServer Pages)** para interface web.
* **Servlet API** para controle de requisições.
* **SQLite** como banco de dados (arquivo `finantrack.db` portátil).
* **Bootstrap 5** para estilização responsiva.
* **JSTL** para lógica nas páginas JSP.

---

## ⚙️ Configuração e Instalação

1.  **Banco de Dados:**
    * O sistema utiliza SQLite. Ao iniciar a aplicação, ele tenta criar o arquivo `finantrack.db` automaticamente na pasta do projeto ou na pasta do usuário (`user.home`).
    * As tabelas são geradas automaticamente caso não existam (verificar logs do console na primeira execução).

2.  **Execução:**
    * Importe o projeto em sua IDE (NetBeans/Eclipse/IntelliJ).
    * Execute em um servidor **Tomcat** ou **GlassFish**.
    * Acesse via navegador: `http://localhost:8080/FinanTrack-FATEC`

---

## 📝 Estrutura do Banco de Dados (Grupos)

Para entendimento da lógica de grupos:
* **`grupos`**: Armazena o nome e o `admin_id` (quem criou).
* **`grupo_membros`**: Relaciona usuários aos grupos com status (PENDENTE/ACEITO).
* **`transacoes_grupo`**: A tabela central da "Planilha". Difere das transações pessoais pois possui campos para `data_vencimento` e `usuario_pagante_id`.

---

## 📌 Status do Projeto
* [x] Cadastro e Login
* [x] CRUD de Transações Pessoais
* [x] Gestão de Grupos e Convites
* [x] Lançamento de Despesas em Grupo
* [ ] Relatórios Avançados (Xls)
* [ ] Notificações por E-mail
