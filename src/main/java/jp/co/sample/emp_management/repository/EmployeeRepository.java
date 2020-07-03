package jp.co.sample.emp_management.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import jp.co.sample.emp_management.domain.Employee;

/**
 * employeesテーブルを操作するリポジトリ.
 * 
 * @author igamasayuki
 * 
 */
@Repository
public class EmployeeRepository {

	/**
	 * Employeeオブジェクトを生成するローマッパー.
	 */
	private static final RowMapper<Employee> EMPLOYEE_ROW_MAPPER = (rs, i) -> {
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
		return employee;
	};
	
	/**
	 * ResultSetオブジェクトから名前のみを取得するローマッパー.
	 */
	private static final RowMapper<String> EMPLOYEE_NAME_ROW_MAPPER = (rs, i) -> {
		return rs.getString("name");
	};
	
	/**
	 * IDの最大値を取得するためのマッパー.
	 */
	private static final RowMapper<Integer> GET_MAX_ID_ROW_MAPPER = (rs, i) -> {
		return rs.getInt("max");
	};

	@Autowired
	private NamedParameterJdbcTemplate template;

	/**
	 * 従業員一覧情報を入社日順で取得します.
	 * 
	 * @return 全従業員一覧 従業員が存在しない場合はサイズ0件の従業員一覧を返します
	 */
	public List<Employee> findAll() {
		String sql = "SELECT id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count FROM employees ORDER BY hire_date DESC";

		List<Employee> developmentList = template.query(sql, EMPLOYEE_ROW_MAPPER);

		return developmentList;
	}

	/**
	 * 主キーから従業員情報を取得します.
	 * 
	 * @param id 検索したい従業員ID
	 * @return 検索された従業員情報
	 * @exception 従業員が存在しない場合は例外を発生します
	 */
	public Employee load(Integer id) {
		String sql = "SELECT id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count FROM employees WHERE id=:id";

		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

		Employee development = template.queryForObject(sql, param, EMPLOYEE_ROW_MAPPER);

		return development;
	}
	
	/**
	 * メールアドレスから従業員を検索します.
	 * 
	 * @param mailAddress メールアドレス
	 * @return 従業員情報(いなければnull)
	 */
	public Employee searchByMailAddress(String mailAddress) {
		String searchSql = "SELECT id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count FROM employees WHERE mail_address=:mailAddress";
		SqlParameterSource param = new MapSqlParameterSource().addValue("mailAddress", mailAddress);
		try {
			Employee employee = template.queryForObject(searchSql, param, EMPLOYEE_ROW_MAPPER);
			return employee;
		} catch (DataAccessException e) {
			// 合致する従業員がいない場合
			return null;
		}
	}

	/**
	 * 従業員情報を変更します.
	 */
	public void update(Employee employee) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(employee);

		String updateSql = "UPDATE employees SET dependents_count=:dependentsCount WHERE id=:id";
		template.update(updateSql, param);
	}

	/**
	 * 従業員情報をあいまい検索します.
	 * 
	 * @param employeeName 従業員名
	 * @return 一致する従業員のリスト
	 */
	public List<Employee> searchByName(String employeeName) {
		String searchSql = "SELECT id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count FROM employees WHERE name LIKE :employeeName ORDER BY hire_date DESC;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("employeeName", "%" + employeeName + "%");
		return template.query(searchSql, param, EMPLOYEE_ROW_MAPPER);
	}

	/**
	 * 従業員情報を保存します.
	 * 
	 * @param employee 従業員情報
	 */
	synchronized public void insert(Employee employee) {
		// 現在の従業員IDの最大値+1を従業員にセットする
		String getMaxIdSql = "SELECT max(id) FROM employees;";
		Integer maxId = template.query(getMaxIdSql, GET_MAX_ID_ROW_MAPPER).get(0);
		System.out.println(maxId);
		employee.setId(maxId == null ? 0 : ++maxId);
		
		String insertSql = "INSERT INTO employees(id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count) VALUES (:id,:name,:image,:gender,:hireDate,:mailAddress,:zipCode,:address,:telephone,:salary,:characteristics,:dependentsCount);";
		SqlParameterSource param = new BeanPropertySqlParameterSource(employee);
		template.update(insertSql, param);
	}
}
