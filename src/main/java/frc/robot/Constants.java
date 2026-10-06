package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import swervelib.math.Matter;
import swervelib.math.SwerveMath;

/**
 * Classe de constantes
 */
public final class Constants {

  // Aqui temos várias constantes referentes as demais áreas do robô
  public static final class Dimensoes {
    public static final Pose2d redHub = new Pose2d(new Translation2d(12.0, 4.04), new Rotation2d());
    public static final Pose2d blueHub = new Pose2d(new Translation2d(4.62, 4.04), new Rotation2d());
    // Tempo de loop (sparkMax + normal = 130ms)
    public static final double LOOP_TIME = 0.13;
    // Massa do robô em libras *DEVE SER CONFIGURADO PARA O SEU ROBÔ*
    public static final double ROBOT_MASS = 38;
    // Velocidade máxima *DEVE SER CONFIGURADO PARA O SEU ROBÔ*
    public static final double MAX_SPEED = 2.0; //máximo é 4
    // Posição do módulo mais longe *COLOQUE S MESMOS VALORES DO JSON*
    private static final Translation2d FURTHEST_MODULE_POSE = new Translation2d(MAX_SPEED, LOOP_TIME);
    public static final double MAX_ANGULAR_SPEED = SwerveMath.calculateMaxAngularVelocity(MAX_SPEED,
        FURTHEST_MODULE_POSE.getX(), FURTHEST_MODULE_POSE.getY());

    // Posições do centro de massa *DEVE SER CONFIGURADO PARA SEU ROBÔ*
    private static final double xMass = 0;
    private static final double yMass = 0;
    private static final double zMass = 0.08;

    // Centro de massa do chassi
    public static final Matter CHASSIS = new Matter(new Translation3d(xMass, yMass, (zMass)), ROBOT_MASS);
  }

  // Contem a porta em que o controle está
  public static final class Controle {

    // Porta do controle
    public static final int xboxControle0 = 0;
    public static final int xboxControle1 = 1;
    
    // Deadband do controle
    public static final double DEADBAND = 0.2; // 0.2
  }

  // Constantes do Robô
  public static final class SwerveConfigs {
    // variável que ativa o PID para controlar a orientação do robô (PID tunado no
    // json)
    public static final boolean headingCorrection = false;
    // true para correção de aceleração
    public static final boolean accelCorrection = false;
    // constante para diminuir o input do joystick (0 < multiplicadorRotacional <=
    // 1)
    public static final double multiplicadorRotacional = 0.8; //0.8
    // constante para diminuir o input do joystick (y)
    public static final double multiplicadorTranslacionalY = 0.7; //0.7
    // constante para diminuir o input do joystick (x)
    public static final double multiplicadorTranslacionalX = 0.7; //0.7
    public static final double TURN_CONSTANT = 0.75; //0.75
    // variável que ativa a coração para resolver problema de "skew"
    public static final boolean usarCorrecaoDesvioVelocidadeAngular = false;
    // constante que corrije o skew (de -0.15 - 0.15) ESSA VARIÁVEL DEVE SER TUNADA
    // PARA SEU ROBÔ
    public static final double coeficienteCorecaoAngVel = 4;
  }

  public class GenericConstants {
    public static boolean disableHAL = false;

    public static void disableHAL() {
      disableHAL = true;
    }
  }
}
