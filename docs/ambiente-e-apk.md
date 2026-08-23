#  Ambiente de Desenvolvimento, Tecnologias e Geração do APK

> **Critério de Avaliação:** Escolha do ambiente de desenvolvimento, tecnologias, linguagens utilizadas e processo de geração do arquivo APK para entrega. (Peso: 2 Pontos)

---

## 1. Escolha do Ambiente de Desenvolvimento

Para o desenvolvimento do projeto **Bloxify (Brick Breaker)**, foi padronizado o ecossistema oficial e moderno da Google para desenvolvimento Android nativo:

* **IDE Oficial:** **Android Studio Ladybug / Koala**
  * **Motivação da Escolha:** Suporte nativo completo ao Jetpack Compose com *Live Edit*, *Interactive Preview*, inspeção de layout em tempo real (*Layout Inspector*) e integração automatizada com Gradle Version Catalogs (`libs.versions.toml`).
* **Sistema de Build:** **Gradle (Kotlin DSL / Groovy)**
  * Automação de dependências, compilação de código nativo, minificação e geração dos pacotes de distribuição (APK e AAB).
* **JDK:** **Java Development Kit (JDK) 17 / 21 (LTS)**
  * Padrão exigido pelo Android Gradle Plugin (AGP 8.x) para compilação moderna e alta performance.
* **Sistema Operacional de Desenvolvimento:** Multiplataforma (Linux, macOS, Windows).
* **Controle de Versão:** **Git & GitHub**
  * Repositório centralizado com modelo de branches estruturado (`develop` como branch integradora e branches de feature individuais).

---

## 2. Linguagens e Tecnologias Utilizadas

O projeto foi concebido seguindo a arquitetura moderna recomendada pelo Google Android (**Modern Android Development - MAD**), sem uso de frameworks externos de terceiros (como Unity, Flutter ou Flame), garantindo domínio total das APIs nativas.

### 2.1. Linguagem Principal
* **Kotlin (versão 1.9+)**:
  * Linguagem oficial para desenvolvimento Android nativo.
  * **Vantagens aplicadas:** *Null-safety* em tempo de compilação, funções de alta ordem e lambdas para renderização de loops de jogo, *data classes* imutáveis para modelagem física (`Ball`, `Paddle`, `Brick`) e *Coroutines* para operações assíncronas e loop de frames (60 FPS).

### 2.2. Interface de Usuário (UI) & Renderização
* **Jetpack Compose & Material 3 (`androidx.compose.material3`)**:
  * Framework declarativo e moderno de interface reativa do Android.
  * Renderização de telas por meio de funções `@Composable` e desenho de primitivas gráficas do jogo via Compose `Canvas` (`drawRect`, `drawCircle`, `drawIntoCanvas`).
* **Jetpack Navigation Compose (`androidx.navigation:navigation-compose`)**:
  * Gerenciamento centralizado de rotas e pilha de navegação entre as telas (`home`, `game`, `settings`, `members`).

### 2.3. Áudio e Multimídia
* **Android SoundPool (`android.media.SoundPool`)**:
  * API nativa de baixa latência da plataforma Android para reprodução de efeitos sonoros imediatos e polifônicos (sons de colisão e início de fase) com `AudioAttributes` voltados para jogos (`USAGE_GAME`, `CONTENT_TYPE_SONIFICATION`).

### 2.4. Persistência de Dados
* **Jetpack DataStore / SharedPreferences**:
  * Armazenamento chave-valor assíncrono para persistir as preferências do usuário (cor dos blocos, tamanho dos tijolos e configurações de áudio) de forma segura e reativa com `StateFlow`.

---

## 3. Como Gerar o Arquivo APK para Entrega

O projeto está configurado para permitir a geração de APKs tanto via **Linha de Comando (CLI / Terminal)** quanto pela interface gráfica do **Android Studio**.

### 3.1. Pré-requisitos
* Ter o repositório clonado localmente:
  ```bash
  git clone https://github.com/Andriele-Rodrigues/Bloxify.git
  cd Bloxify
  ```
* JDK 17+ instalado e configurado na variável de ambiente `JAVA_HOME`.

---

### 3.2. Método 1: Geração do APK via Linha de Comando (Recomendado)

O projeto contém o executável Gradle Wrapper (`gradlew` para Linux/macOS ou `gradlew.bat` para Windows).

#### Passo 1: Gerar o APK de Debug (Desenvolvimento e Teste)
Execute no terminal na raiz do projeto:

* **No Linux / macOS:**
  ```bash
  ./gradlew assembleDebug
  ```
* **No Windows (PowerShell / Prompt):**
  ```cmd
  gradlew.bat assembleDebug
  ```

* **Localização do APK gerado:**
  O arquivo APK compilado estará disponível no caminho:
  ```
  app/build/outputs/apk/debug/app-debug.apk
  ```

---

#### Passo 2: Gerar o APK de Release (Produção / Entrega Final)
Para gerar o pacote otimizado e minificado:

* **No Linux / macOS:**
  ```bash
  ./gradlew assembleRelease
  ```
* **No Windows:**
  ```cmd
  gradlew.bat assembleRelease
  ```

* **Localização do APK gerado:**
  ```
  app/build/outputs/apk/release/app-release-unsigned.apk
  ```

---

### 3.3. Método 2: Geração do APK pelo Android Studio (Interface Gráfica)

1. Abra o projeto no **Android Studio**.
2. Aguarde a sincronização do Gradle (*Gradle Sync Finished*).
3. No menu superior, clique em: **`Build`** ➔ **`Build Bundle(s) / APK(s)`** ➔ **`Build APK(s)`**.
4. Quando a compilação finalizar, uma notificação aparecerá no canto inferior direito. Clique no link azul **`locate`** para abrir a pasta com o arquivo `app-debug.apk`.

---

### 3.4. Como Instalar o APK no Dispositivo ou Emulador

Para instalar o APK diretamente em um dispositivo físico com depuração USB ou emulador Android aberto:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
