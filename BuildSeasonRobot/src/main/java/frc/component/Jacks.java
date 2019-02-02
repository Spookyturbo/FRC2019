/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.component;

import frc.util.Component;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
/**
 * Add your docs here.
 */
public class Jacks implements Component{

    WPI_VictorSPX J1 = new WPI_VictorSPX(RobotMap.Motors.FRDrive);
    WPI_VictorSPX J2 = new WPI_VictorSPX(RobotMap.Motors.FRDrive);


    //Store a static instance and create it for the singleton pattern
    private static Jacks instance = new Jacks();

    private Jacks() {


        //Just here to remove the public constructor
    }

    @Override
    public void execute() {


        //Code ran every loop
    }
    public static Jacks getInstance() {



      return instance;
  }




}