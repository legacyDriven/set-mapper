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
      Set<Employee> result;
        Set<EmployeeDTO> toMap = new HashSet<>();
        try {
            while (resultSet.next()) {
                BigInteger id = BigInteger.valueOf(resultSet.getInt(1));
                System.out.println(id);
                FullName fullName =
                        new FullName(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
                Position position = Position.valueOf(resultSet.getString(5));
                LocalDate hired = LocalDate.parse(resultSet.getString(7));
                BigDecimal salary = new BigDecimal(resultSet.getString(8));
                salary = salary.setScale(5, RoundingMode.HALF_UP);
                BigInteger managerId = BigInteger.valueOf(resultSet.getInt(9));
                System.out.println(managerId);
                toMap.add(new EmployeeDTO(id, fullName, position, hired, salary, managerId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println(toMap);
        result = hierarchyMapper(toMap);
        return (T) result;
    }

    Set<Employee> hierarchyMapper(Set<EmployeeDTO> entries){
        Set<Employee> result = new HashSet<>();
        List<EmployeeDTO> toMap = new ArrayList<>(entries);
        EmployeeDTO president = entries.stream().filter(n->n.managerId==null).findFirst().orElseThrow();
        Employee head = mapDtoToEmployee(president, null);
        result.add(head);
        toMap.remove(president);
        result.addAll(mapToDirectSuperiors(result, toMap));
        return result;
    }

    private Collection<? extends Employee> mapToDirectSuperiors(Set<Employee> result, List<EmployeeDTO> toMap) {
        while(!toMap.isEmpty()){
            for(int i = 0; i<toMap.size(); i++){
                if(i==toMap.size()-1) i=0;
                if(result.contains(findEmpById(result, toMap.get(i).managerId))){
                    result.add(mapDtoToEmployee(toMap.get(i), findEmpById(result, toMap.get(i).id)));
                    toMap.remove(i);
                }
            }
        }
        return result;
    }

    Employee findEmpById(Set<Employee> managers, BigInteger id){
        return managers.stream().filter(n->n.getId().equals(id)).findFirst().orElseThrow();
    }

    private Employee mapDtoToEmployee(EmployeeDTO dto, Employee manager){
        return new Employee(dto.id, dto.fullName, dto.position, dto.hired, dto.salary, manager);
    }

//    Set<Employee> hierarchyMapper(Set<EmployeeDTO> entries){
//        Set<Employee> result = new HashSet<>();
//        EmployeeDTO head = entries.stream().filter(e->e.managerId==null).findFirst().orElseThrow();
//        Employee currentManager = new Employee(head.id, head.fullName, head.position, head.hired, head.salary, null);
//        entries.remove(head); result.add(currentManager);
//        mapDirectSubordinates(entries, currentManager);
//
//        return result;
//    }

    private static Set<Employee> mapDirectSubordinates(Set<EmployeeDTO> entries, Employee manager){
        Set<Employee> result = new HashSet<>();
        List<EmployeeDTO> subordinates = entries.stream()
                .filter(n-> n.managerId.equals(manager.getId()))
                .collect(Collectors.toList());
        for (EmployeeDTO e : entries){
            result.add(new Employee(e.id, e.fullName, e.position, e.hired, e.salary, manager));
        }
        return result;
    }


    private static class EmployeeDTO {
        private final BigInteger id;
        private final FullName fullName;
        private final Position position;
        private final LocalDate hired;
        private final BigDecimal salary;
        private final BigInteger managerId;

        public EmployeeDTO(BigInteger id, FullName fullName, Position position, LocalDate hired, BigDecimal salary, BigInteger managerId) {
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
            EmployeeDTO that = (EmployeeDTO) o;
            return Objects.equals(id, that.id) && Objects.equals(fullName, that.fullName)
                    && position == that.position && Objects.equals(hired, that.hired) && Objects.equals(salary, that.salary)
                    && Objects.equals(managerId, that.managerId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, fullName, position, hired, salary, managerId);
        }
    }
}
