package com.thesis.alphidcar.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Users {
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String username;
    private String password;

    public Users(UserBuilder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.middleName = builder.middleName;
        this.lastName = builder.lastName;
        this.username = builder.username;
        this.password = builder.password;
    }


    public static class UserBuilder {

        private int id;
        private String firstName;
        private String middleName;
        private String lastName;
        private String username;
        private String password;

        public UserBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public UserBuilder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder setMiddleName(String middleName) {
            this.middleName = middleName;
            return this;
        }

        public UserBuilder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Users build() {
            return new Users(this);
        }
    }
}
