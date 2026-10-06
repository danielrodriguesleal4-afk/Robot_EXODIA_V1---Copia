# Guia de Implementação - Proteção de Limites do Intake Angle

## 📋 Resumo das Mudanças

O arquivo `IntakeAngle.java` foi completamente reformulado com proteção de limites usando o encoder integrado do SparkFlex. O objetivo é **impedir que o mecanismo de angulação ultrapasse os limites físicos** e cause danos ao robô.

---

## 🔍 Principais Mudanças Explicadas

### 1️⃣ **IMPORTAÇÕES NOVAS**

```java
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
```

**O que faz:**
- `IdleMode`: Permite escolher entre BRAKE (freia) ou COAST (solto) quando o motor para
- `SmartDashboard`: Envia dados para o dashboard do driver

**Por que é importante:**
- BRAKE mantém o intake na posição escolhida (recomendado)
- COAST permite mexer manualmente (se precisar diagnosticar)

---

### 2️⃣ **CONSTANTES DE LIMITE**

```java
private static final double MIN_POSITION = 0.0;      // Posição mais baixa
private static final double MAX_POSITION = 45.0;     // Posição mais alta
private static final double TOLERANCIA_LIMITE = 0.5; // Zona de segurança
private static final double CURRENT_LIMIT = 25.0;    // Proteção de corrente
```

**O que faz:**
- Define onde o intake pode se mover (entre 0 e 45 rotações)
- TOLERANCIA_LIMITE = quando chegar perto do limite, já começa a frear
- CURRENT_LIMIT = se a corrente passar de 25A, o motor é desligado automaticamente

**⚠️ CALIBRAÇÃO NECESSÁRIA:**
- MIN_POSITION e MAX_POSITION **precisam ser ajustados para seu robô**
- Veja a seção "Como Calibrar" abaixo

---

### 3️⃣ **CONFIGURAÇÃO DO MOTOR (Construtor)**

```java
config.inverted(false);                           // Direção do motor
config.idleMode(IdleMode.kBrake);                // Motor freia quando para
config.smartCurrentLimit((int) CURRENT_LIMIT);   // Limite de corrente
```

**O que faz:**
- `inverted(false)`: Motor gira normalmente. Se subir quando deveria descer, mude para `true`
- `kBrake`: Motor vai frear quando você soltar o botão (mantém posição)
- `smartCurrentLimit`: Protege de sobrecarga se o mecanismo travar

**Diferença do código antigo:**
- Antes: Nenhuma configuração
- Agora: Motor protegido e configurado corretamente

---

### 4️⃣ **MÉTODO PRINCIPAL: `runIntakeAngle(double speed)`**

```java
public void runIntakeAngle(double speed) {
    double currentPosition = angleMotor.getEncoder().getPosition();
    
    // Se quer subir mas já está no topo
    if (speed > 0 && currentPosition >= MAX_POSITION - TOLERANCIA_LIMITE) {
        angleMotor.set(0.0);
        return;
    }
    
    // Se quer descer mas já está no fundo
    if (speed < 0 && currentPosition <= MIN_POSITION + TOLERANCIA_LIMITE) {
        angleMotor.set(0.0);
        return;
    }
    
    // Se passou nas verificações, pode mover!
    angleMotor.set(speed);
}
```

**O que faz:**
✅ Verifica a posição atual do encoder
✅ Se chegou no limite, **para o motor**
✅ Se não chegou, permite mover

**Por que é importante:**
- Evita que o motor force contra os limites mecânicos
- Protege engrenagens e componentes de desgaste prematuro
- Reduz consumo de corrente ao parar no limite

---

### 5️⃣ **NOVOS MÉTODOS ÚTEIS**

#### `moveUp()` e `moveDown()`
```java
public void moveUp() {
    runIntakeAngle(DEFAULT_SPEED);  // 0.15 = 15%
}

public void moveDown() {
    runIntakeAngle(-DEFAULT_SPEED);
}
```
**Uso:** Simplifica comandos, não precisa passar velocidade

#### `getPosition()`, `isAtMinPosition()`, `isAtMaxPosition()`
```java
double pos = iAngle.getPosition();        // Posição atual
boolean minimo = iAngle.isAtMinPosition(); // Chegou no fundo?
boolean maximo = iAngle.isAtMaxPosition(); // Chegou no topo?
```
**Uso:** Útil para criar lógicas mais complexas (ex: parar automático)

#### `resetPosition()`
```java
iAngle.resetPosition();  // Zera o encoder na posição atual
```
**Uso:** CALIBRAÇÃO - chame isso quando o intake estiver na posição mais baixa

---

### 6️⃣ **MÉTODO PERIÓDICO: `periodic()`**

