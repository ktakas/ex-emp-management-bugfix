package jp.co.sample.emp_management.form;

/**
 * 従業員の検索フォームです.
 * 
 * @author kohei.takasaki
 *
 */
public class SearchForm {

	/** 従業員名 */
	private String employeeName;

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@Override
	public String toString() {
		return "SearchForm [employeeName=" + employeeName + "]";
	}

}
