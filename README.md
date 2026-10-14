# Ampéra - Aplicativo

Projeto: **Ampéra**, monitoramento de consumo de energia residencial.
Grupo: **Ampera**.

Repositórios do sistema:

- Backend: https://github.com/Ampera-PI/ampera-backend
- Aplicativo (este, Android, desktop e web): https://github.com/Ampera-PI/ampera-app

## Integrantes e entidades

| Integrante | RA | Entidades sob responsabilidade |
| --- | --- | --- |
| Arthur Francisco de Lima | 1111392411008 | Usuário e Residência |
| Enzo Fernandes Dantas | 1111392411037 | Cômodo e Dispositivo |
| José Augusto Mamani Rosales | 1111392411012 | Sensor e Medição |
| Luís Gustavo Gomes Gonçalves | 1111392411015 | Tarifa e Alerta |
| Maria Fernanda Silva Mirita | 1111392411035 | Meta de consumo e Relatório |

### Divisão de tarefas

Cada integrante cuida das duas entidades acima em todas as camadas: no aplicativo, o modelo, o formulário (com os botões Gravar e Limpar Campos) e a tela; no backend, entidade, repositório com busca `LIKE`, serviço, controller, DTOs, mapper e validação. Além disso:

- **Arthur Francisco de Lima**: autenticação (`/signin` e `/signup`), utilitário JWT e Spring Security no backend, e a estrutura do projeto Gradle multiplataforma.
- **Enzo Fernandes Dantas**: infraestrutura de execução (`docker-compose.yml` e scripts locais), estrutura do CRUD com Gravar e Limpar Campos e módulos de plataforma (Android, desktop e web).
- **José Augusto Mamani Rosales**: leituras de energia e regras de consumo do período, e a base dos recursos de CRUD do aplicativo.
- **Luís Gustavo Gomes Gonçalves**: cálculo de custo pela tarifa vigente, avaliação dos alertas, tema visual, cliente HTTP e contrato do repositório.
- **Maria Fernanda Silva Mirita**: previsão de consumo por regressão linear, geração de relatórios, tela de login e navegação, e este README.

## O que a aplicação faz

Quem mora em uma casa ou apartamento costuma só descobrir quanto gastou de energia quando a conta chega. O Ampéra resolve isso: o usuário cadastra suas residências, cômodos, dispositivos e sensores, registra as medições de tensão, corrente e potência, e o backend grava cada medição já com o custo calculado pela tarifa vigente da residência.

Pelo aplicativo o usuário:

- cadastra residências, cômodos, dispositivos e sensores;
- registra as medições e acompanha o custo por dispositivo;
- cria alertas para quando tensão, corrente ou potência passam de um limite;
- define metas de consumo e vê o quanto já usou delas;
- gera relatórios por período, com a previsão do consumo do dia seguinte por regressão linear.

O mesmo código roda em Android, desktop e navegador. Cada tela de cadastro tem um formulário com os botões **Gravar** e **Limpar Campos**.

## Tecnologias

- Kotlin Multiplatform e Compose Multiplatform (Material 3), para Android, desktop e web (Wasm)
- Ktor Client e kotlinx.serialization para falar com a API
- ViewModel e `StateFlow` no padrão MVVM com intents (MVI)
- Backend: Kotlin, Spring Boot 3.4, Spring Security com JWT, JPA, PostgreSQL e Flyway

## Camadas e pastas

Interface única, no padrão MVVM com intents: cada tela tem um `UiState`, uma `sealed interface` de intents e um ViewModel que expõe `StateFlow`. Os ViewModels dependem de `AmperaRepositorio`, e o Ktor fica só em `AmperaApi`.

Aplicativo, em `composeApp/src`:

| Camada | Pasta | O que tem |
| --- | --- | --- |
| Dados | `commonMain/kotlin/com/ampera/dados` | Um arquivo de modelos por entidade (modelo e formulário), cliente HTTP (`Api.kt`), contrato do repositório (`Repositorio.kt`) e sessão |
| Interface | `commonMain/kotlin/com/ampera/ui` | Telas, ViewModels, lista e estrutura do CRUD (`Crud.kt`), um formulário por entidade com Gravar e Limpar Campos (`Formulario<Entidade>.kt`), a descrição de cada recurso (`Recurso<Entidade>.kt`) e o tema |
| Android | `androidMain` | `MainActivity` e implementação da plataforma |
| Desktop | `desktopMain` | `Main.kt` e implementação da plataforma |
| Web | `wasmJsMain` | `Main.kt`, `index.html` e implementação da plataforma |

Backend (`ampera-backend`), em `src/main/kotlin/com/ampera`:

| Camada | Pasta |
| --- | --- |
| Controladores REST | `api` |
| DTOs | `dto` |
| Mapper | `mapper` |
| Serviços | `application` |
| Domínio | `domain` |
| Persistência (entidades e repositórios) | `infrastructure/persistence` |
| Segurança e JWT | `infrastructure/security` |
| Migrations e seed | `src/main/resources/db` |

## Instalação, execução e acesso

Requisitos: JDK 17. Para Android, o Android SDK e um emulador (ou um celular). O backend precisa estar rodando em `http://localhost:8080` (veja o README de `ampera-backend`).

Login de demonstração: `demo@ampera.local` / `demo123`. Também é possível criar uma conta na tela de login.

- Desktop: `./gradlew :composeApp:run`
- Web: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` e abra `http://localhost:8081`. No celular, na mesma rede Wi-Fi, abra `http://IP-desta-máquina:8081`; a API é chamada no mesmo IP na porta 8080.
- Android: abra o emulador com `%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe -avd ampera` e rode `./gradlew :composeApp:installDebug`. No emulador a API é `http://10.0.2.2:8080`.

Para o celular alcançar o PC, libere as portas no firewall do Windows (PowerShell como administrador):

```powershell
New-NetFirewallRule -DisplayName "Ampera" -Direction Inbound -Protocol TCP -LocalPort 8080,8081 -Action Allow -Profile Private
```

Builds de distribuição: `./gradlew :composeApp:wasmJsBrowserDistribution` (site estático em `composeApp/build/dist/wasmJs/productionExecutable`) e `./gradlew :composeApp:packageDistributionForCurrentOS` (instalador desktop).
