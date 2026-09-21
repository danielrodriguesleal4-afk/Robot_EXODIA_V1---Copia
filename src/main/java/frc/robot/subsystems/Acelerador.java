package frc.robot.subsystems;

// ====================== Importações ======================== //

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// ========================================================== //


public class Acelerador extends SubsystemBase {

// ====================== Criação do Motores e PID ================== //

    private final SparkMax acelerador1 =
       new SparkMax(13, MotorType.kBrushless); // Motor 13 (Acelerador - direito)

    private final SparkMax acelerador2 =
       new SparkMax(14, MotorType.kBrushless); // Motor 14 (Acelerador - Esquerdo)

    // ------------------------------------------------------------->

    private final SparkClosedLoopController pidControllerA1 = // Ccontrolador motor 12
       acelerador1.getClosedLoopController();

  private final SparkClosedLoopController pidControllerA2 = // Ccontrolador motor 18
       acelerador2.getClosedLoopController();
 
// ==================================================================== //

    public Acelerador() {

    // ======================= Configuração do A1 ===================== //

    SparkMaxConfig configA1 = new SparkMaxConfig();
    configA1.closedLoop.pid(0.0004, 0.0, 0.0);
    acelerador1.configure(
        configA1, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters); // Aplicando a config

    // ====================== Configuração do A2 ====================== //

    SparkMaxConfig configA2 = new SparkMaxConfig();
    configA2.closedLoop.pid(0.0004, 0.0, 0.0);
    acelerador2.configure(
        configA2, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters); // Aplicando a config

    // =============================================================== //

    }

    public void runAcelerador(double targetRPM) {

        pidControllerA1.setReference(targetRPM, ControlType.kVelocity); // PCA1 (Motor )
        pidControllerA2.setReference(-targetRPM, ControlType.kVelocity); // PCA2 (Motor )

    }

    public void stopAcelerador() {

        acelerador1.set(0.0);
        acelerador2.set(0.0);

    }

    public double getRPMAcelerador1() {

        return acelerador1.getEncoder().getVelocity(); 
    }

    public double getRPMAcelerador2() {

        return acelerador2.getEncoder().getVelocity(); 
    }

    @Override
    public void periodic() { 

        // Valor do RPM para comparativa
        SmartDashboard.putNumber("Acelerador RPM real (13 direito)", getRPMAcelerador1());
        SmartDashboard.putNumber("Acelerador RPM real (14 esquerdo)", getRPMAcelerador2());
    }

}
