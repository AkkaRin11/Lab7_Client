package org.example.util;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PersonData {
    private static PersonData instance;

    private String userName = null;
    private String userPass = null;

    public static PersonData getInstance() {
        if (instance == null) {
            instance = new PersonData();
        }

        return instance;
    }

}
