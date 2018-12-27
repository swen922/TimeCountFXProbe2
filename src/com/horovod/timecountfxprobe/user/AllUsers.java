package com.horovod.timecountfxprobe.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AllUsers {

    private static volatile AtomicInteger IDCounterAllUsers = new AtomicInteger(0);

    private static Map<Integer, User> users = new ConcurrentHashMap<>();

    // Заводим отдельный список для удаленных пользователей,
    // чтобы иметь возможность использовать их в подборках статистики
    private static Map<Integer, User> deletedUsers = new ConcurrentHashMap<>();

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

    public static Map<Integer, User> getDeletedUsers() {
        return deletedUsers;
    }

    public static synchronized void setDeletedUsers(Map<Integer, User> deletedUsers) {
        AllUsers.deletedUsers = deletedUsers;
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

    public static synchronized void setUsersLogged(ObservableList<String> newUsersLogged) {
        AllUsers.usersLogged = newUsersLogged;
    }


    public static synchronized void addLoggedUserByIDnumber(int newLoggedUser) {
        if (!usersLogged.contains(AllUsers.getOneUser(newLoggedUser).getFullName())) {
            AllUsers.usersLogged.add(AllUsers.getOneUser(newLoggedUser).getFullName());
        }
    }

    public static synchronized void addLoggedUser(String newLoggedUser) {
        if (!usersLogged.contains(newLoggedUser)) {
            AllUsers.usersLogged.add(newLoggedUser);
        }
    }

    public static synchronized void deleteLoggedUserbyIDnumber(int deletedUser) {
        String oldFullName = AllUsers.getOneUser(deletedUser).getFullName();
        if (usersLogged.contains(oldFullName)) {
            AllUsers.usersLogged.remove(oldFullName);
        }
    }

    public static synchronized void deleteLoggedUser(String deletedUser) {
        if (usersLogged.contains(deletedUser)) {
            AllUsers.usersLogged.remove(deletedUser);
        }
    }


    public static boolean isUsersLoggedContainsUser(int loggedUser) {
        return AllUsers.usersLogged.contains(AllUsers.getOneUser(loggedUser).getFullName());
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

    public static User getOneDeletedUser(int deletedUser) {
        if (deletedUsers.containsKey(deletedUser)) {
            return deletedUsers.get(deletedUser);
        }
        return null;
    }


    public static Map<Integer, User> getDesigners() {
        Map<Integer, User> result = new HashMap<>();
        for (User u : users.values()) {
            if (u.getRole().equals(Role.DESIGNER)) {
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
        for (User us : deletedUsers.values()) {
            if (us.getRole().equals(Role.DESIGNER)) {
                result.put(us.getIDNumber(), us);
            }
        }
        return result;
    }



    /** Добавление и удаление пользователя */

    /** @return null !!!
     * */
    public static synchronized User createUser(String login, String password, Role role) {
        User result = null;
        if (!isNameLoginExist(login)) {
            if (role.equals(Role.DESIGNER)) {
                result = new Designer(login, password);
            }
            else if (role.equals(Role.MANAGER)) {
                result = new Manager(login, password);

            }

            if (result == null) {
                return null;
            }

            users.put(result.getIDNumber(), result);

            SecurePassword sp = SecurePassword.getInstance(password);
            if (sp == null) {
                return null;
            }
            usersPass.put(result.getIDNumber(), sp);
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
            deletedUsers.put(idUser, users.get(idUser));
            users.remove(idUser);
            return true;
        }
        return false;
    }

    public static synchronized boolean resurrectUser(int idUser) {
        if (isUserDeleted(idUser)) {
            users.put(idUser, deletedUsers.get(idUser));
            deletedUsers.remove(idUser);
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
        if (nameLog == null || nameLog.isEmpty()) {
            return true;
        }

        Collection<User> tmpUsers = users.values();

        for (User u : tmpUsers) {
            if (nameLog.equals(u.getNameLogin())) {
                return true;
            }
        }

        return false;
    }

    public static boolean isUserDeleted(int idNumber) {
        return deletedUsers.containsKey(idNumber);
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
