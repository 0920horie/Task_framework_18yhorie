package jp.co.axiz.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.axiz.web.entity.UserInfo;
import jp.co.axiz.web.form.SelectForm;
import jp.co.axiz.web.service.impl.UserInfoService;

@Controller
public class SelectController {

	@Autowired
    MessageSource messageSource;

	@Autowired
	private UserInfoService userInfoService;


	@RequestMapping("/select")//検索画面から遷移
	public String login(@ModelAttribute("selectForm") SelectForm form, Model model) {
		return "select";//検索画面に遷移
	}

	@RequestMapping(value = "/list")//検索から遷移
	public String list(@Validated @ModelAttribute("selectForm") SelectForm form, BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {//入力値の判定
			model.addAttribute("errmsg", "入力されたデータはありませんでした。");
			return "select";//検索画面に遷移
		}

		UserInfo condition = new UserInfo();
		condition.setUserId(form.getUserId());
		condition.setUserName(form.getName());
		condition.setTelephone(form.getTel());

		List<UserInfo> resultList = userInfoService.find(condition);

		if(resultList.isEmpty()) {//入力値の判定
			model.addAttribute("errmsg","入力されたデータはありませんでした。" );
			return "select";//検索画面に遷移
		}else {
			model.addAttribute("userlist", resultList);
			return "selectResult";//検索結果に遷移
		}
	}
}
