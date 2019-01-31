/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.autonomous;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.PathfinderFRC;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;

/**
 * Used as a container for all the paths created in pathweaver and managing the
 * running of these paths
 */
public class Pathweaver {

    private AHRS gyro;

    private Encoder leftEncoder;
    private Encoder rightEncoder;

    private EncoderFollower leftEncoderFollower = new EncoderFollower();
    private EncoderFollower rightEncoderFollower = new EncoderFollower();

    private Trajectory leftTrajectory;
    private Trajectory rightTrajectory;

    private Notifier followerNotifier = new Notifier(this::followPath);

    private SpeedControllerGroup leftMotors;
    private SpeedControllerGroup rightMotors;

    private final double kWheelDiameter = 0.5;
    private final int kLeftTicksPerRev = 257; //264, 251, 257
    private final int kRightTicksPerRev = 171; //170, 172, 171
    private final double kMaxLeftVelocity = 8f;
    private final double kMaxRightVelocity = 9f;
    private final double kMaxDesiredVelocity = 5f;

    //Speedcontroller group
    public Pathweaver(Encoder leftEncoder, Encoder rightEncoder, SpeedControllerGroup leftMotors, SpeedControllerGroup rightMotors, AHRS gyro) {
        this.leftMotors = leftMotors;
        this.rightMotors = rightMotors;

        init(leftEncoder, rightEncoder, gyro);
    }

    //Individual Speed Controller
    public Pathweaver(Encoder leftEncoder, Encoder rightEncoder, SpeedController leftMotor, SpeedController rightMotor, AHRS Gyro) {
        leftMotors = new SpeedControllerGroup(leftMotor, leftMotor);
        rightMotors = new SpeedControllerGroup(rightMotor, rightMotor);

        init(leftEncoder, rightEncoder, gyro);
    }

    private void init(Encoder leftEncoder, Encoder rightEncoder, AHRS gyro) {
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
        this.gyro = gyro;

        //These should technically be done before being passed in to this class
        rightMotors.setInverted(true);
        rightEncoder.setReverseDirection(true);

        //These need tuned
        leftEncoderFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / kMaxLeftVelocity, 0);
        rightEncoderFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / kMaxRightVelocity, 0);

        SmartDashboard.putNumber("leftP", 1);
        SmartDashboard.putNumber("rightP", 1);

        SmartDashboard.putNumber("leftD", 0);
        SmartDashboard.putNumber("rightD", 0);
    }

    public void setPath(String pathName) {
        //Once v2019.3.1 comes out swap the left and right suffixes to be the same
        leftTrajectory = PathfinderFRC.getTrajectory(pathName + ".right");
        rightTrajectory = PathfinderFRC.getTrajectory(pathName + ".left");

        leftEncoderFollower.configurePIDVA(SmartDashboard.getNumber("leftP", 1), 0.0, SmartDashboard.getNumber("leftD", 0), 1 / kMaxLeftVelocity, 0);
        rightEncoderFollower.configurePIDVA(SmartDashboard.getNumber("rightP", 1), 0.0, SmartDashboard.getNumber("rightD", 0), 1 / kMaxRightVelocity, 0);
        
        leftEncoderFollower.setTrajectory(leftTrajectory);
        rightEncoderFollower.setTrajectory(rightTrajectory);
    }

    public void start() {
        leftEncoderFollower.configureEncoder(leftEncoder.get(), kLeftTicksPerRev, kWheelDiameter);
        rightEncoderFollower.configureEncoder(rightEncoder.get(), kRightTicksPerRev, kWheelDiameter);
        gyro.reset();

        followerNotifier.startPeriodic(leftTrajectory.get(0).dt);
    }

    public void stop() {
        followerNotifier.stop();
    }

    private void followPath() {
        if (leftEncoderFollower.isFinished() || rightEncoderFollower.isFinished()) {
            followerNotifier.stop();
            System.out.println("Left: " + leftEncoderFollower.isFinished() + " Right: " + rightEncoderFollower.isFinished());
        } else {
            double left_speed = leftEncoderFollower.calculate(leftEncoder.get());
            double right_speed = rightEncoderFollower.calculate(rightEncoder.get());
            left_speed *= (kMaxDesiredVelocity / kMaxLeftVelocity);
            right_speed *= (kMaxDesiredVelocity / kMaxRightVelocity);
            double heading = gyro.getAngle();
            SmartDashboard.putNumber("Gyro", heading);
            double desired_heading = Pathfinder.r2d(leftEncoderFollower.getHeading());
            SmartDashboard.putNumber("DesiredHeading", desired_heading);
            double heading_difference = Pathfinder.boundHalfDegrees(desired_heading - heading);
            SmartDashboard.putNumber("HeadingDifference", heading_difference);
            double turn = 2 * (-1.0 / 80.0) * heading_difference;
            leftMotors.set(left_speed - turn); //+turn
            rightMotors.set(right_speed + turn);//-turn
        }
    }

}
