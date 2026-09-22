package frc.robot.subsystems;

// ========================== IMPORTS ======================== //

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.config.SparkMaxConfig;

// =========================================================== //

public class IntekeAngle extends SubsystemBase {

    // ============================ Motores =========================== //

        private final SparkMax angle =
            new SparkMax(16, MotorType.kBrushless); 

    // =============================================================== //


    public IntekeAngle() {

    }

    public void runIntakeAngle() {

    }

    public void stopIntakeAngle() {

    }

}