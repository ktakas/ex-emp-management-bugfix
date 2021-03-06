package jp.co.sample.emp_management.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.domain.EmployeeListAndCount;
import jp.co.sample.emp_management.form.InsertEmployeeForm;
import jp.co.sample.emp_management.form.SearchForm;
import jp.co.sample.emp_management.form.UpdateEmployeeForm;
import jp.co.sample.emp_management.service.EmployeeService;

/**
 * 従業員情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private HttpSession session;

	private static final int PAGINATION_LIMIT = 10;

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public UpdateEmployeeForm setUpForm() {
		return new UpdateEmployeeForm();
	}

	/**
	 * 使用する検索フォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return 検索フォーム
	 */
	@ModelAttribute
	public SearchForm setUpSearchForm() {
		return new SearchForm();
	}

	/**
	 * 使用する従業員検索フォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return 従業員情報登録フォーム
	 */
	@ModelAttribute
	public InsertEmployeeForm setUpInsertForm() {
		InsertEmployeeForm form = new InsertEmployeeForm();
		form.setGender("男性");
		return form;
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員一覧を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員一覧画面
	 */
//	@RequestMapping("/showList")
	public String showList(Model model) {
		List<Employee> employeeList = employeeService.showList();
		model.addAttribute(employeeList);
		return "employee/list";
	}

	/**
	 * セッションのoffsetとlimitから取得件数を指定して従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員一覧画面
	 */
	@RequestMapping("/showList")
	public String showLimitedList(Model model) {
		Integer currentOffset = (Integer) session.getAttribute("offset");
		if (currentOffset == null) {
			currentOffset = 1;
			session.setAttribute("offset", currentOffset);
		}

		EmployeeListAndCount employeeListAndCount = employeeService.showLimitedList(currentOffset, PAGINATION_LIMIT);
		List<Employee> employeeList = employeeListAndCount.getEmployeeList();
		Integer dataCount = employeeListAndCount.getDataCount();

		// ページの上限番号を計算 全データ件数より大きければ
		session.setAttribute("limit", currentOffset-- + PAGINATION_LIMIT > dataCount ? dataCount : currentOffset + PAGINATION_LIMIT);
		
		session.setAttribute("dataCount", dataCount);
		model.addAttribute(employeeList);
		return "employee/list";
	}

	/**
	 * 現在のデータのオフセットを進めます.
	 * 
	 * @return 従業員一覧にリダイレクト
	 */
	@RequestMapping("/toNextPage")
	public String toNextPage() {
		Integer nextOffset = (Integer) session.getAttribute("offset") + PAGINATION_LIMIT;
		Integer dataCount = (Integer) session.getAttribute("dataCount");

		session.setAttribute("offset", nextOffset > dataCount ? nextOffset - 10 : nextOffset);
		return "redirect:/employee/showList";
	}

	/**
	 * 現在のデータのオフセットを戻します.
	 * 
	 * @return 従業員一覧にリダイレクト
	 */
	@RequestMapping("/toPrevPage")
	public String toPrevPage() {
		Integer prevOffset = (Integer) session.getAttribute("offset") - PAGINATION_LIMIT;
		session.setAttribute("offset", prevOffset < 1 ? 1 : prevOffset);
		return "redirect:/employee/showList";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細画面を出力します.
	 * 
	 * @param id    リクエストパラメータで送られてくる従業員ID
	 * @param model モデル
	 * @return 従業員詳細画面
	 */
	@RequestMapping("/showDetail")
	public String showDetail(String id, Model model) {
		Employee employee = employeeService.showDetail(Integer.parseInt(id));
		model.addAttribute("employee", employee);
		return "employee/detail";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を更新する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細(ここでは扶養人数のみ)を更新します.
	 * 
	 * @param form 従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@RequestMapping("/update")
	public String update(@Validated UpdateEmployeeForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showDetail(form.getId(), model);
		}
		Employee employee = new Employee();
		employee.setId(form.getIntId());
		employee.setDependentsCount(form.getIntDependentsCount());
		employeeService.update(employee);
		return "redirect:/employee/showList";
	}

	/**
	 * 従業員登録画面を表示します.
	 * 
	 * @return 従業員登録画面
	 */
	@RequestMapping("/toInsert")
	public String toInsert(Model model) {
		return "employee/insert";
	}

	/**
	 * 従業員名の一覧をマップで返却するAPIです.
	 * 
	 * @return 従業員名の一覧
	 */
	@ResponseBody
	@RequestMapping(value = "name", method = RequestMethod.GET)
	public Map<String, List<String>> getAllEmployeeNameList() {
		Map<String, List<String>> map = new HashMap<>();
		List<String> empNameList = (List<String>) session.getAttribute("empNameList");
		if (empNameList == null) {
			empNameList = employeeService.loadAllEmployeeName();
			session.setAttribute("empNameList", empNameList);
		}
		map.put("empNameList", empNameList);
		return map;
	}

	/**
	 * 従業員情報を登録します.
	 * 
	 * @param form   従業員登録フォーム
	 * @param result エラー情報を保持するオブジェクト
	 * @param model  リクエストスコープのオブジェクト
	 * @return 従業員一覧画面
	 */
	@RequestMapping("/insert")
	synchronized public String insert(@Validated InsertEmployeeForm form, BindingResult result, Model model,
			RedirectAttributes redirectAttributes) {
		Employee employee = new Employee();
		BeanUtils.copyProperties(form, employee);

		// メールアドレスが重複していないか確認
		boolean isAlreadyExist = employeeService.searchByMailAddress(form.getMailAddress()) != null;
		if (isAlreadyExist) {
			FieldError duplicateMailAddressError = new FieldError("insertEmployeeForm", "mailAddress",
					"メールアドレスが重複しています");
			result.addError(duplicateMailAddressError);
		}

		if (form.getImage() == null) {
			FieldError emptyImageError = new FieldError("insertEmployeeForm", "image", "画像を選択してください");
			result.addError(emptyImageError);
		}

		// hireDateはString->util.Dateへパースが必要
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String hireDate = form.getHireDate();
			employee.setHireDate(sdf.parse(hireDate));
		} catch (ParseException pe) {
			FieldError hireDateParseError = new FieldError("insertEmployeeForm", "hireDate", "入社日が未入力か、形式が不正です");
			result.addError(hireDateParseError);
		}

		if (result.hasErrors()) {
			return toInsert(model);
		}

		// base64形式へ画像をエンコード
		try {
			byte[] byteImage = form.getImage().getBytes();
			byte[] encodedImage = Base64.getEncoder().encode(byteImage);
			StringBuilder imageURI = new StringBuilder();
			imageURI.append(StringUtils.newStringUtf8(encodedImage));
			employee.setImage(imageURI.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		employeeService.insert(employee);
		redirectAttributes.addFlashAttribute("isInsertedOk", true);
		return "redirect:/employee/showList";
	}

	/**
	 * あいまい検索を行います.
	 * 
	 * @param employeeName 従業員名
	 * @param model        リクエストスコープのオブジェクト
	 * @return 一致する従業員の一覧画面
	 */
	@RequestMapping("/search")
	public String search(SearchForm form, Model model) {
		List<Employee> employeeList;
		String employeeName = form.getEmployeeName();
		if (employeeName.equals("")) {
			employeeList = employeeService.showList();
		} else {
			employeeList = employeeService.searchByName(employeeName);
			if (employeeList.size() == 0) {
				model.addAttribute("empNotFound", true);
				employeeList = employeeService.showList();
			}
		}

		model.addAttribute("employeeList", employeeList);
		return "employee/list";
	}
}
