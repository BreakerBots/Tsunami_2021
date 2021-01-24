package frc.team5104;

import frc.team5104.auto.paths.CoordinateExample;
import frc.team5104.util.Plotter;
import frc.team5104.util.Webapp;

public class Simulation {
    public Simulation() {
        Webapp.run();
        Plotter.reset();

        (new CoordinateExample()).plot();

//        while (true) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("test");
//        }
    }
}
