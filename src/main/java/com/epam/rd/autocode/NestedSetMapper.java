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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class NestedSetMapper<T> implements SetMapper<T>{
    @Override
    public T mapSet(ResultSet resultSet) {
      Set<Employee> result = new HashSet<>();
      Map<Position, EmployeeDAO> toProcess = new TreeMap<>()
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

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return (T) result;
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
