package com.horovod.timecountfxprobe.user;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import com.horovod.timecountfxprobe.serialize.Updater;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AllUsers {






    //private static volatile AtomicInteger IDCounterAllUsers = new AtomicInteger(0);
    public static volatile IntegerProperty createUserID = new SimpleIntegerProperty(0);


    private static Map<Integer, User> users = new ConcurrentHashMap<>();

    // TODO убрать список паролей – лишние, все теперь прямо в юзерах
    /*private static Map<Integer, SecurePassword> usersPass = new ConcurrentHashMap<>();*/

    /** Это поле надо сохранять в XML, чтобы при загрузке утром сразу грузился нужный пользователь */
    private static int currentUser = 0;

    /** Это поле надо сохранять в XML, чтобы при загрузке утром сразу грузился нужный пользователь */
    private static ObservableList<String> usersLogged = FXCollections.observableArrayList();



    /** Стандартные геттеры и сеттеры */

    /*public static int getIDCounterAllUsers() {
        return IDCounterAllUsers.get();
    }

    public static synchronized void setIDCounterAllUsers(int newIDCounterAllUsers) {
        AllUsers.IDCounterAllUsers.set(newIDCounterAllUsers);
    }

    public static synchronized int incrementIdNumberAndGet() {
        return IDCounterAllUsers.incrementAndGet();
    }*/

    public static Map<Integer, User> getUsers() {
        return users;
    }

    public static synchronized void setUsers(Map<Integer, User> users) {
        AllUsers.users = users;
    }


    /*public static Map<Integer, SecurePassword> getUsersPass() {
        return usersPass;
    }

    public static SecurePassword getSecurePassForUser(int idUser) {
        if (usersPass.containsKey(idUser)) {
            return usersPass.get(idUser);
        }
        return null;
    }

    public static synchronized void addSecurePass(int idUser, SecurePassword securePassword) {
        usersPass.put(idUser, securePassword);
    }

    public static synchronized void setUsersPass(Map<Integer, SecurePassword> newUsersPass) {
        AllUsers.usersPass = newUsersPass;
    }*/

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
    public static User createUser(int userID, String login, String password, Role role) {

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
        SecurePassword sp = SecurePassword.getInstance(password);


        User result = null;
        if (!isNameLoginExist(login)) {
            if (role.equals(Role.DESIGNER)) {
                result = new Designer(userID, login, sp);
            }
            else if (role.equals(Role.MANAGER)) {
                result = new Manager(userID, login, sp);
            }
            else if (role.equals(Role.ADMIN)) {
                result = new Admin(userID, login, sp);
            }
            else if (role.equals(Role.SURVEYOR)) {
                result = new Surveyor(userID, login, sp);
            }

            if (result == null) {
                return null;
            }

            users.put(result.getIDNumber(), result);
            //usersPass.put(result.getIDNumber(), sp);

            // Статус и Логирование
            AllData.updateAllStatus("AllUsers.createUser - Локально создан новый работник id-" + result.getIDNumber() + " = " + AllUsers.getOneUser(result.getIDNumber()).getFullName());
            AllData.logger.info(AllData.status);

            if (role.equals(Role.DESIGNER)) {
                Updater.update(UpdateType.CREATE_DESIGNER, result);
            }
            else if (role.equals(Role.MANAGER)) {
                Updater.update(UpdateType.CREATE_MANAGER, result);
            }
            else if (role.equals(Role.ADMIN)) {
                Updater.update(UpdateType.CREATE_ADMIN, result);
            }
            else if (role.equals(Role.SURVEYOR)) {
                Updater.update(UpdateType.CREATE_SURVEYOR, result);
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
            User user = users.get(idUser);
            user.setRetired(true);

            // Статус и Логирование
            AllData.updateAllStatus("AllUsers.deleteUser - Работник id-" + idUser + " " + AllUsers.getOneUser(idUser).getFullName() + " уволен.");
            AllData.logger.info(AllData.status);

            if (user.getRole().equals(Role.DESIGNER)) {
                Updater.update(UpdateType.UPDATE_DESIGNER, user);
            }
            else if (user.getRole().equals(Role.MANAGER)) {
                Updater.update(UpdateType.UPDATE_MANAGER, user);
            }
            else if (user.getRole().equals(Role.ADMIN)) {
                Updater.update(UpdateType.UPDATE_ADMIN, user);
            }
            else if (user.getRole().equals(Role.SURVEYOR)) {
                Updater.update(UpdateType.UPDATE_SURVEYOR, user);
            }

            return true;
        }
        return false;
    }

    public static synchronized boolean resurrectUser(int idUser) {
        if (isUserDeleted(idUser)) {
            User user = users.get(idUser);
            user.setRetired(false);

            // Статус и Логирование
            AllData.updateAllStatus("AllUsers.resurrectUser - Работник id-" + idUser + " " + AllUsers.getOneUser(idUser).getFullName() + " снова принят на работу.");
            AllData.logger.info(AllData.status);

            if (user.getRole().equals(Role.DESIGNER)) {
                Updater.update(UpdateType.UPDATE_DESIGNER, user);
            }
            else if (user.getRole().equals(Role.MANAGER)) {
                Updater.update(UpdateType.UPDATE_MANAGER, user);
            }
            else if (user.getRole().equals(Role.ADMIN)) {
                Updater.update(UpdateType.UPDATE_ADMIN, user);
            }
            else if (user.getRole().equals(Role.SURVEYOR)) {
                Updater.update(UpdateType.UPDATE_SURVEYOR, user);
            }

            return true;
        }
        return false;
    }



    /** Методы проверки существования юзера
     * * с данным ID и nameLogin
     */
    public static boolean isUserExist(int idNumber) {
        if (idNumber <= 0) {
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

    /*public static boolean isPassExistForUser(int IDuser) {
        return usersPass.containsKey(IDuser);
    }*/

    /*public static boolean deletePassForUser(int IDuser) {
        if (isPassExistForUser(IDuser)) {
            usersPass.remove(IDuser);
            return true;
        }
        return false;
    }*/

    public static boolean isPassCorrectForUser(int IDuser, String password) {
        if (!users.containsKey(IDuser)) {
            return false;
        }

        System.out.println("AllUsers.getOneUser(IDuser) = " + AllUsers.getOneUser(IDuser));


        String securePass = AllUsers.getOneUser(IDuser).getSecurePassword().getSecurePass();
        String salt = AllUsers.getOneUser(IDuser).getSecurePassword().getSalt();
        boolean result;
        try {
            result = PasswordUtil.verifyUserPassword(password, securePass, salt);
        } catch (Error error) {
            return false;
        }
        return result;
    }

}
