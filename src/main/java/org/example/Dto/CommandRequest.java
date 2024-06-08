package org.example.Dto;

import lombok.Getter;
import org.example.util.PersonData;

import java.io.Serializable;

@Getter
public class CommandRequest implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    private final String commandName;
    private final String[] commandStringArgument;
    private final Serializable commandObjectArgument;
    private final String userName;
    private String userPass;

    public CommandRequest(String commandName, String[] commandStringArgument, Serializable commandObjectArgument) {
        this.commandName = commandName;
        this.commandStringArgument = commandStringArgument;
        this.commandObjectArgument = commandObjectArgument;
        userName = PersonData.getInstance().getUserName();
        userPass = PersonData.getInstance().getUserPass();
    }

    public CommandRequest(String commandName, String[] commandStringArgument) {
        this.commandName = commandName;
        this.commandStringArgument = commandStringArgument;
        this.commandObjectArgument = null;
        userName = PersonData.getInstance().getUserName();
        userPass = PersonData.getInstance().getUserPass();
    }

    public CommandRequest(String commandName, String userName, String userPass) {
        this.commandName = commandName;
        this.commandStringArgument = null;
        this.commandObjectArgument = null;
        this.userName = userName;
        this.userPass = userPass;
    }
}
