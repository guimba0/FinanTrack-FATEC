// Define as versões dos plugins diretamente.
plugins {
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.3.2"
}

// Configurações básicas do projeto.
group = "com.finantrack"
version = "0.0.1"
application {
    mainClass.set("com.finantrack.ApplicationKt")
}

// =========== ADICIONE ESTE BLOCO PARA CORRIGIR O ERRO ===========
// Diz ao Gradle para usar as ferramentas do Java 17 para compilar o projeto,
// o que resolve o problema de compatibilidade com o "JVM target 21".
kotlin {
    jvmToolchain(17)
}
// ===============================================================

// Define de onde baixar as bibliotecas.
repositories {
    mavenCentral()
}

// Lista de dependências com versões "hardcoded" (sem variáveis).
dependencies {
    // Essenciais do Ktor
    implementation("io.ktor:ktor-server-core-jvm:2.3.2")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.2")

    // Para as páginas HTML (Thymeleaf)
    implementation("io.ktor:ktor-server-thymeleaf:2.3.2")

    // Para o sistema de Login (Sessões)
    implementation("io.ktor:ktor-server-sessions:2.3.2")

    // Para o banco de dados SQLite
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")

    // Para a biblioteca Exposed (falar com o DB em Kotlin)
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    // Para o sistema de logs
    implementation("ch.qos.logback:logback-classic:1.2.11")

    // Para testes (não afeta a execução)
    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")
}