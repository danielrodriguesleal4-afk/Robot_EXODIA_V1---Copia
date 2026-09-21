package frc.robot.subsystems;

// =================== IMPORTAÇÕES ================= //

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import frc.robot.limeligtht.LimelightHelpers;

// ================================================ //

public class Shooter extends SubsystemBase {

    // ============================================================== //
   // ======================== Motores ============================= // 

  private final SparkMax motor1 =
      new SparkMax(12, MotorType.kBrushless); // Motor 12 (Lançador - Esquerdo)

  private final SparkMax motor2 = 
      new SparkMax(18, MotorType.kBrushless); // Motor 18 (Lançador - Direito)

    // --------------------------------------------------------------------- //

  private final SparkClosedLoopController pidControllerS1 = // Ccontrolador motor 12
       motor1.getClosedLoopController();

  private final SparkClosedLoopController pidControllerS2 = // Ccontrolador motor 18
       motor2.getClosedLoopController();
 
    // =============================================================== //

    public Shooter() {

        // ========= Configuração do Motor 12 ======== //

        SparkMaxConfig config1 = new SparkMaxConfig();
        config1.closedLoop.pid(0.0004, 0.0, 0.0);
        motor1.configure(
            config1, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters); // Aplicando a config

        // ------------------------------------------- //

        // ======== Configuração do Motor 18 ========= //

        SparkMaxConfig config2 = new SparkMaxConfig();
        config2.closedLoop.pid(0.0004, 0.0, 0.0);
        motor2.configure(
            config2, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters); // Aplicando a config

        // -------------------------------------------- //

    }

    public void runShooter(double targetRPM) { //

        pidControllerS1.setReference(targetRPM, ControlType.kVelocity); // Motor 12
        pidControllerS2.setReference(-targetRPM, ControlType.kVelocity); // Motor 18
       
    }

    public void stopShooter() { // STOP no SHOOTER

        motor1.set(0.0); // Stopppp
        motor2.set(0.0); // Stopppp

    }

    public double getRPMMotor1() {

        return motor1.getEncoder().getVelocity(); 
    }

    public double getRPMMotor2() {

        return motor2.getEncoder().getVelocity();
    }

    @Override
    public void periodic() { 

        // Valor do RPM para comparativa
        SmartDashboard.putNumber("Shooter RPM Real (Lançador 1)", getRPMMotor1());
        SmartDashboard.putNumber("Shooter RPM Real (Lançador 2)", getRPMMotor2());

    }
  
}