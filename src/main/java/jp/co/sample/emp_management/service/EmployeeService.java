package jp.co.sample.emp_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.domain.EmployeeListAndCount;
import jp.co.sample.emp_management.repository.EmployeeRepository;

/**
 * 従業員情報を操作するサービス.
 * 
 * @author igamasayuki
 *
 */
@Service
@Transactional
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	/**
	 * 従業員情報を全件取得します.
	 * 
	 * @return　従業員情報一覧
	 */
	public List<Employee> showList() {
		List<Employee> employeeList = employeeRepository.findAll();
		return employeeList;
	}
	
	/**
	 * 従業員情報をオフセットとリミットを指定して取得します.
	 * 
	 * @return　従業員情報一覧(
	 */
	public EmployeeListAndCount showLimitedList(int offset, int limit) {
		// 1件目は0番目のデータなのでここで差異を吸収
		EmployeeListAndCount employeeListAndCount = employeeRepository.findEmployeesByOffsetAndLimit(--offset, limit);
		return employeeListAndCount;
	}
	
	/**
	 * 従業員情報を取得します.
	 * 
	 * @param id ID
	 * @return 従業員情報
	 * @throws 検索されない場合は例外が発生します
	 */
	public Employee showDetail(Integer id) {
		Employee employee = employeeRepository.load(id);
		return employee;
	}
	
	/**
	 * メールアドレスから従業員情報を検索します.
	 * 
	 * @param mailAddress メールアドレス
	 * @return 従業員情報(いなければnull)
	 */
	public Employee searchByMailAddress(String mailAddress) {
		return employeeRepository.searchByMailAddress(mailAddress);
	}
	
	/**
	 * 従業員情報を更新します.
	 * 
	 * @param employee　更新した従業員情報
	 */
	public void update(Employee employee) {
		employeeRepository.update(employee);
	}
	
	/**
	 * 名前から従業員をあいまい検索します.
	 * 
	 * @param employeeName 従業員名
	 * @return 一致する従業員のリスト
	 */
	public List<Employee> searchByName(String employeeName) {
		return employeeRepository.searchByName(employeeName);
	}

	/**
	 * 従業員をインサートします.
	 * 
	 * @param employee インサートする従業員
	 */
	public void insert(Employee employee) {
		employeeRepository.insert(employee);
	}
	
	/**
	 * 従業員名の一覧を取得します.
	 */
	public List<String> loadAllEmployeeName() {
		return employeeRepository.loadAllEmployeeName();
	}
}
