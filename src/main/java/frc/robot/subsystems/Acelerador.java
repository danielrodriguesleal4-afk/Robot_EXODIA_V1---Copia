package frc.robot.subsystems;

// ====================== Importações ======================== //

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.config.SparkMaxConfig;

// ========================================================== //


public class Acelerador extends SubsystemBase {

// ================================ Criação do Motores ============================ //

    private final SparkMax acelerador1 =
       new SparkMax(13, MotorType.kBrushless); // Motor 13 (Acelerador - direito)

    private final SparkMax acelerador2 =
       new SparkMax(14, MotorType.kBrushless); // Motor 14 (Acelerador - Esquerdo)

    // -------------------------------------------------------------------------- //

    public Acelerador() {

        // ---------------------------------- Config Acelerador 1 ------------------------------------- //
        SparkMaxConfig configA1 = new SparkMaxConfig();
        configA1.voltageCompensation(12);
        acelerador1.configure(configA1, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        // ---------------------------------- Config Acelerador 2 ------------------------------------ //
        SparkMaxConfig configA2 = new SparkMaxConfig();
        configA2.voltageCompensation(12);
        acelerador2.configure(configA2, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        // ------------------------------------------------------------------------------------------- //
       
    }

    public void runAcelerador(double speedPercent) {

        acelerador1.set(speedPercent);
        acelerador2.set(speedPercent);

    }

    public void stopAcelerador() {

        acelerador1.set(0.0);
        acelerador2.set(0.0);

    }

    @Override
    public void periodic() { 

    }

}
