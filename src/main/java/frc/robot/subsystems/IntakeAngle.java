package frc.robot.subsystems;

// ========================== IMPORTS ======================== //

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;

// =========================================================== //

public class IntakeAngle extends SubsystemBase {

    // ============================ Motores e Encoder. =========================== //

        private final SparkFlex angle = new SparkFlex(16, MotorType.kBrushless);

    // =============================================================== //


    public IntakeAngle() {

    }

    public void runIntakeAngle(double speed) {

        angle.set(speed);
    }

    public void stopIntakeAngle() {

        angle.set(0);
    }

}