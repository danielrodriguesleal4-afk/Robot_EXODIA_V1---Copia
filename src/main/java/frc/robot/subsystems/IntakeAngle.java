package frc.robot.subsystems;

// ========================== IMPORTS ======================== //

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// =========================================================== //

public class IntakeAngle extends SubsystemBase {

    // ============================ Motores e Encoder. =========================== //

        private final SparkFlex angle = 
            new SparkFlex(22, MotorType.kBrushless); // Motor 22 Angulo.

    // =========================================================================== //


    public IntakeAngle() {

        SparkMaxConfig configIA = new SparkMaxConfig();
        configIA
        .idleMode(IdleMode.kBrake)
        .voltageCompensation(12);

        angle.configure(
            configIA, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters
        );

    }

    public void runIntakeAngle(double speed) {

        angle.set(speed);
    }

    public void stopIntakeAngle() {

        angle.set(0);
    }

    // Retorna a posição atual do encoder (em rotações)
    public double getPosition() {
        return angle.getEncoder().getPosition();
    }

    // Retorna a velocidade atual (em RPM)
    public double getVelocity() {
        return angle.getEncoder().getVelocity();
    }

    // Retorna a corrente atual do motor (em Amperes)
    public double getCurrent() {
        return angle.getOutputCurrent();
    }

    // Zera a posição do encoder (use para calibração)
    public void resetPosition() {
        angle.getEncoder().setPosition(0.0);
    }

    @Override
    public void periodic() {
        // Envia dados para o Shuffleboard/SmartDashboard
        SmartDashboard.putNumber("IntakeAngle/Position", getPosition());
        SmartDashboard.putNumber("IntakeAngle/Velocity RPM", getVelocity());
        SmartDashboard.putNumber("IntakeAngle/Current Amps", getCurrent());
        SmartDashboard.putNumber("IntakeAngle/Applied Output", angle.getAppliedOutput());

        // Botão para zerar encoder (mude para true no Shuffleboard para zerar)
        if (SmartDashboard.getBoolean("IntakeAngle/Reset Position", false)) {
            resetPosition();
            SmartDashboard.putBoolean("IntakeAngle/Reset Position", false);
        }
    }

}