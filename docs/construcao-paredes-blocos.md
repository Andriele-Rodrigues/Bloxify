# Métodos de Construção da Parede de Blocos e Paredes do Jogo

> **Critério de Avaliação:** Documentação detalhada de como e quais métodos são utilizados para a construção da parede de blocos, cálculo de dimensões, posicionamento e delimitação de paredes do jogo. (Peso: 2 Pontos)

---

## 1. Modelo Matemático da Grade de Blocos

A disposição dos blocos no **Bloxify** é baseada em uma matriz bidimensional discreta $M \in \mathbb{R}^{R \times C}$, onde:
* $R = 5$ (número fixo de linhas)
* $C = 8$ (número fixo de colunas)

Cada elemento $M[r][c]$ armazena o estado do bloco na coordenada $(r, c)$, onde $M[r][c] \in \{0, 1\}$ (0 indica espaço vazio e 1 indica tijolo ativo).

---

## 2. Métodos de Cálculo Geométrico e Posicionamento

A renderização dos blocos no Compose `Canvas` é proporcional à resolução da tela do dispositivo (`screenWidth` $\times$ `screenHeight`), garantindo perfeita responsividade em qualquer densidade de pixels (DPI).

```
 0,0 --------------------------------------------> screenWidth (X)
  |   |<- margin ->|  gap  |  gap  |<- margin ->|
  |   +------------+-------+-------+------------+
  |   | [Bloco 0,0]|       |       | [Bloco 0,7]| -> topOffset
  |   +------------+-------+-------+------------+
  |   | [Bloco 1,0]|       |       | [Bloco 1,7]|
  |   +------------+-------+-------+------------+
  v
screenHeight (Y)
```

### 2.1. Fórmulas de Dimensão
* **Largura do Bloco ($W_{brick}$):**
  $$W_{brick} = \frac{\text{screenWidth} - (2 \times \text{horizontalPadding}) - ((C - 1) \times \text{brickSpacing})}{C}$$

* **Altura do Bloco ($H_{brick}$):**
  $$H_{brick} = \text{baseHeight} \times \text{scaleFactor}$$
  *(Onde $\text{scaleFactor}$ é derivado da configuração do usuário: Pequeno $= 0.8$, Médio $= 1.0$, Grande $= 1.25$)*.

### 2.2. Posição Absoluta de Cada Bloco $(X_{r,c}, Y_{r,c})$
Para um bloco localizado na linha $r$ e coluna $c$:
$$X_{r,c} = \text{horizontalPadding} + c \times (W_{brick} + \text{brickSpacing})$$
$$Y_{r,c} = \text{topOffset} + r \times (H_{brick} + \text{brickSpacing})$$

---

## 3. Métodos de Construção e Geração dos Níveis

A arquitetura do jogo implementa dois métodos complementares de construção de blocos:

### 3.1. Método Baseado em Matriz Estática (Níveis 1 a 4)
Consiste na definição de matrizes booleanas ou inteiras pré-configuradas. O motor do jogo itera sobre a matriz e instancia objetos `Brick` apenas para posições com valor `1`:

```kotlin
// Exemplo conceitual do método de construção
fun buildLevelFromMatrix(
    matrix: Array<IntArray>,
    color: Color,
    brickWidth: Float,
    brickHeight: Float,
    spacing: Float,
    topOffset: Float,
    padding: Float
): List<Brick> {
    val bricks = mutableListOf<Brick>()
    for (r in matrix.indices) {
        for (c in matrix[r].indices) {
            if (matrix[r][c] == 1) {
                val x = padding + c * (brickWidth + spacing)
                val y = topOffset + r * (brickHeight + spacing)
                bricks.add(
                    Brick(
                        id = "$r-$c",
                        x = x,
                        y = y,
                        width = brickWidth,
                        height = brickHeight,
                        color = color,
                        points = (5 - r) * 20
                    )
                )
            }
        }
    }
    return bricks
}
```

### 3.2. Método Procedural / Aleatório Equilibrado (Nível 5)
Para o nível aleatório, o algoritmo utiliza uma função probabilística que gera blocos respeitando regras de jogabilidade (evitando labirintos impossíveis e garantindo um mínimo de 40% e máximo de 75% de densidade de blocos).

---

## 4. Delimitação das Paredes e Limites do Jogo

O campo de jogo é delimitado por 4 fronteiras físicas no espaço 2D:

| Parede | Coordenada / Limite | Comportamento Físico ao Colidir |
| :--- | :--- | :--- |
| **Parede Esquerda** | $X = 0$ | A bola inverte sua velocidade horizontal ($v_x = -v_x$) |
| **Parede Direita** | $X = \text{screenWidth}$ | A bola inverte sua velocidade horizontal ($v_x = -v_x$) |
| **Teto (Topo)** | $Y = 0$ | A bola inverte sua velocidade vertical ($v_y = -v_y$) |
| **Fundo (Chão)** | $Y = \text{screenHeight}$ | **Zona de Perda:** A bola é perdida, deduzindo 1 vida do jogador. Se as vidas chegarem a 0, dispara o estado de `GameOver`. |

---

## 5. Detecção de Colisão e Destruição de Blocos (AABB)

A detecção entre a esfera da bola ($\text{raio } R$) e o retângulo do bloco utiliza a técnica de **Bounding Box Alinhada aos Eixos (AABB - Axis-Aligned Bounding Box)** com ponto mais próximo (*Clamped Nearest Point*):

1. **Ponto mais próximo do centro da bola ao retângulo do bloco:**
   $$X_{closest} = \max(X_{brick}, \min(X_{ball}, X_{brick} + W_{brick}))$$
   $$Y_{closest} = \max(Y_{brick}, \min(Y_{ball}, Y_{brick} + H_{brick}))$$

2. **Cálculo da Distância Euclidiana:**
   $$D^2 = (X_{ball} - X_{closest})^2 + (Y_{ball} - Y_{closest})^2$$

3. **Verificação de Impacto:**
   * Se $D^2 \le R^2$, ocorreu colisão!
   * O bloco é removido da lista ativa de tijolos (`destroyed = true`).
   * A pontuação é incrementada e o vetor de velocidade $(v_x, v_y)$ da bola é refletido de acordo com a face de impacto (topo, base ou laterais).
