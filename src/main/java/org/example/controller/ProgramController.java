package org.example.controller;

import org.example.Dto.CommandRequest;
import org.example.Dto.Response;
import org.example.Dto.StatusCode;
import org.example.connection.ResponseHandler;
import org.example.connection.Sender;
import org.example.connection.TCPClient;
import org.example.exception.ClosureFailedException;
import org.example.exception.NonWorkingServerException;
import org.example.util.PersonData;

import java.io.IOException;
import java.net.SocketException;

/**
 *
 * Класс отвечающий за жизненный цикл программы
 *
 */

public class ProgramController {
    private final CommandController commandController;
    private final ConsoleController consoleController;
    private final TCPClient tcpClient;
    private final Sender sender;
    private final ResponseHandler responseHandler;
    private final PersonData personData;

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 2000;

    public ProgramController() {
        commandController = new CommandController();
        consoleController = ConsoleController.getInstance();
        personData = PersonData.getInstance();

        tcpClient = new TCPClient(SERVER_ADDRESS,SERVER_PORT);
        sender = new Sender(tcpClient);
        responseHandler = new ResponseHandler();
    }

    public void run() throws NonWorkingServerException{

        try {
            tcpClient.run();
        } catch (IOException e) {
//            consoleController.println("Сервер не запущен");
//            System.exit(1);
            throw new NonWorkingServerException();
        }


        Thread thread = new Thread(() -> {

            consoleController.println("\nЗавершение работы по другой причине");
            try {

                // тут закрывается соединение с сервером

                Thread.currentThread().interrupt();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        thread.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(thread);

        try {

            System.out.println("Для подолжения работы вам нужно авторизоваться");

            while (personData.getUserName() == null || personData.getUserPass() == null){

                System.out.print("Вы хотите зарегистрироваться?\nвведите y - если да, n - если нет\n> ");

                String answer = consoleController.readNextLine().trim().toLowerCase();
                String action;

                if (answer.equals("y")){
                    action = "registration";
                } else if (answer.equals("n")){
                    action = "log";
                } else {
                    continue;
                }

                System.out.print("Введите имя:\n> ");
                String userName = consoleController.readNextLine().trim();

                System.out.print("Введите пароль:\n> ");
                String userPass = consoleController.readNextLine().trim();

                Response response = sender.sendRequest(new CommandRequest(action, userName, userPass));

                if (response.getStatusCode().equals(StatusCode._200_SUCCESS_)){
                    personData.setUserName(userName);
                    personData.setUserPass(userPass);
                } else {
                    consoleController.println(response.getMessage());
                }


            }
        } catch (Exception e) {
            consoleController.println("Произошла ошибка сервера: " + e.getMessage());
            throw new NonWorkingServerException();

        }


        consoleController.println("Вы успешно авторизовались\nДля получения списка команд напишите: help");
        System.out.print("> ");

        try {
            while (consoleController.hasNext()) {
                String line = consoleController.readNextLine();
                if (line == null) { // Check for EOF (Ctrl+D)

                    try {
                        tcpClient.close();
                    } catch (ClosureFailedException exception){
                        consoleController.print("При завершении соединения произошла ошибка");
                    }

                    System.exit(1);
                }

                if (line.isEmpty()) {
                    continue;
                }

                String[] str = line.trim().split("\\s+");
                if (str.length == 0) {
                    continue;
                }

                if (!CommandController.isValidCommand(str[0])) {
                    consoleController.println(str[0] + ": Имя " + str[0] +
                            " не распознано как имя командлета, функции, файла сценария или выполняемой программы\n" +
                            "Проверьте правильность написания имени, после чего повторите попытку.");
                    System.out.print("> ");
                    continue;
                }

                String[] args = new String[str.length - 1];
                System.arraycopy(str, 1, args, 0, str.length - 1);

                CommandRequest commandRequest = commandController.buildCommand(str[0], args);

                if (commandRequest != null){

                    Response response;

                    try{
                        response = sender.sendRequest(commandRequest);
                    } catch (SocketException e){
//                        consoleController.println("Сервер не запущен");
                        throw new NonWorkingServerException();
                    }

                    consoleController.println(responseHandler.handleResponse(response));


                } else {
                    consoleController.println("Неверное количество аргументов или используемые файлы недоступны");
                }

                consoleController.print("> ");
            }
        } catch (Exception e) {
            consoleController.println("Произошла ошибка сервера: " + e.getMessage());
            throw new NonWorkingServerException();
//            try {
//                tcpClient.close();
//            } catch (ClosureFailedException exception){
//                consoleController.print("При завершении соединения произошла ошибка");
//            }
//            System.exit(1);
        }
    }
}