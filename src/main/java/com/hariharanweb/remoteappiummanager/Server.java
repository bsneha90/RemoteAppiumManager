package com.hariharanweb.remoteappiummanager;

import com.hariharanweb.remoteappiummanager.controller.AppiumController;
import com.hariharanweb.remoteappiummanager.controller.DeviceController;
import com.hariharanweb.remoteappiummanager.controller.MachineController;
import com.hariharanweb.remoteappiummanager.transformers.JsonTransformer;

import java.io.IOException;
import java.util.logging.Logger;

import static com.hariharanweb.helpers.Helpers.isPortAvailable;
import static spark.Spark.*;

public class Server {

    public static void main(String[] args) throws IOException {

        final Logger LOGGER =
                Logger.getLogger(Server.class.getName());
        if (System.getProperty("port") != null) {
            int port = Integer.parseInt(System.getProperty("port"));
            if (isPortAvailable(port)) {
                port(port);
            } else {
                throw new RuntimeException("Port" + port + " in use");
            }
            LOGGER.info("Started Server on port" + System.getProperty("port"));
        }
        DeviceController deviceController = new DeviceController();
        AppiumController appiumController = new AppiumController();
        MachineController machineController = new MachineController();
        get("/", (req, res) -> "Server is Running!!!");
        get("/devices", deviceController.getDevices,new JsonTransformer());

        path("/device/:udid", () -> {
            get("", deviceController.getDevice, new JsonTransformer());
        });

        path("/appium", () -> {
            get("/start", appiumController.startAppium, new JsonTransformer());
            get("/stop", appiumController.stopAppium, new JsonTransformer());
            get("/isRunning", appiumController.isAppiumServerRunning, new JsonTransformer());
            get("/logs",appiumController.getAppiumLogs);
        });

        path("/machine",()->{
            get("/xcodeVersion", machineController.getXCodeVersion);
        });

        after((request, response) -> {
            response.header("Content-Type", "application/json");
        });
    }
}
