package org.example.command;

import org.example.Dto.CommandRequest;

import static java.lang.System.exit;

public class Logout extends Command{

    public Logout(){
        argSize = 0;
        name = "logout";
    }

    @Override
    public CommandRequest build(String... args) {
        if (!isSizeCorrect(args.length)) {
            return null;
        }



        return new CommandRequest(name, args, null);
    }
}
