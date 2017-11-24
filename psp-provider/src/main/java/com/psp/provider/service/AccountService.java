package com.psp.provider.service;

import com.psp.provider.model.AccountBean;

public interface AccountService {
	
	/**
	 * 根据token查询服务商账号
	 * @param token
	 * @return
	 */
	AccountBean getAccountByToken(String token);
	
	/**
	 * 根据id查询服务商账号
	 * @param id
	 * @return
	 */
	AccountBean getAccountById(String id);

}
