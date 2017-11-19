package com.psp.park.controller.springmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.*;
import com.psp.park.controller.res.*;
import com.psp.park.controller.res.bean.*;
import com.psp.park.controller.springmvc.req.*;

/**
 * 用户相关接口
 **/
@Controller
@RequestMapping(value = "/wapp/user", produces = "application/json")
public class SUserController {
	@Autowired
	com.psp.park.controller.UserController userController;

	/**
	 * 登录
	 **/
	@RequestMapping("/v1/login")
	@ResponseBody
	public ObjectResult<RUserBean> login(@Validated LoginParam param, BindingResult error, HttpServletRequest request, HttpServletResponse response) {
		ObjectResult<RUserBean> res = new ObjectResult<RUserBean>();
		if (error.hasErrors()) {
			res.setRescode(BaseResult.param.getCode());
			res.setMsg(error.getFieldError().getDefaultMessage());
			return res;
		}

		return userController.login(param, request, response);
	}
}
