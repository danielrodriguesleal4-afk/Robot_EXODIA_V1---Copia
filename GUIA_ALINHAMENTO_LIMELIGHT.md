# 🔫 Guia de Alinhamento com Limelight - Robot_EXODIA_V1

## ✅ O que foi implementado

### 1. SwerveSubsystem.java - Novas funções:
- `driveAndAimAtTargetE()` - Comando principal de alinhamento
- `isLimelightHasTarget()` - Verifica se tem alvo visível
- `getLimelightTX()` - Obtém offset horizontal
- `getLimelightTY()` - Obtém offset vertical
- `getDistanceToTarget()` - Calcula distância

### 2. RobotContainer.java - Botão adicionado:
- **Botão X** (no controle do piloto): Ativa alinhamento automático

---

## 🎮 Como Usar

### Para Alinhar com o Hub:

1. **Configure a pipeline da Limelight** para detectar o alvo (AprilTag ou retro-refletivo)
2. **Aponte a câmera para o Hub**
3. **Segure o botão X** no controle do piloto
4. **Mova o joystick** para se posicionar
5. O robô vai **girar automaticamente** para mirar no alvo
6. **Solte o botão** quando estiver alinhado

### Para Zerar Giroscópio:
- **Botão Y** no controle do piloto

---

## 📊 SmartDashboard - Valores de Debug

Durante o alinhamento, você podemonitorar no SmartDashboard:

| Valor | Descrição |
|-------|-----------|
| `Limelight/HasTarget` | true/false - Tem alvo visível |
| `Limelight/TX` | Offset horizontal em graus (-27 a +27) |
| `Limelight/PID_Output` | Saída do PID (velocidade de rotação) |
| `Limelight/Status` | Status atual ("Alinhando..." ou "Sem alvo!") |

---

## ⚙️ Configurações que Você Pode Ajustar

### 1. Velocidade durante alinhamento

No arquivo `SwerveSubsystem.java`, procure:

```java
// Linha ~304-306
Translation2d translation = new Translation2d(
    xInput * swerveDrive.getMaximumChassisVelocity() * 0.5,  // 50% da velocidade
    yInput * swerveDrive.getMaximumChassisVelocity() * 0.5);
```

**Ajuste:** Mude `0.5` para:
- `0.3` = mais devagar (mais preciso)
- `0.7` = mais rápido (mais agressivo)

### 2. Ganho do PID

No arquivo `SwerveSubsystem.java`, procure:

```java
// Linha ~290
public PIDController PDAim = new PIDController(0.1, 0., 0);
```

**Ajuste:** Se o robô oscila muito, diminua o P:
- `0.05` = menos agressivo
- `0.15` = mais agressivo

Se não chega no alvo, aumente:
- `0.2` = mais forte

### 3. Setpoint do PID

O robô está configurado para alinhar no centro (TX = 0):

```java
PDAim.setSetpoint(0);  // Alinha no centro do alvo
```

Se quiser um pouco offset (para compensar algo):

```java
PDAim.setSetpoint(-3);  // Alinha com TX = -3 graus
```

---

## 🔧 Configuração Física da Limelight

### Alturas (em polegadas):

Se você quiser usar o cálculo de distância, configure no código:

```java
// No SwerveSubsystem.java - método getDistanceToTarget()
// Mas por agora, os valores padrão são:
double limelightMountAngleDegrees = 37.0;   // Seu ângulo de montagem
double limelightLensHeightInches = 19.57;   // Sua altura da câmera
double goalHeightInches = 72.0;             // Altura do hub
```

**Para ajustar:** Meça as alturas reais do seu robô e edite o método!

---

## 🐛 Troubleshooting

### Problema: Robô não gira ao apertar X

**Verificações:**
1. ✅ Limelight está ligada? (LED aceso)
2. ✅ Tem alvo visível? (veja no SmartDashboard "Limelight/HasTarget")
3. ✅ TX está mudando? (veja no SmartDashboard "Limelight/TX")

### Problema: Robô gira muito rápido ou oscila

**Solução:** Diminua o P do PID:
```java
public PIDController PDAim = new PIDController(0.05, 0., 0);
```

### Problema: Robô não para de girar

**Solução:** Aumente a tolerância:
```java
PDAim.setTolerance(2.0);  // 2 graus de tolerância
```

### Problema: Alinhamento muito lento

**Solução:** Aumente a velocidade:
```java
// Mude de 0.5 para 0.7
xInput * swerveDrive.getMaximumChassisVelocity() * 0.7
```

---

## 📝 Cálculo de Distância (Para o Shooter)

O código já tem a função para calcular distância. Você pode usar para ajustar a velocidade do shooter dinamicamente!

Exemplo de uso:
```java
// No seu código do shooter
double distancia = swerve.getDistanceToTarget(37.0, 19.57, 72.0);
double rpm = calcularRPM(distancia);  // Você cria essa função
shooter.runShooter(rpm);
```

---

## 🎯 Resumo dos Botões

| Botão | Controle | Função |
|-------|----------|--------|
| X | Piloto | Alinhamento automático com Limelight |
| Y | Piloto | Zerar giroscópio |
| A | Piloto | Ativar intake |
| RT | Piloto | Levantar intake |
| LT | Piloto | Abaixar intake |
| A | Mecanismos | Ativar shooter (5000 RPM) |
| RB | Mecanismos | Acelerador |
| LB | Mecanismos | Index |

---

**Implementado em:** 26/09/2026
**Por:** Claude (assistindo Daniel)