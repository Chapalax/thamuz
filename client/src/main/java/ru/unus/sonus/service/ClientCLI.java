package ru.unus.sonus.service;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Scanner;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientCLI {

    private final FileProcessingService fileProcessingService;
    private static int counter = 0;

    public void start() {
        while (true) {
            System.out.println("This is my first time using gRPC. Don't be too hard on me ^_^");
            System.out.println("List of available commands:\n1) upload;\n2) download.\n");
            System.out.println("Please enter the command: ");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            switch (command) {
                case "upload" -> {
                    System.out.println("Enter the absolute path to the file you want to upload:");
                    String path = scanner.nextLine();
                    try (FileInputStream fis = new FileInputStream(path)) {
                        System.out.println("Enter the absolute path to the file you would like to create:");
                        String newPath = scanner.nextLine();
                        System.out.println(newPath);
                        fileProcessingService.uploadFile(newPath, fis.readAllBytes());
                        counter++;
                    } catch (IOException | StatusRuntimeException e) {
                        log.error("Error while uploading file", e);
                    }
                }
                case "download" -> {
                    System.out.println("Enter the absolute path to the file you want to download:");
                    String path = scanner.nextLine();
                    try {
                        String homePath = System.getProperty("user.home") + File.separator
                                + "file_" + counter + path.substring(path.lastIndexOf('.'));
                        FileOutputStream fos = new FileOutputStream(homePath);
                        fos.write(fileProcessingService.readFile(path));
                        fos.close();
                    } catch (IOException | StatusRuntimeException | StringIndexOutOfBoundsException e) {
                        log.error("Error while downloading file", e);
                    }
                }
            }
        }
    }
}
