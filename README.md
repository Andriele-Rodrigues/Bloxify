# 🎮 Bloxify — Brick Breaker Android

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg?logo=android)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-7B1FA2.svg)](https://m3.material.io)

**Bloxify** é um jogo moderno de **Brick Breaker** desenvolvido nativamente para Android utilizando **Kotlin**, **Jetpack Compose** e **Material 3**. O projeto foi estruturado seguindo as melhores práticas de engenharia de software e padrões recomendados pelo Google (**Modern Android Development - MAD**).

---

## 🔗 Link do Repositório Público no GitHub

O projeto está hospedado e versionado publicamente no GitHub:
👉 **[https://github.com/Andriele-Rodrigues/Bloxify](https://github.com/Andriele-Rodrigues/Bloxify)**

---

## 👥 Integrantes da Equipe

| # | Nome do Integrante | Papel / Responsabilidade Principal |
| :-: | :--- | :--- |
| 1 | **Andriele Rodrigues** | Estrutura inicial, MainActivity, rotas e navegação |
| 2 | **Bruno Kunzler Borges** | Motor do jogo (`Ball`, `Paddle`, `GameEngine`, loop de física) |
| 3 | **Felipe Vicentini** | Configurações, persistência, sons (`SoundPool`), integrantes e docs |
| 4 | **Jeferson Duarte** | Matrizes de tijolos, 5 níveis e geração procedural (`LevelGenerator`) |
| 5 | **Matheus Feijó Barp** | Sistema de colisões (`CollisionDetector`), pontuação e estados do jogo |

---

## 📚 Documentação Técnica do Projeto (Índice de Entregáveis)

A documentação detalhada foi dividida em tópicos específicos conforme os critérios de avaliação:

### 📄 [1. Ambiente de Desenvolvimento, Tecnologias e Geração do APK](docs/ambiente-e-apk.md)
* **Peso: 2 Pontos**
* **Conteúdo:** Justificativa da escolha da IDE (Android Studio), linguagem (Kotlin), bibliotecas (Jetpack Compose, Navigation, SoundPool) e guia passo a passo para gerar o APK via linha de comando (`./gradlew assembleDebug`) e via interface gráfica.

### 📄 [2. Apresentação dos Wireframes em Alta Definição](docs/wireframes.md)
* **Peso: 4 Pontos**
* **Conteúdo:** Wireframes estruturados de todas as telas (`HomeScreen`, `SettingsScreen`, `MembersScreen`) e dos 5 níveis do jogo (`GameScreen`), acompanhados de fluxos de navegação e detalhamento dos componentes de interface.

### 📄 [3. Documentação de Métodos de Construção da Parede de Blocos](docs/construcao-paredes-blocos.md)
* **Peso: 2 Pontos**
* **Conteúdo:** Modelagem matemática da grade $5 \times 8$, fórmulas de dimensionamento responsivo, algoritmos de instanciação por matriz e procedural, delimitação das 4 paredes físicas e física de colisão AABB.

---

## 🌳 Fluxo de Branches do Repositório

O repositório adota o modelo **Git Flow**:

```
main (versão final estável)
└── develop (branch integradora de desenvolvimento)
    ├── feature/motor-jogo                (Integrante 2)
    ├── feature/tijolos-niveis            (Integrante 3)
    ├── feature/colisoes-regras           (Integrante 4)
    └── feature/configuracoes-documentacao (Integrante 5)
```

---

## 🚀 Como Executar o Projeto Rapidamente

1. **Clonar o repositório:**
   ```bash
   git clone https://github.com/Andriele-Rodrigues/Bloxify.git
   cd Bloxify
   ```

2. **Compilar e gerar o APK:**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Instalar no emulador ou dispositivo conectado:**
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
