package frc.robot.limeligtht;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LimelightPoseEstimator {

    private static Pose2d poseMegaTag2 = new Pose2d(new Translation2d(0, 0), new Rotation2d(0));
    private static double timestampSeconds = 0.0;
    private static boolean doRejectUpdate = false;

    private static Field2d field2d = new Field2d();

    public static void updateEstimatePose(double yawDegrees, double yawRate) {
        try {
            LimelightHelpers.SetRobotOrientation("", yawDegrees, 0, 0, 0, 0, 0);
            LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("");

            doRejectUpdate = false;

            if ((Math.abs(yawRate) > 360) || mt2.tagCount == 0) {
                doRejectUpdate = true;
            }
            if (!doRejectUpdate) {
                poseMegaTag2 = mt2.pose;
                timestampSeconds = mt2.timestampSeconds;

                field2d.setRobotPose(mt2.pose);

                SmartDashboard.putData("Limelight pose", field2d);
            }
        } catch (NullPointerException nullPointerException) {
            doRejectUpdate = true;
        }
    }

    public static Pose2d getEstimatedPose() {
        return poseMegaTag2;
    }

    public static double getTimestampSecondsEstimatedPose() {
        return timestampSeconds;
    }

    public static boolean isTheLastEstimatedPoseValid() {
        return !doRejectUpdate;
    }
}

