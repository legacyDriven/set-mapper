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
import java.util.*;
import java.util.stream.Collectors;

public class NestedSetMapper<T> implements SetMapper<T>{
    @Override
    public T mapSet(ResultSet resultSet) {
      Set<Employee> result = new HashSet<>();
//      Map<Position, EmployeeDAO> toProcess = new TreeMap<>();
        Set<EmployeeDAO> toMap = new HashSet<>();
        try {
            while (resultSet.next()) {
                BigInteger id = BigInteger.valueOf(resultSet.getInt(1));
                FullName fullName =
                        new FullName(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
                Position position = Position.valueOf(resultSet.getString(5));
                LocalDate hired = LocalDate.parse(resultSet.getString(7));
                BigDecimal salary = new BigDecimal(resultSet.getString(8));
                salary = salary.setScale(5, RoundingMode.HALF_UP);
                BigInteger managerId = BigInteger.valueOf(resultSet.getInt(9));
                toMap.add(new EmployeeDAO(id, fullName, position, hired, salary, managerId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        result = hierarchyMapper(toMap);
        return (T) result;
    }

    Set<Employee> hierarchyMapper(Set<EmployeeDAO> entries){
        Set<Employee> result = new HashSet<>();
        EmployeeDAO head = entries.stream().filter(e->e.managerId==null).findFirst().get();
        Employee currentManager = new Employee(head.id, head.fullName, head.position, head.hired, head.salary, null);
        entries.remove(head); result.add(currentManager);


        return result;
    }

    private static Set<Employee> mapDirectSubordinates(Set<EmployeeDAO> entries, Employee manager){
        Set<Employee> result = new HashSet<>();
        List<EmployeeDAO> subordinates = entries.stream()
                .filter(n-> n.managerId.equals(manager.getId()))
                .collect(Collectors.toList());
        for (EmployeeDAO e : entries){
            result.add(new Employee(e.id, e.fullName, e.position, e.hired, e.salary, manager));
        }
        return result;
    }


    private static class EmployeeDAO {
        private final BigInteger id;
        private final FullName fullName;
        private final Position position;
        private final LocalDate hired;
        private final BigDecimal salary;
        private final BigInteger managerId;

        public EmployeeDAO(BigInteger id, FullName fullName, Position position, LocalDate hired, BigDecimal salary, BigInteger managerId) {
            this.id = id;
            this.fullName = fullName;
            this.position = position;
            this.hired = hired;
            this.salary = salary;
            this.managerId = managerId;
        }

        @Override
        public String toString() {
            return "EmployeeDAO{" +
                    "id=" + id +
                    ", fullName=" + fullName +
                    ", position=" + position +
                    ", hired=" + hired +
                    ", salary=" + salary +
                    ", managerId=" + managerId +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmployeeDAO that = (EmployeeDAO) o;
            return Objects.equals(id, that.id) && Objects.equals(fullName, that.fullName) && position == that.position && Objects.equals(hired, that.hired) && Objects.equals(salary, that.salary) && Objects.equals(managerId, that.managerId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, fullName, position, hired, salary, managerId);
        }
    }
}
