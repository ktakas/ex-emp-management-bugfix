package jp.co.sample.emp_management.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * 全データ件数を含む結果から従業員情報を抜き出すマッパーです.
 * 
 * @author kohei.takasaki
 *
 */
public class EmployeeResultSetExtractor implements ResultSetExtractor<EmployeeListAndCount> {

	@Override
	public EmployeeListAndCount extractData(ResultSet rs) throws SQLException, DataAccessException {
		
		EmployeeListAndCount employeeListAndCount = new EmployeeListAndCount();
		List<Employee> employeeList = new ArrayList<>();
		Integer dataCount = null;
		
		while(rs.next()) {
			Employee employee = new Employee();
			employee.setId(rs.getInt("id"));
			employee.setName(rs.getString("name"));
			employee.setImage(rs.getString("image"));
			employee.setGender(rs.getString("gender"));
			employee.setHireDate(rs.getDate("hire_date"));
			employee.setMailAddress(rs.getString("mail_address"));
			employee.setZipCode(rs.getString("zip_code"));
			employee.setAddress(rs.getString("address"));
			employee.setTelephone(rs.getString("telephone"));
			employee.setSalary(rs.getInt("salary"));
			employee.setCharacteristics(rs.getString("characteristics"));
			employee.setDependentsCount(rs.getInt("dependents_count"));
			employeeList.add(employee);
			
			if (dataCount == null) {
				dataCount = rs.getInt("data_count");
			}
		}
		employeeListAndCount.setDataCount(dataCount == null ? 0 : dataCount);
		employeeListAndCount.setEmployeeList(employeeList);
		return employeeListAndCount;
	}

}
