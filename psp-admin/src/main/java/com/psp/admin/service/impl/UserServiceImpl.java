package com.psp.admin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.psp.admin.controller.res.bean.RUserBean;
import com.psp.admin.controller.res.bean.RUserLogsBean;
import com.psp.admin.controller.res.bean.RUserNewsBean;
import com.psp.admin.model.AdminBean;
import com.psp.admin.model.SellerBean;
import com.psp.admin.model.UserBean;
import com.psp.admin.model.UserLogBean;
import com.psp.admin.model.UserNewsBean;
import com.psp.admin.persist.dao.AdminDao;
import com.psp.admin.persist.dao.SellerDao;
import com.psp.admin.persist.dao.UserDao;
import com.psp.admin.persist.dao.UserLogDao;
import com.psp.admin.persist.dao.UserNewsDao;
import com.psp.admin.service.UserService;
import com.psp.admin.service.exception.ServiceException;
import com.psp.admin.service.res.PageResult;
import com.psp.util.StringUtil;

@Service
public class UserServiceImpl implements UserService {
	
	Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	AdminDao adminImpl;

	@Autowired
	SellerDao sellerImpl;
	
	@Autowired
	UserDao userImpl;
	
	@Autowired
	UserLogDao userLogImpl;

	@Autowired
	UserNewsDao userNewsImpl;

	@Override
	public PageResult<RUserBean> getUsers(int page, int pageSize, int filteType, int stype, String key, int status) {
		PageResult<RUserBean> result = new PageResult<RUserBean>();
		int count = userImpl.selectUserCount(filteType, stype, key, status);
		if(count == 0) {
			return null;
		}
		List<UserBean> resList = userImpl.selectUsers(page, pageSize, filteType, stype, key, status);
		List<RUserBean> resData = new ArrayList<>();
		logger.info(JSON.toJSONString(resList));
		if (resList != null && resList.size() > 0) {
			for (UserBean bean : resList) {
				RUserBean rb = parse(bean);
				resData.add(rb);
			}
		}
		result.setCount(count);
		result.setData(resData);
		return result;
	}	
	
	/**
	 * 格式化数据
	 * @param user
	 * @return
	 */
	private RUserBean parse(UserBean user) {
		RUserBean res = new RUserBean();
		res.setName(user.getName());
		res.setAdminJson(user.getAdminJson());
		res.setAid(user.getAid());
		if(user.getAllotTime() != null) {
			res.setAllotTime(user.getAllotTime().getTime() / 1000);
		}
		res.setCompanyName(user.getCompanyName());
		res.setCreateTime(user.getCreateTime().getTime() / 1000);
		res.setIsAllot(user.getIsAllot());
		res.setLabel(user.getLabel());
		res.setLevel(user.getLevel());
		res.setOrderNum(user.getOrderNum());
		res.setOrigin(user.getOrigin());
		res.setPhoneNum(user.getPhoneNum());
		res.setPosition(user.getPosition());
		res.setSellerJson(user.getSellerJson());
		res.setSid(user.getSid());
		res.setUid(user.getUid());
		res.setStatus(user.getStatus());
		logger.info(JSON.toJSONString(res));
		return res;
	}

	@Override
	public RUserBean getDetail(String aid, String uid) {
		UserBean user = userImpl.selectUserById(uid);
		if(user == null) {
			throw new ServiceException("object_is_not_exist", "客户");
		}
		return parse(user);
	}

	@Override
	public PageResult<RUserLogsBean> getUserLogs(String aid, String uid, String key) {
		PageResult<RUserLogsBean> result = new PageResult<RUserLogsBean>();
		int count = userLogImpl.selectUserLogsCount(uid, key);
		if(count == 0) {
			return null;
		}
		List<UserLogBean> resList = userLogImpl.selectUserLogs(uid, key);
		List<RUserLogsBean> resData = new ArrayList<>();
		logger.info(JSON.toJSONString(resList));
		if (resList != null && resList.size() > 0) {
			for (UserLogBean bean : resList) {
				RUserLogsBean rb = parse(bean);
				resData.add(rb);
			}
		}
		result.setCount(count);
		result.setData(resData);
		return result;
	}

	@Override
	public PageResult<RUserNewsBean> getUserNews(String sid, int page, int pageSize, int stype, String key,
			String uid) {
		PageResult<RUserNewsBean> result = new PageResult<RUserNewsBean>();
		int count = userNewsImpl.selectUserNewsCount(null, stype, key, uid);
		if(count == 0) {
			return null;
		}
		List<UserNewsBean> resList = userNewsImpl.selectUserNews(page, pageSize, null, stype, key, uid);
		List<RUserNewsBean> resData = new ArrayList<>();
		logger.info(JSON.toJSONString(resList));
		if (resList != null && resList.size() > 0) {
			for (UserNewsBean bean : resList) {
				RUserNewsBean rb = parse(bean);
				resData.add(rb);
			}
		}
		result.setCount(count);
		result.setData(resData);
		return result;
	}
	
