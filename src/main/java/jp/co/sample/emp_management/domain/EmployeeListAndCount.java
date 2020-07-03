package jp.co.sample.emp_management.domain;

import java.util.List;

/**
 * 全データ数を情報として持つ従業員情報ドメインです.
 * 
 * @author kohei.takasaki
 *
 */
public class EmployeeListAndCount {
	/** 全データ数です */
	private Integer dataCount;
	/** 従業員情報 */
	private List<Employee> employeeList;

	public Integer getDataCount() {
		return dataCount;
	}

	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}

	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	@Override
	public String toString() {
		return "EmployeeIncludedCount [dataCount=" + dataCount + ", employee=" + employeeList + "]";
	}

}
