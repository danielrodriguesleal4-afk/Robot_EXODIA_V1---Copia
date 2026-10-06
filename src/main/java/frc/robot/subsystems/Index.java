package frc.robot.subsystems;

// ==================== IMPORTS =================== //

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

// ================================================ //

public class Index extends SubsystemBase {

    // =========================== Motor =============================== //
    
    private final SparkMax esteira = 
        new SparkMax(20, MotorType.kBrushless); // SparkMax 20 (Index)

    // ================================================================ //

    public Index() {

    // ================================== Configuração ================================ //

        SparkMaxConfig configE = new SparkMaxConfig();
        configE
        .idleMode(IdleMode.kCoast)
        .voltageCompensation(12);

        esteira.configure(
            configE, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters
        );

    // ================================================================================ //

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
