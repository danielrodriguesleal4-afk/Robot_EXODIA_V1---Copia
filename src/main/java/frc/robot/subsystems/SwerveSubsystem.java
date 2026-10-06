package frc.robot.subsystems;

import java.io.File;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants;
import frc.robot.Constants.SwerveConfigs;
import frc.robot.limeligtht.LimelightHelpers;
import swervelib.SwerveController;
import swervelib.SwerveDrive;
import swervelib.math.SwerveMath;
import swervelib.telemetry.SwerveDriveTelemetry;
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity;
import swervelib.parser.SwerveDriveConfiguration;
import swervelib.parser.SwerveParser;

/**
 * Classe de subsistema onde fazemos a ponte do nosso código para YAGSL
 */
public class SwerveSubsystem extends SubsystemBase {
  // Objeto global da SwerveDrive (Classe YAGSL)
  SwerveDrive swerveDrive;
  XboxController control = new XboxController(0);

  Constants constants = new Constants();

  // Método construtor da classed
  public SwerveSubsystem(File directory) {
    // Seta a telemetria como nível mais alto
    SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;

    // Acessa os arquivos do diretório .JSON
    try {
      swerveDrive = new SwerveParser(directory).createSwerveDrive(Constants.Dimensoes.MAX_SPEED);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    swerveDrive.setHeadingCorrection(Constants.SwerveConfigs.headingCorrection);
    swerveDrive.angularVelocityCorrection = SwerveConfigs.usarCorrecaoDesvioVelocidadeAngular;
    swerveDrive.angularVelocityCoefficient = SwerveConfigs.coeficienteCorecaoAngVel;
    setupPathPlanner();
  }

  @Override
  public void periodic() {
    // Dentro da função periódica atualizamos nossa odometria
    swerveDrive.updateOdometry();
   }
  

  public void setupPathPlanner() {
    // Load the RobotConfig from the GUI settings. You should probably
    // store this in your Constants file
    RobotConfig config;
    try {
      config = RobotConfig.fromGUISettings();

      final boolean enableFeedforward = false;
      // Configure AutoBuilder last
      AutoBuilder.configure(
          swerveDrive::getPose,
          // Robot pose supplier
          swerveDrive::resetOdometry,
          // Method to reset odometry (will be called if your auto has a starting pose)
          swerveDrive::getRobotVelocity,
          // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
          (speedsRobotRelative, moduleFeedForwards) -> {
            if (enableFeedforward) {
              swerveDrive.drive(
                  speedsRobotRelative,
                  swerveDrive.kinematics.toSwerveModuleStates(speedsRobotRelative),
                  moduleFeedForwards.linearForces());
            } else {
              swerveDrive.setChassisSpeeds(speedsRobotRelative);
            }
          },
          // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds. Also
          // optionally outputs individual module feedforwards
          new PPHolonomicDriveController(
              // PPHolonomicController is the built in path following controller for holonomic
              // drive trains
              new PIDConstants(0.002, 0.0, 0.0),
              // Translation PID constants
              new PIDConstants(0.002, 0.0, 0.0)
          // Rotation PID constants
          ), // estava em 5.0
          config,
          // The robot configuration
          () -> {
            // Boolean supplier that controls when the path will be mirrored for the red
            // alliance
            // This will flip the path being followed to the red side of the field.
            // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

            var alliance = DriverStation.getAlliance();
            if (alliance.isPresent()) {
              return alliance.get() == DriverStation.Alliance.Red;
            }
            return false;
          },
          this
      // Reference to this subsystem to set requirements
      );

    } catch (Exception e) {
      // Handle exception as needed
      e.printStackTrace();
    }
  }

  // Movimenta o robô com o joystick esquerdo, e mira o robo no ângulo no qual o
  // joystick está apontando
  public Command driveCommandAlinharComJoystick(DoubleSupplier translationX, DoubleSupplier translationY,
      DoubleSupplier headingX,
      DoubleSupplier headingY, BooleanSupplier trigger) {
    return run(() -> {
      boolean boost = trigger.getAsBoolean();
      double xInput = translationX.getAsDouble() * 0.6;
      double yInput = translationY.getAsDouble() * 0.6;

      if (boost) {
        xInput = translationX.getAsDouble();
        yInput = translationY.getAsDouble();
      }

      // Faz o robô se mover
      driveFieldOriented(swerveDrive.swerveController.getTargetSpeeds(xInput, yInput,
          headingX.getAsDouble(),
          headingY.getAsDouble(),
          swerveDrive.getYaw().getRadians(),
          swerveDrive.getMaximumChassisVelocity()));
    });
  }

  // Movimenta o robô com o joystick esquerdo, e gira o robô na intensidade na
  // qual o joystick direito está para o lado
  public Command driveCommand(DoubleSupplier translationX, DoubleSupplier translationY,
      DoubleSupplier angularRotationX) {
    return run(() -> {
      double xInput = Math.pow(translationX.getAsDouble(), 3);
      double yInput = Math.pow(translationY.getAsDouble(), 3);
      // Faz o robô se mover
      swerveDrive.drive(new Translation2d(xInput * swerveDrive.getMaximumChassisVelocity(),
          yInput * swerveDrive.getMaximumChassisVelocity()),
          angularRotationX.getAsDouble() * swerveDrive.getMaximumChassisAngularVelocity(),
          true,
          false);
    });
  }

  public Command alignInDegress(DoubleSupplier y, DoubleSupplier x, double setPoint) {
    return run(() -> {
      double angularVelocity = swerveDrive.swerveController.headingCalculate(getHeading().getRadians(),
          Units.degreesToRadians(setPoint));
      double vY = y.getAsDouble() * swerveDrive.getMaximumChassisAngularVelocity();
      double vX = x.getAsDouble() * swerveDrive.getMaximumChassisAngularVelocity();

      ChassisSpeeds goalVelocity = new ChassisSpeeds(vX, vY, angularVelocity);
      swerveDrive.driveFieldOriented(goalVelocity);
    });
  }

  public void driveFieldOriented(ChassisSpeeds velocity) {
    swerveDrive.driveFieldOriented(velocity);
  }

  public Command driveFieldOriented(Supplier<ChassisSpeeds> velocity) {
    return run(() -> {
      swerveDrive.driveFieldOriented(velocity.get());
    });
  }

  // Função drive que chamamos em nossa classe de comando Teleoperado
  public void drive(Translation2d translation, double rotation, boolean fieldRelative) {
    swerveDrive.drive(translation, rotation, fieldRelative, false);
  }

  public void swerveStop() {
    drive(new Translation2d(0.0, 0.0), 0.0, true);
  }

  // Função para obter a velocidade desejada a partir dos inputs do gamepad
  public ChassisSpeeds getTargetSpeeds(double xInput, double yInput, double headingX, double headingY) {
    return swerveDrive.swerveController.getTargetSpeeds(xInput, yInput, headingX, headingY,
        getHeading().getRadians());
  }

  public ChassisSpeeds getTargetSpeeds(double xInput, double yInput) {
    return new ChassisSpeeds(xInput, yInput, 0);
  }

  public ChassisSpeeds getTargetSpeeds(double xInput, double yInput, double headingX, double headingY,
      double maxSpeed) {
    Translation2d scaledInputs = SwerveMath.cubeTranslation(new Translation2d(xInput, yInput));
    return swerveDrive.swerveController.getTargetSpeeds(scaledInputs.getX(),
        scaledInputs.getY(),
        headingX,
        headingY,
        getHeading().getRadians(),
        maxSpeed);
  }

  // Função que retorna a posição do robô (translação e ângulo), (Usado no
  // autônomo)
  public Pose2d getPose() {
    return swerveDrive.getPose();
  }

  // Retorna a velocidade relativa ao campo
  public ChassisSpeeds getFieldVelocity() {
    return swerveDrive.getFieldVelocity();
  }

  // Retorna a configuração do swerve
  public SwerveDriveConfiguration getSwerveDriveConfiguration() {
    return swerveDrive.swerveDriveConfiguration;
  }

  // Retorna o objeto de controle, o qual é usado para acessar as velocidades
  // máximas por exemplo
  public SwerveController getSwerveController() {
    return swerveDrive.getSwerveController();
  }

  // Ângulo atual do robô
  public Rotation2d getHeading() {
    return swerveDrive.getYaw();
  }

  // Reseta a odometria para uma posição indicada (Usado no autônomo)
  public void resetOdometry(Pose2d posicao) {
    swerveDrive.resetOdometry(posicao);
  }

  // Seta a velocidade do chassi (Usado no autônomo)
  public void setChassisSpeeds(ChassisSpeeds chassisSpeeds) {
    swerveDrive.setChassisSpeeds(chassisSpeeds);
  }

  public void setMotorBrake(boolean brake) {
    swerveDrive.setMotorIdleMode(brake);
  }

  public void zeroGyro() {
    swerveDrive.zeroGyro();
  }

  public ChassisSpeeds getRobotVelocity() {
    return swerveDrive.getRobotVelocity();
  }

  public void setHeadingCorrection(boolean heading) {
    swerveDrive.setHeadingCorrection(heading, 1);
  }

  public SwerveDrive getSwerveDrive() {
    return swerveDrive;
  }

  public Command getAutonomousCommand(String pathName, boolean setOdomToStart) {

    // Create a path following command using AutoBuilder. This will also trigger
    // event markers.
    return new PathPlannerAuto(pathName);
  }

CommandXboxController controlAim = new CommandXboxController(0);
  public PIDController PDAim = new PIDController(0.1, 0., 0);

  /**
   * Comando para mover o robô enquanto alinha com a Limelight.
   * O robô se move com os sticks E gira automaticamente para mirar no alvo.
   *
   * @param translationX Supplier do input X do joystick (movimento lateral)
   * @param translationY Supplier do input Y do joystick (movimento frente/trás)
   * @param txSupplier Supplier do valor TX da Limelight (offset horizontal)
   * @return Comando que combina movimento + alinhamento
   */
  public Command driveAndAimAtTargetE(
      DoubleSupplier translationX,
      DoubleSupplier translationY,
      DoubleSupplier txSupplier // tx da Limelight
  ) {
    return run(() -> {

      // ========== VERIFICAÇÃO DE ALVO PRIMEIRO ========== //
      // Se não tem alvo visível, para o robô e sai
      if (!LimelightHelpers.getTV("limelight")) {
        swerveStop();
        SmartDashboard.putBoolean("Limelight/HasTarget", false);
        SmartDashboard.putString("Limelight/Status", "Sem alvo!");
        return;
      }

      SmartDashboard.putBoolean("Limelight/HasTarget", true);

      // ========== MOVIMENTO DO JOYSTICK ========== //
      // Cubic scaling para movimento suave
      double xInput = Math.pow(translationX.getAsDouble(), 3);
      double yInput = Math.pow(translationY.getAsDouble(), 3);

      // Velocidade linear (30% durante alinhamento - ajuste se quiser mais rápido)
      Translation2d translation = new Translation2d(
          xInput * swerveDrive.getMaximumChassisVelocity() * 0.5,
          yInput * swerveDrive.getMaximumChassisVelocity() * 0.5);

      // ========== PID DE ALINHAMENTO ========== //
      double tx = txSupplier.getAsDouble(); // erro em graus (-27 a +27)

      // Configura o PID para alinhar em 0 graus (centro do alvo)
      PDAim.setSetpoint(0);  // 0 =对准中心
      PDAim.setTolerance(1.0);  // 1 grau de tolerância

      // Calcula a velocidade angular necessária
      double omega = PDAim.calculate(tx);

      // Limita a rotação para não girar muito rápido
      omega = MathUtil.clamp(
          omega,
          -swerveDrive.getMaximumChassisAngularVelocity(),
          swerveDrive.getMaximumChassisAngularVelocity());

      // ========== DEBUG NO SMARTDASHBOARD ========== //
      SmartDashboard.putNumber("Limelight/TX", tx);
      SmartDashboard.putNumber("Limelight/PID_Output", omega);
      SmartDashboard.putString("Limelight/Status", "Alinhando...");

      // ========== APLICA NO SWERVE ========== //
      // fieldRelative = true para manter controle normal do campo
      drive(translation, -omega, true);
    });
  }

  /**
   * Para o robô (usado quando perde o alvo ou termina alinhamento)
   */

  /**
   * Método simples para verificar se a Limelight está vendo um alvo.
   * Útil para usar em outros comandos.
   *
   * @return true se tem alvo visível, false se não
   */
  public boolean isLimelightHasTarget() {
    return LimelightHelpers.getTV("limelight");
  }

  /**
   * Obtém o ângulo horizontal do alvo em graus.
   *
   * @return TX em graus (-27 a +27)
   */
  public double getLimelightTX() {
    return LimelightHelpers.getTX("limelight");
  }

  /**
   * Obtém o ângulo vertical do alvo em graus.
   * Útil para calcular distância.
   *
   * @return TY em graus (-20.5 a +20.5)
   */
  public double getLimelightTY() {
    return LimelightHelpers.getTY("limelight");
  }

  /**
   * Calcula a distância até o alvo usando trigonometria.
   *
   * @param limelightMountAngleDegrees Ângulo de montagem da câmera (graus)
   * @param limelightLensHeightInches Altura da câmera (polegadas)
   * @param goalHeightInches Altura do alvo (polegadas)
   * @return Distância em polegadas
   */
  public double getDistanceToTarget(double limelightMountAngleDegrees,
                                    double limelightLensHeightInches,
                                    double goalHeightInches) {
    double ty = getLimelightTY();
    double angleToGoalDegrees = limelightMountAngleDegrees + ty;
    double angleToGoalRadians = angleToGoalDegrees * (Math.PI / 180.0);

    return (goalHeightInches - limelightLensHeightInches) / Math.tan(angleToGoalRadians);
  }

}
