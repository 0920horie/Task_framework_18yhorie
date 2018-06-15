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
import jp.co.axiz.web.form.UpdateForm;
import jp.co.axiz.web.service.impl.UserInfoService;

@Controller
public class UpdateController {

	@Autowired
	private SessionInfo sessionInfo;

	@Autowired
    MessageSource messageSource;

	@Autowired
	private UserInfoService userInfoService;

	@RequestMapping("/update")//更新画面へ遷移
	public String update(@ModelAttribute("updateForm") UpdateForm form, Model model) {
		return "update";
	}

	@RequestMapping(value = "/updateInput", method = RequestMethod.POST)//更新確認画面へ遷移
	public String updateInput(@Validated @ModelAttribute("updateForm") UpdateForm form, BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasFieldErrors("userId")) {//入力チェック

			model.addAttribute("errmsg", "必須項目を入力して下さい");
			return "update";
		}

		UserInfo user = userInfoService.findById(form.getUserId());

		if(user == null) {//入力チェック
			model.addAttribute("errmsg","入力されたデータは存在しません");
			return "update";
		}

		sessionInfo.setPrevUser(user);

		form.setNewName(user.getUserName());
		form.setNewTel(user.getTelephone());
		form.setNewPassword(user.getPassword());

		return "updateInput";
	}

	@RequestMapping(value = "/updateConfirm", method = RequestMethod.POST)
	public String updateConfirm(@Validated @ModelAttribute("updateForm") UpdateForm form, BindingResult bindingResult,
			Model model) {

		if (form.hasRequiredError()) {
			model.addAttribute("errmsg", "入力されたデータは存在しません");
			return "updateInput";//更新内容入力画面へ遷移
		}

		UserInfo beforeUser = sessionInfo.getPrevUser();

		UserInfo afterUser = new UserInfo();
		afterUser.setUserId(beforeUser.getUserId());
		afterUser.setUserName(form.getNewName());
		afterUser.setTelephone(form.getNewTel());
		afterUser.setPassword(form.getNewPassword());

		if(afterUser.equals(beforeUser)) {
			model.addAttribute("errmsg", "1項目以上変更してください");
			return "updateInput";//更新内容入力画面へ遷移
		}

		sessionInfo.setAfterUser(afterUser);

		form.setPrevName(beforeUser.getUserName());
		form.setPrevTel(beforeUser.getTelephone());
		form.setPrevPassword(beforeUser.getPassword());

		if(beforeUser.getPassword().equals(afterUser.getPassword())) {
			form.setConfirmNewPassword(afterUser.getPassword());
		}

		return "updateConfirm";//更新確認画面へ遷移
	}

	@RequestMapping(value = "/updateInputBack")
	public String updateInputBack(@ModelAttribute("updateForm") UpdateForm form, Model model) {

		UserInfo afterUser = sessionInfo.getAfterUser();

		form.setUserId(afterUser.getUserId());
		form.setNewName(afterUser.getUserName());
		form.setNewTel(afterUser.getTelephone());
		form.setNewPassword(afterUser.getPassword());

		return "updateInput";//更新内容入力画面へ遷移
	}


	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateExecute(@Validated @ModelAttribute("updateForm") UpdateForm form, BindingResult bindingResult,
			Model model) {

		UserInfo afterUser = sessionInfo.getAfterUser();

		if(!afterUser.getPassword().equals(form.getConfirmNewPassword())) {
			model.addAttribute("errmsg","前画面で入力したパスワードと一致しません");

			form.setConfirmNewPassword("");

			UserInfo beforeUser = sessionInfo.getPrevUser();
			form.setPrevName(beforeUser.getUserName());
			form.setPrevTel(beforeUser.getTelephone());
			form.setPrevPassword(beforeUser.getPassword());

			return "updateConfirm";//更新確認画面へ遷移
		}

		userInfoService.update(afterUser);

		sessionInfo.setAfterUser(null);
		sessionInfo.setPrevUser(null);

		model.addAttribute("user", sessionInfo.getLoginUser());

		return "updateResult";//更新結果画面へ遷移
	}

}
