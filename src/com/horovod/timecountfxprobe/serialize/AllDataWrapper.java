package com.horovod.timecountfxprobe.serialize;


import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.Designer;
import com.horovod.timecountfxprobe.user.Manager;
import com.horovod.timecountfxprobe.user.SecurePassword;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/** Специальный класс-обертка для всех данных,
 * чтобы сохранить их в XML-файл.
 * Используется классом Loader для сохранения и чтения сохраненных данных
 *
 * Поскольку JAXB не поддерживает сохранение интерфейсов,
 * сохраняем юзеров в индивидуальные списки согласно классам-имплементаторам */

@XmlRootElement(name = "alldatawrapper")
public class AllDataWrapper {

    //поля класса AllData
    private int allProjectsIdNumber;
    private Map<Integer, Project> allProjects = new HashMap<>();
    private int workSumProjects;

    //поля класса AllUsers
    private int IDCounterAllUsers;
    private Map<Integer, Designer> saveDesigners = new HashMap<>();
    private Map<Integer, Manager> saveManagers = new HashMap<>();
    private Map<Integer, SecurePassword> saveUsersPass = new HashMap<>();



}
