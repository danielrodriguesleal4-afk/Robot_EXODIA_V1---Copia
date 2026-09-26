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

}