```java
@Override
public void periodic() {
    SmartDashboard.putNumber("Intake/Position", getPosition());
    SmartDashboard.putNumber("Intake/Velocity RPM", getVelocity());
    SmartDashboard.putNumber("Intake/Current Amps", getCurrent());
    SmartDashboard.putBoolean("Intake/At Min Limit", isAtMinPosition());
    SmartDashboard.putBoolean("Intake/At Max Limit", isAtMaxPosition());
    
    if (SmartDashboard.getBoolean("Intake/Reset Position", false)) {
        resetPosition();
        SmartDashboard.putBoolean("Intake/Reset Position", false);
    }
}
```

**O que faz:**
- Envia dados **em tempo real** para o SmartDashboard
- Permite **resetar o encoder direto do dashboard** (clicando em um valor)
- Mostra corrente, velocidade e posição

**Por que é importante:**
- Driver pode ver exatamente onde está o intake
- Facilita diagnóstico de problemas
- Essencial para calibração

---

## 🛠️ COMO CALIBRAR

### Passo 1: Compilar e Fazer Deploy
1. Abra o projeto no VS Code / WPILib
2. Compile: `Ctrl + Shift + P` → `WPILib: Build Robot Code`
3. Deploy: `Ctrl + Shift + P` → `WPILib: Deploy Robot Code`

### Passo 2: Acessar o SmartDashboard
1. Coloque o robô em **modo TEST**
2. Abra o SmartDashboard: `Ctrl + Shift + P` → `WPILib: Start Tool` → `SmartDashboard`
3. Procure por "Intake/Position"

### Passo 3: Encontrar Posição Mínima
1. Mova o intake para a **posição mais baixa segura** (usando os triggers do Xbox)
2. Anote o valor de "Intake/Position" no SmartDashboard
3. Abra o arquivo `IntakeAngle.java`
4. Altere: `private static final double MIN_POSITION = VALOR_ANOTADO;`

### Passo 4: Encontrar Posição Máxima
1. Mova o intake para a **posição mais alta segura**
2. Anote o valor de "Intake/Position"
3. Altere: `private static final double MAX_POSITION = VALOR_ANOTADO;`

### Passo 5: Resetar o Encoder
1. Mova o intake para a posição mais baixa (MIN)
2. No SmartDashboard, clique em "Intake/Reset Position" e mude para `true`
3. O encoder será zerado nessa posição

### Passo 6: Recompile e Faça Deploy
1. Compile novamente com os novos valores
2. Faça deploy
3. Teste! Os triggers devem:
   - ✅ Subir quando apertar trigger direito
   - ✅ Parar ao chegar no topo
   - ✅ Descer quando apertar trigger esquerdo
   - ✅ Parar ao chegar no fundo

---

## 📊 Exemplo de Leitura do SmartDashboard

Quando calibrado corretamente, você verá algo assim:

```
Intake/Position:        15.2 rotações (posição atual)
Intake/Velocity RPM:    150 RPM (velocidade atual)
Intake/Current Amps:    2.5 A (corrente baixa = normal)
Intake/At Min Limit:    false (não está no fundo)
Intake/At Max Limit:    false (não está no topo)
```

**Se aparecer isto, ATENÇÃO:**
```
Intake/Current Amps:    40+ A (muito alta!)
```
= Motor pode estar travado. Desligue o robô!

---

## ⚡ RobotContainer.java - NÃO PRECISA MUDAR!

O arquivo `RobotContainer.java` já está 100% compatível:

```java
xboxMovimentacao.rightTrigger().whileTrue(
    Commands.run(() -> iAngle.runIntakeAngle(0.15), iAngle)
    .finallyDo(() -> iAngle.stopIntakeAngle())
);
```

Continua funcionando normalmente! A proteção de limites funciona **automaticamente** dentro de `runIntakeAngle()`.

---

## 🎯 Resumo Rápido

| Antes | Depois |
|-------|--------|
| ❌ Sem limite de posição | ✅ Com limite de posição |
| ❌ Sem proteção de corrente | ✅ Proteção de corrente |
| ❌ Sem configuração | ✅ Configurado com BRAKE |
| ❌ Sem dados no dashboard | ✅ Dados em tempo real |
| ❌ Sem forma de calibrar | ✅ Calibração fácil |

---

## 🐛 Troubleshooting

**Problema:** O intake não sobe/desce
- **Solução 1:** Verifique se `inverted(false)` está correto. Mude para `true` se necessário.
- **Solução 2:** Confirme que o motor ID 22 está correto

**Problema:** O intake para antes do esperado
- **Solução:** Os valores de MIN_POSITION e MAX_POSITION precisam ser recalibrados

**Problema:** Corrente muito alta (40+ A)
- **Solução:** Motor está travado. Aumente CURRENT_LIMIT ou verifique mecanismo

**Problema:** SmartDashboard não mostra nada
- **Solução:** Verifique se o robô está em modo TEST e se fez deploy

---

## 📝 Próximos Passos

1. ✅ Copiar o novo arquivo `IntakeAngle.java`
2. ✅ Compilar e fazer deploy
3. ✅ Calibrar MIN_POSITION e MAX_POSITION
4. ✅ Testar com os triggers do Xbox
5. ✅ Verificar se para nos limites

**Pronto! Seu intake agora está protegido! 🎉**
