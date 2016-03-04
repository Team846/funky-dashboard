package com.lynbrookrobotics.funkydashboard.blackbox;

import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.FI;
import akka.japi.pf.ReceiveBuilder;

public class BlackboxReceiver extends AbstractActor {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create(
                "blackbox",
                ConfigFactory.parseFile(new File("blackbox/application.conf"))
        );

        ActorRef receiver = system.actorOf(Props.create(BlackboxReceiver.class), "receiver");
        System.out.println(receiver);
        System.out.println(system.provider().getDefaultAddress());
    }

    String usbDrive = "/Users/shadaj/clubs/frc/blackbox-drive"/*"/dev/sdb1"*/;

    public BlackboxReceiver() {
        receive(ReceiveBuilder.matchAny(o -> {}).build());

        boolean connected = false;
        while (!connected) {
            File usb = new File(usbDrive);
            if (usb.exists()) {
                try {
                    String location =  usbDrive + "/" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".blackbox";
                    System.out.println(location);
                    File out = new File(location);
                    PrintWriter writer = new PrintWriter(out);
                    System.out.println("Created new blackbox file on drive");

                    connected = true;

                    FI.UnitApply<String> receiver = new FI.UnitApply<String>() {
                        int tick = 0;

                        @Override
                        public void apply(String s) {
                            writer.println(s);
                            if (tick++ % 200 == 0) {
                                writer.flush();
                            }
                        }
                    };

                    context().become(
                            ReceiveBuilder.match(String.class, receiver).build()
                    );
                } catch (Exception e) {
                    context().become(ReceiveBuilder.matchAny(o -> {}).build());
                }
            } else {
                context().become(ReceiveBuilder.matchAny(o -> {}).build());
            }
        }
    }
}
