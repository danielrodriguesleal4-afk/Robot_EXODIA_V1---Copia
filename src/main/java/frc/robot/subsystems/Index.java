package frc.robot.subsystems;

// ==================== IMPORTS =================== //

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

// ================================================ //

public class Index extends SubsystemBase {

    // =========================== Motor =============================== //
    
    private final SparkMax esteira = 
        new SparkMax(20, MotorType.kBrushless); // SparkMax 20 (Index)

    // ================================================================ //

    public Index() {

    }

    public void runIndex(double speed) {

        esteira.set(speed);

    }

    public void stopIndex() { // STOPPPPPP

        esteira.set(0);

    }

    @Override
    public void periodic() {

    }

}
