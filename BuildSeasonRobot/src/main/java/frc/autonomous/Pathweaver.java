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
    private final int kLeftTicksPerRev = 252;
    private final int kRightTicksPerRev = 172;
    private final double kMaxVelocity = 10f;

    //Speedcontroller group
    public Pathweaver(Encoder leftEncoder, Encoder rightEncoder, SpeedControllerGroup leftMotors, SpeedControllerGroup rightMotors, AHRS gyro) {
        init(leftEncoder, rightEncoder, gyro);

        this.leftMotors = leftMotors;
        this.rightMotors = rightMotors;
    }

    //Individual Speed Controller
    public Pathweaver(Encoder leftEncoder, Encoder rightEncoder, SpeedController leftMotor, SpeedController rightMotor, AHRS Gyro) {
        init(leftEncoder, rightEncoder, gyro);

        leftMotors = new SpeedControllerGroup(leftMotor, leftMotor);
        rightMotors = new SpeedControllerGroup(rightMotor, rightMotor);
    }

    private void init(Encoder leftEncoder, Encoder rightEncoder, AHRS gyro) {
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
        this.gyro = gyro;

        //These should technically be done before being passed in to this class
        rightMotors.setInverted(true);
        rightEncoder.setReverseDirection(true);

        //These need tuned
        leftEncoderFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / kMaxVelocity, 0);
        rightEncoderFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / kMaxVelocity, 0);
    }

    public void setPath(String pathName) {
        //Once v2019.3.1 comes out swap the left and right suffixes to be the same
        leftTrajectory = PathfinderFRC.getTrajectory(pathName + ".right");
        rightTrajectory = PathfinderFRC.getTrajectory(pathName + ".left");

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
        } else {
            double left_speed = leftEncoderFollower.calculate(leftEncoder.get());
            double right_speed = rightEncoderFollower.calculate(rightEncoder.get());
            double heading = gyro.getAngle();
            double desired_heading = Pathfinder.r2d(leftEncoderFollower.getHeading());
            double heading_difference = Pathfinder.boundHalfDegrees(desired_heading - heading);
            double turn = 0.8 * (-1.0 / 80.0) * heading_difference;
            leftMotors.set(left_speed + turn);
            rightMotors.set(right_speed - turn);
        }
    }

}
