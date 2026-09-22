package frc.robot;

/////////////////////////// IMPORTAÇÕES /////////////////////////

import frc.robot.Constants.Controle;
import frc.robot.subsystems.SwerveSubsystem;
import swervelib.SwerveInputStream;

import java.io.File;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Acelerador;
import frc.robot.subsystems.Index;
import frc.robot.subsystems.IntekeAngle;
import frc.robot.subsystems.IntekeRoller;

// ---------------------------------------------------------------->


public class RobotContainer {

    // ==================================================================================================== //
    // ====================================== CLASSES DE SUBSYSTEMAS ====================================== //

    // Aqui iniciamos o swerve
    private SwerveSubsystem swerve = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(), "swerve"));

    // Subsistema do Shooter.
    private final Shooter shooter = new Shooter();

    // Subisistema do Acelerador.
    private final Acelerador acelerador = new Acelerador();

    // Subisistema do Index.
    private final Index index = new Index();

    // Subisistema do Inteke Angulo.
    private final IntekeAngle iAngle = new IntekeAngle();

    // Subisistema do Inteke rolete.
    private final IntekeRoller iRoller = new IntekeRoller();

    // ------------------------------------------------------------------------------------------------------------>

    // É criado o escolhedor de autônomo
    private final SendableChooser<Command> autoChooser; ///////////////////////////////////////////////////////////////////

    // ============================================================================================================ //
    // ============================================ CONTROLES ===================================================== //

    // Controle de Xbox (Piloto)
    private CommandXboxController xboxMovimentacao = new CommandXboxController(Controle.xboxControle);

    // Controle de Xbox N 2 (MECANISMOS)
    private CommandXboxController xboxController = new CommandXboxController(1);

    // ----------------------------------------------------------------------------------------------------------->

    SwerveInputStream driveAngularVelocity = SwerveInputStream.of(swerve.getSwerveDrive(),
            () -> -xboxMovimentacao.getLeftY() * 0.9,
            () -> -xboxMovimentacao.getLeftX() * 0.9)
            .withControllerRotationAxis(() -> xboxMovimentacao.getRightX() * -1)
            .deadband(Constants.Controle.DEADBAND)
            .scaleTranslation(0.8)
            .allianceRelativeControl(true);

    SwerveInputStream lowMode = driveAngularVelocity.copy().scaleTranslation(0.3).scaleRotation(0.5);

    public RobotContainer() {
        // autoCommands = new SetNamedCommands(swerve);
        autoChooser = AutoBuilder.buildAutoChooser(); ////////////////////////////////////////////////////////////////
        SmartDashboard.putData("AutoChooser", autoChooser);
 
        configureBindings();
    }

    // Função onde os eventos (triggers) são configurados
    private void configureBindings() {

        Command driveFieldOrientedAngularVelocity = swerve.driveFieldOriented(driveAngularVelocity);
        Command lowModeCommand = swerve.driveFieldOriented(lowMode);
        swerve.setDefaultCommand(driveFieldOrientedAngularVelocity);

        // ================ Comandos do Mecanismos N 1 ================== //
 
        // --------------------- Inteke Coletor. --------------------- //
        xboxMovimentacao.a().whileTrue(
            Commands.run(() -> iRoller.runIntekeRoller(3000), iRoller)
            .finallyDo(() -> iRoller.stopIntakeRoller())
        );
        // --------------------- Inteke Angulação. ---------------------- //
        // xboxMovimentacao.leftBumper()



        
        // ================ Comandos do Mecanismos N 2 ================== //

        // ---------------- Comando do lançador. ----------------- //
        xboxController.a().whileTrue(
           Commands.run(() -> shooter.runShooter(5000), shooter) // RPM fixo
               .finallyDo(() -> shooter.stopShooter())
        );
        // --------------- Comando do Acelerador. ---------------- //
        xboxController.rightBumper().whileTrue(
            Commands.run(() -> acelerador.runAcelerador(5000), acelerador)
                .finallyDo(() -> acelerador.stopAcelerador())
        );
        // ------------------------------------------------------ // 

        // ================ Comados da esteira. ===================== // 
    
        xboxController.leftBumper().whileTrue(
            Commands.run(() -> index.runIndex(1), index)
                 .finallyDo(() -> index.stopIndex())
        );
        // =========================================================== //
    
    }

    // Função que retorna o autônomo
    public Command getAutonomousCommand() {
        // Aqui retornamos o comando que está no selecionador
        return autoChooser.getSelected();
    }

    // Define os motores como coast ou brake
    public void setMotorBrake(boolean brake) {
        swerve.setMotorBrake(brake);
    }

    public void setHeadingCorrection(boolean heading) {
        swerve.setHeadingCorrection(heading);
    }

    public Command alignSwerveWithAngle(double angleInDegrees) {
        Rotation2d rotationToAlign = Rotation2d.fromDegrees(angleInDegrees);
        SwerveInputStream alignWithAngles = driveAngularVelocity.copy().withControllerHeadingAxis(
                rotationToAlign::getCos,
                rotationToAlign::getSin);
        return swerve.driveFieldOriented(alignWithAngles);
    }

}
