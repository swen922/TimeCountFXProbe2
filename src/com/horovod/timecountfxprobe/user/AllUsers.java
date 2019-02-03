package com.horovod.timecountfxprobe.user;

import com.horovod.timecountfxprobe.exceptions.WrongArgumentException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AllUsers {

    private static volatile AtomicInteger IDCounterAllUsers = new AtomicInteger(0);

    private static Map<Integer, User> users = new ConcurrentHashMap<>();

    private static Map<Integer, SecurePassword> usersPass = new ConcurrentHashMap<>();

    /** Это поле надо сохранять в XML, чтобы при загрузке утром сразу грузился нужный пользователь */
    private static int currentUser = 0;

    /** Это поле надо сохранять в XML, чтобы при загрузке утром сразу грузился нужный пользователь */
    private static ObservableList<String> usersLogged = FXCollections.observableArrayList();



    /** Стандартные геттеры и сеттеры */

    public static int getIDCounterAllUsers() {
        return IDCounterAllUsers.get();
    }

    public static synchronized void setIDCounterAllUsers(int newIDCounterAllUsers) {
        AllUsers.IDCounterAllUsers.set(newIDCounterAllUsers);
    }

    public static synchronized int incrementIdNumberAndGet() {
        return IDCounterAllUsers.incrementAndGet();
    }

    public static Map<Integer, User> getUsers() {
        return users;
    }

    public static synchronized void setUsers(Map<Integer, User> users) {
        AllUsers.users = users;
    }


    public static Map<Integer, SecurePassword> getUsersPass() {
        return usersPass;
    }

    public static synchronized void setUsersPass(Map<Integer, SecurePassword> newUsersPass) {
        AllUsers.usersPass = newUsersPass;
    }

    public static int getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(int newCurrentUser) {
        AllUsers.currentUser = newCurrentUser;
    }

    public static ObservableList<String> getUsersLogged() {
        return usersLogged;
    }

    public static synchronized void setUsersLogged(List<String> newUsersLogged) {
        AllUsers.usersLogged.clear();
        AllUsers.usersLogged.addAll(newUsersLogged);
    }


    public static synchronized boolean addLoggedUser(String newLoggedUser) {
        if (!usersLogged.contains(newLoggedUser)) {
            AllUsers.usersLogged.add(newLoggedUser);
            return true;
        }
        return false;
    }


    public static synchronized boolean deleteLoggedUser(String deletedUser) {
        if (usersLogged.contains(deletedUser)) {
            AllUsers.usersLogged.remove(deletedUser);
            return true;
        }
        return false;
    }


    public static boolean isUsersLoggedContainsUser(String fullName) {
        return AllUsers.usersLogged.contains(fullName);
    }


    /** Геттеры отдельных юзеров из мапы
     * @return null
     * */

    public static User getOneUser(int idUser) {
        if (isUserExist(idUser)) {
            return users.get(idUser);
        }
        return null;
    }

    public static User getOneUser(String userNameLogin) {
        if (userNameLogin == null || userNameLogin.isEmpty()) {
            return null;
        }
        if (isNameLoginExist(userNameLogin)) {
            for (User u : users.values()) {
                if (u.getNameLogin().equals(userNameLogin)) {
                    return u;
                }
            }
        }
        return null;
    }

    public static User getOneUserForFullName(String userFullName) {
        for (User u : users.values()) {
            if (u.getFullName().equals(userFullName)) {
                return u;
            }
        }
        return null;
    }

    public static Map<Integer, User> getActiveUsers() {
        Map<Integer, User> result = new HashMap<>();
        for (User u : users.values()) {
            if (!u.isRetired()) {
                result.put(u.getIDNumber(), u);
            }
        }
        return result;
    }


    public static Map<Integer, User> getActiveDesigners() {
        Map<Integer, User> result = new HashMap<>();
        for (User u : users.values()) {
            if (u.getRole().equals(Role.DESIGNER) && !u.isRetired()) {
                result.put(u.getIDNumber(), u);

            }
        }
        return result;
    }


    public static Map<Integer, User> getDesignersPlusDeleted() {
        Map<Integer, User> result = new HashMap<>();
        for (User u : users.values()) {
            if (u.getRole().equals(Role.DESIGNER)) {
                result.put(u.getIDNumber(), u);
            }
        }
        return result;
    }



    /** Добавление и удаление пользователя */

    /** @return null !!!
     * */
    public static User createUser(String login, String password, Role role) {

        if (login == null || login.isEmpty()) {
            return null;
        }

        if (password == null || password.isEmpty()) {
            return null;
        }

        if (role == null || role.equals("")) {
            return null;
        }

        login = login.trim();
        password = password.trim();

        User result = null;
        if (!isNameLoginExist(login)) {
            if (role.equals(Role.DESIGNER)) {
                result = new Designer(login);
            }
            else if (role.equals(Role.MANAGER)) {
                result = new Manager(login);
            }
            else if (role.equals(Role.ADMIN)) {
                result = new Admin(login);
            }
            else if (role.equals(Role.SURVEYOR)) {
                result = new Surveyor(login);
            }

            if (result == null) {
                return null;
            }

            SecurePassword sp = SecurePassword.getInstance(password);
            if (sp == null) {
                return null;
            }

            users.put(result.getIDNumber(), result);
            usersPass.put(result.getIDNumber(), sp);
            if (result.getRole().equals(Role.ADMIN)) {
                System.out.println("created Admin id-" + result.getIDNumber() + " " + result.getNameLogin() + " " + result.getFullName());
            }
        }
        return result;
    }

    /*public static synchronized boolean addUser(User user) {
        if (!users.containsKey(user.getIDNumber()) && !isNameLoginExist(user.getNameLogin())) {
            users.put(user.getIDNumber(), user);
            return true;
        }
        return false;
    }*/

    public static synchronized boolean deleteUser(int idUser) {
        if (isUserExist(idUser)) {
            users.get(idUser).setRetired(true);
            return true;
        }
        return false;
    }

    public static synchronized boolean resurrectUser(int idUser) {
        if (isUserDeleted(idUser)) {
            users.get(idUser).setRetired(false);
            return true;
        }
        return false;
    }



    /** Методы проверки существования юзера
     * * с данным ID и nameLogin
     */
    public static boolean isUserExist(int idNumber) {
        if (idNumber <= 0 || idNumber > getIDCounterAllUsers()) {
            return false;
        }
        return users.containsKey(idNumber);
    }

    public static boolean isNameLoginExist(String nameLog) {

        Collection<User> tmpUsers = users.values();

        for (User u : tmpUsers) {
            if (u.getNameLogin().equalsIgnoreCase(nameLog)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isFullNameExist(String fName) {

        Collection<User> tmpUsers = users.values();

        for (User u : tmpUsers) {
            if (u.getFullName().equalsIgnoreCase(fName)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isUserDeleted(int idNumber) {
        return users.get(idNumber).isRetired();
    }



    /** Методы работы с паролями и хранения паролей */

    public static boolean isPassExistForUser(int IDuser) {
        return usersPass.containsKey(IDuser);
    }

    public static boolean deletePassForUser(int IDuser) {
        if (isPassExistForUser(IDuser)) {
            usersPass.remove(IDuser);
            return true;
        }
        return false;
    }

    public static boolean isPassCorrectForUser(int IDuser, String password) {
        if (!users.containsKey(IDuser) || !usersPass.containsKey(IDuser)) {
            return false;
        }
        String securePass = usersPass.get(IDuser).getSecurePass();
        String salt = usersPass.get(IDuser).getSalt();
        boolean result;
        try {
            result = PasswordUtil.verifyUserPassword(password, securePass, salt);
        } catch (Error error) {
            return false;
        }
        return result;
    }

}
