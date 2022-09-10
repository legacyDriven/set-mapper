package com.epam.rd.autocode;

import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class EmployeeSetMapper<T> implements SetMapper<T> {
    @Override
    public T mapSet(ResultSet resultSet) {
        Set<EmployeeDAO> toMap = new HashSet<>();
        Set<Employee> result = new HashSet<>();
        try {
            while (resultSet.next()) {
                BigInteger id = BigInteger.valueOf(resultSet.getInt(1));
                FullName fullName =
                        new FullName(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
                Position position = Position.valueOf(resultSet.getString(5));
                LocalDate hired = LocalDate.parse(resultSet.getString(7));
                BigDecimal salary = new BigDecimal(resultSet.getString(8));
                salary = salary.setScale(5, RoundingMode.HALF_UP);
                Integer managerId = resultSet.getInt(9);
                toMap.add(new EmployeeDAO(id, fullName, position, hired, salary, managerId));
                result = mapHierarchy(toMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return (T) result;
    }

    private Set<Employee> mapHierarchy(Set<EmployeeDAO> toMap) {
        Set<Employee> result = new HashSet<>();
        for(EmployeeDAO e:toMap){
            result.add(mapEmployee(e,toMap));
        }
        return result;
    }

    private Employee mapEmployee(EmployeeDAO worker, Set<EmployeeDAO> toMap) {
        return new Employee(worker.id, worker.fullName,findManager(worker.managerId, toMap), )
    }

    private Employee findManager(Integer managerId, Set<EmployeeDAO> toMap){

    }

    private class EmployeeDAO {
        private final BigInteger id;
        private final FullName fullName;
        private final Position position;
        private final LocalDate hired;
        private final BigDecimal salary;
        private Integer managerId;

        public EmployeeDAO(BigInteger id, FullName fullName, Position position, LocalDate hired, BigDecimal salary, Integer managerId) {
            this.id = id;
            this.fullName = fullName;
            this.position = position;
            this.hired = hired;
            this.salary = salary;
            this.managerId = managerId;
        }
    }
}
/*

    private Set<Employee> mapManagerReferences(Map<Employee, Integer> notReferenced) {
        Set<Employee> result = new HashSet<>();
        return null;
    }

    public T mapRow(ResultSet resultSet) {
        try {
            BigInteger id = BigInteger.valueOf(resultSet.getInt(1));
            FullName fullName =
                    new FullName(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
            Position position = Position.valueOf(resultSet.getString(5));
            LocalDate hired = LocalDate.parse(resultSet.getString(7));
            BigDecimal salary = new BigDecimal(resultSet.getString(8));
            salary = salary.setScale(5, RoundingMode.HALF_UP);
            // Employee employee = new Employee(id, fullName, position, hired, salary);

            return (T) null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


CREATE TABLE DEPARTMENT
(
    ID       INTEGER PRIMARY KEY,
    NAME     VARCHAR(14),
    LOCATION VARCHAR(13)
);

CREATE TABLE EMPLOYEE
(
    ID         INTEGER PRIMARY KEY,
    FIRSTNAME  VARCHAR(10),
    LASTNAME   VARCHAR(10),
    MIDDLENAME VARCHAR(10),
    POSITION   VARCHAR(9),
    MANAGER    INTEGER,
    HIREDATE   DATE,
    SALARY     DOUBLE,
    DEPARTMENT INTEGER,
    CONSTRAINT FK_DEPTNO FOREIGN KEY (DEPARTMENT) REFERENCES DEPARTMENT (ID)
);

COMMIT;
 */