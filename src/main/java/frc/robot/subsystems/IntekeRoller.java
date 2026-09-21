package frc.robot.subsystems;

// =========================== IMPORTS ============================ //

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.config.SparkMaxConfig;

// ================================================================ //

public class IntekeRoller extends SubsystemBase {

    // ============================ Motores ======================== //

        private final SparkMax roller = 
            new SparkMax(17, MotorType.kBrushless);

        private final SparkClosedLoopController pidControllerIR =
            roller.getClosedLoopController();


    // ============================================================= //
 
    public IntekeRoller() {

        // ======================= Config RPM ======================= //

        SparkMaxConfig configIR = new SparkMaxConfig();
        configIR.closedLoop.pid(0.0004, 0.0, 0.0);
        roller.configure(
            configIR, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters); // Aplicando a config

        // ========================================================== //


    }

    public void runIntekeRoller(double targetRPM) {

        pidControllerIR.setReference(targetRPM, ControlType.kVelocity); // 
    }

    public void stopIntakeRoller() {

        roller.set(0.0);

    }



}