	/**
	 * 格式化数据
	 * @param bean
	 * @return
	 */
	private RUserNewsBean parse(UserNewsBean bean) {
		RUserNewsBean res = new RUserNewsBean();
		res.setContent(bean.getContent());
		if(bean.getCreateTime() != null) {
			res.setCreateTime(bean.getCreateTime().getTime() / 1000);
		}
		res.setLabel(bean.getLabel());
		res.setNid(bean.getNid());
		res.setOrigin(bean.getOrigin());
		res.setUid(bean.getUid());
		res.setUserJson(bean.getUserJson());
		res.setSid(bean.getSid());
		res.setSellerJson(bean.getSellerJson());
		return res;
	}
	
	/**
	 * 插入客户操作日志
	 * @param type 操作类型
	 * @param sid 销售id
	 * @param sellerName 销售名称
	 * @param sellerJson 销售json
	 * @param user 用户信息
	 * @param aid 管理员ID
	 * @param adminName 管理员名称
	 * @param adminJson 管理员json
	 * @return
	 */
	private boolean insertUserLog(int type, String sid, String sellerName, String sellerJson, 
			UserBean user, String aid, String adminName, String adminJson) {
		UserLogBean userlog = new UserLogBean();
		userlog.setUid(user.getUid());
		if(!StringUtil.isEmpty(sid)) {
			userlog.setSid(sid);
			userlog.setSellerJson(sellerJson);
		}
		if(!StringUtil.isEmpty(aid)) {
			userlog.setAid(aid);
			userlog.setAdminJson(adminJson);
		}
		// 0管理员分配 1 新建客户 2 修改客户 3归档客户
		JSONObject userJson = new JSONObject();
		userJson.put("name", user.getName());
		userJson.put("phoneNum", user.getPhoneNum());
		userJson.put("companyName", user.getCompanyName());
		userJson.put("position", user.getPosition());
		userlog.setContent(userJson.toJSONString());
		userlog.setType(type);
		return userLogImpl.insert(userlog) > 0;
		
	}
	
	
	/**
	 * 格式化客户操作日志
	 * @param bean
	 * @return
	 */
	private RUserLogsBean parse(UserLogBean bean) {
		RUserLogsBean userlog = new RUserLogsBean();
		userlog.setUid(bean.getUid());
		userlog.setSid(bean.getSid());
		userlog.setSellerJson(bean.getSellerJson());
		userlog.setLid(bean.getLid());
		userlog.setAid(bean.getAid());
		userlog.setAdminJson(bean.getAdminJson());
		userlog.setContent(bean.getContent());
		userlog.setType(bean.getType());
		return userlog;
	}

	@Override
	public int getUserNum(int isAllot) {
		//TODO:存入缓存 客户数量存入缓存
		int count = userImpl.selectUserCount(0, 0, null, isAllot);
		return count;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean allot(String aid, String sid, String uid) {
		AdminBean admin = adminImpl.selectOneById(aid);
		logger.info("管理员：" + JSON.toJSONString(admin));
		UserBean user = userImpl.selectUserById(uid);
		logger.info("编辑用户" + JSON.toJSONString(user));
		if(user == null) {
			throw new ServiceException("object_is_not_exist", "被分配的客户");
		}
		if(user.getIsAllot() == 1) {
			throw new ServiceException("user_has_alloted");
		}
		JSONObject adminJson = new JSONObject();
		adminJson.put("name", admin.getUsername());
		adminJson.put("phone", admin.getPhoneNum());
		SellerBean seller = sellerImpl.selectOneById(sid);
		logger.info("分配的销售：" + JSON.toJSONString(seller));
		boolean flag = false;
		if(seller == null) {
			throw new ServiceException("object_is_not_exist", "分配的销售");
		}
		JSONObject sellerJson = new JSONObject();
		sellerJson.put("name", seller.getUsername());
		sellerJson.put("phone", seller.getPhoneNum());
		user.setSid(sid);
		user.setSellerJson(sellerJson.toJSONString());
		user.setAid(aid);
		user.setAdminJson(adminJson.toJSONString());
		user.setIsAllot(1);// 已分配
		flag = userImpl.allotUser(user) > 0;
		if(!flag) {
			throw new ServiceException("update_user_error");
		}
		flag = insertUserLog(0, sid, seller.getUsername(), sellerJson.toJSONString(), user, aid, admin.getUsername(), adminJson.toJSONString());
		if(!flag) {
			throw new ServiceException("create_user_log_error");
		}
		return flag;
	}


}
