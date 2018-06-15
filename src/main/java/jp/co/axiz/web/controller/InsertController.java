package jp.co.axiz.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.axiz.web.entity.SessionInfo;
import jp.co.axiz.web.entity.UserInfo;
import jp.co.axiz.web.form.InsertForm;
import jp.co.axiz.web.service.impl.UserInfoService;

@Controller
public class InsertController {

	@Autowired
	private SessionInfo sessionInfo;

	@Autowired
    MessageSource messageSource;

	@Autowired
	private UserInfoService userInfoService;

	@RequestMapping("/insert")//登録画面から遷移
	public String insert(@ModelAttribute("insertForm") InsertForm form, Model model) {
		return "insert";//登録画面へ遷移
	}

	@RequestMapping(value = "/insertConfirm", method = RequestMethod.POST)//登録確認画面へ遷移
	public String insertConfirm(@Validated @ModelAttribute("insertForm") InsertForm form, BindingResult bindingResult,
			Model model) {
		//入力チェック
		if (bindingResult.hasErrors()) {//項目の入力をチェック
			model.addAttribute("errmsg","必須項目を入力して下さい" );
			return "insert";//登録画面へ遷移
		}

		UserInfo user = new UserInfo();
		user.setUserName(form.getName());
		user.setTelephone(form.getTel());
		user.setPassword(form.getPassword());

		sessionInfo.setNewUser(user);//user情報を保存

		return "insertConfirm";//登録確認画面へ遷移
	}

	@RequestMapping(value = "/insertBack")
	public String insertBack(@ModelAttribute("insertForm") InsertForm form, Model model) {

		UserInfo user = sessionInfo.getNewUser();

		form.setName(user.getUserName());
		form.setTel(user.getTelephone());
		form.setPassword(user.getPassword());

		return "insert";//登録画面へ遷移
	}

	@RequestMapping(value = "/insert", method = RequestMethod.POST)//登録画面から
	public String insertExecute(@Validated @ModelAttribute("insertForm") InsertForm form, BindingResult bindingResult,
			Model model) {

		UserInfo user = sessionInfo.getNewUser();

		if(!user.getPassword().equals(form.getConfirmPassword())) {//入力チェック
			model.addAttribute("errmsg", "前画面で入力したパスワードと一致しません");

			form.setConfirmPassword("");

			return "insertConfirm";//登録確認画面へ遷移
		}

		int id = userInfoService.insert(user);

		sessionInfo.setNewUser(null);

		form.setUserId(id);

		model.addAttribute("user", sessionInfo.getLoginUser());

		return "insertResult";
	}
}
