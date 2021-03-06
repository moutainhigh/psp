package com.psp.sellcenter.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.psp.sellcenter.cache.dao.ServiceCacheDao;
import com.psp.sellcenter.controller.res.bean.ROrderBean;
import com.psp.sellcenter.controller.res.bean.ROrderContractBean;
import com.psp.sellcenter.controller.res.bean.ROrderFeedbackBean;
import com.psp.sellcenter.controller.res.bean.ROrderLogsBean;
import com.psp.sellcenter.controller.res.bean.RServiceProviderBean;
import com.psp.sellcenter.model.CategoryBean;
import com.psp.sellcenter.model.OrderBean;
import com.psp.sellcenter.model.OrderContractBean;
import com.psp.sellcenter.model.OrderFeedbackBean;
import com.psp.sellcenter.model.OrderLogBean;
import com.psp.sellcenter.model.ProviderBean;
import com.psp.sellcenter.model.SellerBean;
import com.psp.sellcenter.model.UserBean;
import com.psp.sellcenter.persist.dao.OrderDao;
import com.psp.sellcenter.persist.dao.OrderLogDao;
import com.psp.sellcenter.persist.dao.ProviderDao;
import com.psp.sellcenter.persist.dao.SellerDao;
import com.psp.sellcenter.persist.dao.UserDao;
import com.psp.sellcenter.service.OrderService;
import com.psp.sellcenter.service.exception.ServiceException;
import com.psp.sellcenter.service.res.PageResult;
import com.psp.util.AppTextUtil;
import com.psp.util.DateUtil;
import com.psp.util.NumUtil;
import com.psp.util.StringUtil;
import com.psp.util.VCodeSender;

@Service
public class OrderServiceImpl implements OrderService {

	Logger logger = Logger.getLogger(this.getClass());

	final String qiniulinkurl = "http://os4z3g2v6.bkt.clouddn.com/";

	// 发送手机验证码
	VCodeSender phoneCode = VCodeSender.getInstance("N1330628", "t7NYh90uB");

	@Autowired
	SellerDao sellerImpl;

	@Autowired
	OrderDao orderImpl;

	@Autowired
	OrderLogDao orderLogImpl;

	@Autowired
	UserDao userImpl;

	@Autowired
	ProviderDao providerImpl;

	@Autowired
	ServiceCacheDao serviceCacheImpl;

	@Override
	public PageResult<ROrderBean> getOrders(String sid, int page, int pageSize, int filteType, int stype, String key,
			String uid, int stage) {
		PageResult<ROrderBean> result = new PageResult<ROrderBean>();
		SellerBean seller = sellerImpl.selectOneById(sid);
		if (seller == null) {
			throw new ServiceException("object_is_not_exist", "销售");
		}
		int count = orderImpl.selectOrderCount(sid, filteType, stype, key, uid, stage);
		if (count == 0) {
			return null;
		}
		List<OrderBean> resList = orderImpl.selectOrders(page, pageSize, sid, filteType, stype, key, uid, stage);
		List<ROrderBean> resData = new ArrayList<>();
		if (resList != null && resList.size() > 0) {
			for (OrderBean bean : resList) {
				ROrderBean rb = parse(bean);
				resData.add(rb);
			}
		}
		result.setCount(count);
		result.setData(resData);
		return result;
	}

	/**
	 * 格式化数据
	 * 
	 * @param bean
	 * @return
	 */
	private ROrderBean parse(OrderBean bean) {
		ROrderBean order = new ROrderBean();
		if (bean.getCompleteTime() != null) {
			order.setCompleteTime(bean.getCompleteTime().getTime() / 1000);
		}
		bean.setContent(bean.getContent());
		if (bean.getCreateTime() != null) {
			order.setCreateTime(bean.getCreateTime().getTime() / 1000);
		}
		if (bean.getExpectedTime() != null) {
			order.setExpectedTime(bean.getExpectedTime().getTime() / 1000);
		}
		if (bean.getCloseTime() != null) {
			order.setCloseTime(bean.getCloseTime().getTime() / 1000);
		}
		if (bean.getUpdateTime() != null) {
			order.setUpdateime(bean.getUpdateTime().getTime() / 1000);
		}
		order.setIsAllot(bean.getIsAllot());
		order.setIsNeedInvoice(bean.getIsNeedInvoice());
		order.setLabel(bean.getLabel());
		order.setOid(bean.getOid());
		order.setOrderNo(bean.getOrderNo());
		order.setPid(bean.getPid());
		order.setContractStatus(bean.getContractStatus());
		if (bean.getUser() != null) {
			UserBean userBean = bean.getUser();
			JSONObject userJson = new JSONObject();
			userJson.put("name", userBean.getName());
			userJson.put("phone", userBean.getPhoneNum());
			userJson.put("companyName", userBean.getCompanyName());
			order.setUserJson(userJson.toJSONString());
		}
		if (bean.getProvider() != null) {
			ProviderBean proBean = bean.getProvider();
			JSONObject providerJson = new JSONObject();
			providerJson.put("name", proBean.getName());
			providerJson.put("phone", proBean.getPhoneNum());
			providerJson.put("contact", proBean.getContact());

			order.setProviderJson(providerJson.toJSONString());
		}
		if (bean.getContracts() != null) {
			List<OrderContractBean> contracts = bean.getContracts();
			List<ROrderContractBean> recontracts = new ArrayList<ROrderContractBean>();
			if (contracts.size() > 0) {
				for (OrderContractBean con : contracts) {
					recontracts.add(parse(con));
				}
			}
			order.setContracts(recontracts);
		}
		if (bean.getFeedback() != null) {
			OrderFeedbackBean feed = bean.getFeedback();
			ROrderFeedbackBean rfeed = new ROrderFeedbackBean();
			rfeed.setAverageScore(feed.getAverageScore());
			rfeed.setFid(feed.getFid());
			rfeed.setContent(feed.getContent());
			rfeed.setSuggestion(feed.getSuggestion());
			order.setFeedback(rfeed);
		}

		order.setSid(bean.getSid());
		order.setStage(bean.getStage());
		order.setStatus(bean.getStatus());
		order.setUid(bean.getUid());
		order.setContent(bean.getContent());
		return order;
	}

	private ROrderContractBean parse(OrderContractBean con) {
		ROrderContractBean rcontract = new ROrderContractBean();
		rcontract.setCid(con.getCid());
		rcontract.setContractNo(con.getContractNo());
		rcontract.setContractUrl(qiniulinkurl + con.getContractUrl());
		if (con.getEndTime() != null) {
			rcontract.setEndTime(con.getEndTime().getTime() / 1000);
		}
		if (con.getSignTime() != null) {
			rcontract.setSignTime(con.getSignTime().getTime() / 1000);
		}
		if (con.getStartTime() != null) {
			rcontract.setStartTime(con.getStartTime().getTime() / 1000);
		}
		rcontract.setMoney(con.getMoney());
		rcontract.setOid(con.getOid());
		rcontract.setPartyA(con.getPartyA());
		rcontract.setPartyB(con.getPartyB());
		rcontract.setPayment(con.getPayment());
		rcontract.setPaymentDesc(con.getPaymentDesc());
		rcontract.setPaymentWay(con.getPaymentWay());
		rcontract.setName(con.getName());
		rcontract.setType(con.getType());

		return rcontract;
	}

	/**
	 * 新建工单
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean addOrder(SellerBean seller, String pid, String uid, String label, String content) {
		boolean flag = false;
		if (seller == null) {
			throw new ServiceException("object_is_not_exist", "销售");
		}
		JSONObject sellerJson = new JSONObject();
		sellerJson.put("name", seller.getUsername());
		sellerJson.put("phone", seller.getPhoneNum());
		UserBean user = userImpl.selectUserById(uid);
		if (user == null) {
			throw new ServiceException("object_is_not_exist", "客户");
		}
		OrderBean order = new OrderBean();
		String oid = AppTextUtil.getPrimaryKey();
		String prefix = "GD";// 工单前缀
		String orderNo = AppTextUtil.getRandomNo(prefix);
		order.setOid(oid);
		order.setOrderNo(orderNo);
		order.setPid(pid);
		ProviderBean proBean = providerImpl.selectOneById(pid);
		JSONObject providerJson = new JSONObject();
		providerJson.put("name", proBean.getName());
		providerJson.put("phone", proBean.getPhoneNum());
		providerJson.put("contact", proBean.getContact());
		order.setProviderJson(providerJson.toJSONString());
		order.setUid(uid);
		order.setSid(seller.getSid());
		order.setSellerJson(sellerJson.toJSONString());
		order.setLabel(label);
		order.setContent(content);
		order.setStatus(2);// 待处理
		order.setStage(1);// 进行中
		order.setIsAllot(1);// 已分配
		order.setContractStatus(0);// 合同待上传
		order.setDataType(seller.getType() == 0 ? 0 : 1);

		flag = orderImpl.insert(order) > 0;
		if (!flag) {
			throw new ServiceException("create_order_error");
		}

		// 派单完发送短信
		// phoneCode.send(proBean.getPhoneNum(), "您有新的工单待处理，请登录到科技服务平台查看", null);
		flag = insertOrderLog(oid, orderNo, seller.getSid(), sellerJson.toJSONString(), pid,
				providerJson.toJSONString(), 0, null, 0);// 0 创建并分配 1 编辑 2 派单 3 上传合同 4 调查反馈 5 归档
		if (!flag) {
			throw new ServiceException("create_order_log_error");
		}
		return flag;
	}

	/**
	 * 插入订单操作日志表
	 * 
	 * @param oid
	 * @param sid
	 * @param jsonString
	 * @param pid
	 * @param provider
	 * @param type
	 * @param content
	 * @param contractType
	 * @return
	 */
	private boolean insertOrderLog(String oid, String orderNo, String sid, String sellerJson, String pid,
			String provider, int type, String content, int contractType) {
		OrderLogBean orderlog = new OrderLogBean();
		orderlog.setOid(oid);
		if (!StringUtil.isEmpty(sid)) {
			orderlog.setSid(sid);
			orderlog.setSellerJson(sellerJson);
		}
		if (!StringUtil.isEmpty(pid)) {
			orderlog.setPid(pid);
			orderlog.setProviderJson(provider);
		}
		JSONObject contentJson = new JSONObject();
		// 0 创建并分配 1 编辑 2 派单 3 上传合同 4 确认完成 5 拒绝完成 6 调查反馈 7 归档关闭
		contentJson.put("oid", oid);
		contentJson.put("orderNo", orderNo);
		if (type == 3) {
			contentJson.put("type", contractType);
			contentJson.put("contractNo", content);// 上传合同的合同编号
		} else {
			contentJson.put("content", content);
		}
		orderlog.setContent(contentJson.toJSONString());
		orderlog.setType(type);
		return orderLogImpl.insert(orderlog) > 0;
	}

	@Override
	public RServiceProviderBean getServiceProviders() {
		RServiceProviderBean bean = new RServiceProviderBean();
		JSONArray jsonArray = new JSONArray();
		String cateStr = serviceCacheImpl.getCategoryCache();
		if (StringUtil.isEmpty(cateStr)) {
			List<CategoryBean> cates = providerImpl.selectAllCates();
			if (cates == null) {
				return null;
			}
			List<ProviderBean> providers = providerImpl.selectAll();
			Map<Integer, JSONArray> AllProviders = new HashMap<Integer, JSONArray>();

			JSONArray subProviders = new JSONArray();
			for (ProviderBean provider : providers) {
				if (provider.getCid() != null) {
					JSONObject providerObject = new JSONObject();
					providerObject.put("label", provider.getName());
					providerObject.put("value", provider.getPid());
					if (AllProviders.containsKey(provider.getCid())) {// map中异常批次已存在，将该数据存放到同一个key（key存放的是异常批次）的map中
						AllProviders.get(provider.getCid()).add(providerObject);
					} else {// map中不存在，新建key，用来存放数据
						subProviders = new JSONArray();
						subProviders.add(providerObject);
						AllProviders.put(provider.getCid(), subProviders);
					}
				}
			}

			// 获取所有服务项
			List<CategoryBean> Services = providerImpl.selectService(null);
			Map<Integer, JSONArray> AllServices = new HashMap<Integer, JSONArray>();
			JSONArray subCates = new JSONArray();
			for (CategoryBean cate : Services) {
				JSONObject serviceObject = new JSONObject();
				serviceObject.put("label", cate.getName());
				serviceObject.put("value", cate.getCid());
				if (AllProviders.get(cate.getCid()) == null) {
					serviceObject.put("disabled", true);
				} else {
					serviceObject.put("children", AllProviders.get(cate.getCid()));
				}
				if (AllServices.containsKey(cate.getParentId())) {// map中异常批次已存在，将该数据存放到同一个key（key存放的是异常批次）的map中
					AllServices.get(cate.getParentId()).add(serviceObject);
				} else {// map中不存在，新建key，用来存放数据
					subCates = new JSONArray();
					subCates.add(serviceObject);
					AllServices.put(cate.getParentId(), subCates);
				}
			}

			// 构造三级树
			for (CategoryBean ca : cates) {
				JSONObject firstObject = new JSONObject();
				firstObject.put("label", ca.getName());
				firstObject.put("value", ca.getCid());
				List<CategoryBean> children = ca.getChildern();
				JSONArray secondCates = new JSONArray();
				if (children != null && children.size() > 0) {
					for (CategoryBean c : children) {
						JSONObject secondObject = new JSONObject();
						secondObject.put("label", c.getName());
						secondObject.put("value", c.getCid());
						if (AllServices.get(c.getCid()) != null) {
							secondObject.put("children", AllServices.get(c.getCid()));
						}
						secondCates.add(secondObject);
					}
					firstObject.put("children", secondCates);
				}
				jsonArray.add(firstObject);
			}
			String jsonMenu = JSON.toJSONString(jsonArray);
			serviceCacheImpl.setCategoryCache(jsonMenu);
		} else {
			jsonArray = JSON.parseArray(cateStr);
		}
		bean.setService(jsonArray);
		return bean;
	}

	@Override
	public int getOrderNum2Seller(String sid, int stage) {
		int count = orderImpl.selectOrderCount2Seller(sid, stage);
		return count;
	}

	@Override
	public ROrderBean getDetail(String sid, String oid) {
		OrderBean order = orderImpl.selectOrderById(oid);
		if (order == null) {
			throw new ServiceException("object_is_not_exist", "工单");
		}
		return parse(order);
	}

	@Override
	public PageResult<ROrderLogsBean> getOrderogs(String sid, String oid, String key) {
		PageResult<ROrderLogsBean> result = new PageResult<ROrderLogsBean>();
		int count = orderLogImpl.selectOrderLogsCount(oid, key);
		if (count == 0) {
			return null;
		}
		List<OrderLogBean> resList = orderLogImpl.selectOrderLogs(oid, key);
		List<ROrderLogsBean> resData = new ArrayList<>();
		if (resList != null && resList.size() > 0) {
			for (OrderLogBean bean : resList) {
				ROrderLogsBean rb = parse(bean);
				resData.add(rb);
			}
		}
		result.setCount(count);
		result.setData(resData);
		return result;
	}

	/**
	 * 格式化工单操作日志信息
	 * 
	 * @param bean
	 * @return
	 */
	private ROrderLogsBean parse(OrderLogBean bean) {
		ROrderLogsBean log = new ROrderLogsBean();
		log.setContent(bean.getContent());
		log.setLid(bean.getLid());
		log.setOid(bean.getOid());
		log.setPid(bean.getPid());
		log.setProviderJson(bean.getProviderJson());
		log.setSellerJson(bean.getSellerJson());
		log.setSid(bean.getSid());
		log.setType(bean.getType());
		if (bean.getCreateTime() != null) {
			log.setCreateTime(bean.getCreateTime().getTime() / 1000);
		}
		return log;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean allotOrder(String sid, String pid, String oid) {
		SellerBean seller = sellerImpl.selectOneById(sid);
		boolean flag = false;
		if (seller == null) {
			throw new ServiceException("object_is_not_exist", "销售");
		}
		OrderBean order = orderImpl.selectOrderById(oid);
		if (order == null) {
			throw new ServiceException("object_is_not_exist", "工单");
		}
		JSONObject sellerJson = new JSONObject();
		sellerJson.put("name", seller.getUsername());
		sellerJson.put("phone", seller.getPhoneNum());
		ProviderBean proBean = providerImpl.selectOneById(pid);
		JSONObject providerJson = new JSONObject();
		providerJson.put("name", proBean.getName());
		providerJson.put("phone", proBean.getPhoneNum());
		providerJson.put("contact", proBean.getContact());

		if (order.getStatus() != 0) {
			throw new ServiceException("can_not_allot");
		}
		order.setStatus(2);// 等待服务商服务
		order.setStage(1);// 进行中
		order.setPid(pid);
		order.setIsAllot(1);// 分配服务商
		flag = orderImpl.updateProvider(order) > 0;
		if (!flag) {
			throw new ServiceException("allot_order_error");
		}

		// 派单完发送短信
		// phoneCode.send(proBean.getPhoneNum(), "您有新的工单待处理，请快去科技服务平台查看", null);
		flag = insertOrderLog(oid, order.getOrderNo(), sid, sellerJson.toJSONString(), pid, providerJson.toJSONString(),
				2, null, 0);// 0 创建并分配 1 编辑 2 派单 3 上传合同 4 确认完成 5 拒绝完成 6 调查反馈 7 归档关闭
		if (!flag) {
			throw new ServiceException("create_order_log_error");
		}
		return flag;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean closeOrder(String sid, String oid, String content, int status) {
		SellerBean seller = sellerImpl.selectOneById(sid);
		boolean flag = false;
		if (seller == null) {
			throw new ServiceException("object_is_not_exist", "销售");
		}
		OrderBean order = orderImpl.selectOrderById(oid);
		if (order == null) {
			throw new ServiceException("object_is_not_exist", "工单");
		}
		if ((status == -1 && order.getStatus() != 0) // 待处理时关闭
				|| (status == -2 && order.getStatus() != 3) // 服务商已接受后，合同问题关闭
				|| (status == -3 && order.getStatus() != 8)) { // 服务商无法完成，客户提出终止
			throw new ServiceException("order_can_not_close");
		}
		JSONObject sellerJson = new JSONObject();
		sellerJson.put("name", seller.getUsername());
		sellerJson.put("phone", seller.getPhoneNum());
		order.setStatus(status);
		order.setStage(3); // 阶段：关闭
		flag = orderImpl.updateStatus(order) > 0;
		if (!flag) {
			throw new ServiceException("allot_order_error");
		}
		flag = insertOrderLog(oid, order.getOrderNo(), sid, sellerJson.toJSONString(), null, null, 7, content, 0);// 0
																													// 创建并分配
																													// 1
																													// 编辑
																													// 2
																													// 派单
																													// 3
																													// 上传合同
																													// 4
																													// 确认完成
																													// 5
																													// 拒绝完成
																													// 6
																													// 调查反馈
																													// 7
																													// 归档关闭
		if (!flag) {
			throw new ServiceException("create_order_log_error");
		}
		return flag;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean uploadContract(String sid, String oid, String contractNo, String name, String signTime,
			String startTime, String endTime, String partyA, String partyB, String contractUrl, int payment,
			String paymentWay, String service, double money, String paymentDesc, int contractType) {
		SellerBean seller = sellerImpl.selectOneById(sid);
		boolean flag = false;
		if (seller == null) {
			throw new ServiceException("object_is_not_exist", "销售");
		}
		OrderBean order = orderImpl.selectOrderById(oid);
		if (order == null) {
			throw new ServiceException("object_is_not_exist", "工单");
		}
		if (order.getStatus() != 3) { // 服务商已接受状态后，销售可以与客户签合同
			throw new ServiceException("order_can_not_sign_contract");
		}
		OrderContractBean contract = orderImpl.selectOrderContractInType(oid, contractType);
		if (contract == null) {
			contract = new OrderContractBean();
		}
		contract.setContractNo(contractNo);
		contract.setContractUrl(contractUrl);
		if (!StringUtil.isEmpty(endTime)) {
			contract.setEndTime(DateUtil.getTimestamp(endTime, "yyyy-MM-dd"));
		}
		contract.setMoney(money);
		contract.setName(name);
		contract.setOid(oid);
		contract.setPartyA(partyA);
		contract.setPartyB(partyB);
		contract.setPayment(payment);
		contract.setPaymentWay(paymentWay);
		contract.setService(service);
		contract.setType(contractType);
		if (!StringUtil.isEmpty(startTime)) {
			contract.setStartTime(DateUtil.getTimestamp(startTime, "yyyy-MM-dd"));
		}
		if (!StringUtil.isEmpty(signTime)) {
			contract.setSignTime(DateUtil.getTimestamp(signTime, "yyyy-MM-dd"));
		}
		contract.setPaymentDesc(paymentDesc);
		if (contract.getCid() != null) {
			flag = orderImpl.updateContract(contract) > 0;
		} else {

			flag = orderImpl.insertContract(contract) > 0;
		}
		if (!flag) {
			throw new ServiceException("upload_contract_error");
		}
		int contractStatus = order.getContractStatus();
		order.setContractStatus(contractType);
		if (contractStatus != 0 && contractStatus != contractType) {
			order.setStatus(4);// 合同已上传
		}
		if (contractType == 1) {// 客户合同的时候
			order.setMoney(money);
		}
		if (!StringUtil.isEmpty(endTime)) {
			order.setExpectedTime(DateUtil.getTimestamp(endTime, "yyyy-MM-dd"));// 合同结束时间更新到工单预计完成时间
		}
		// 发送短信给服务商
		ProviderBean proBean = providerImpl.selectOneById(order.getPid());
		// phoneCode.send(proBean.getPhoneNum(), "您的工单已上传完合同，请快去科技服务平台查看", null);

		flag = orderImpl.updateStatus(order) > 0;
		JSONObject sellerJson = new JSONObject();
		sellerJson.put("name", seller.getUsername());
		sellerJson.put("phone", seller.getPhoneNum());
		if (!flag) {
			throw new ServiceException("update_order_status_error");
		}
		flag = insertOrderLog(oid, order.getOrderNo(), sid, sellerJson.toJSONString(), null, null, 3, contractNo,
				contractType);// 0 创建并分配 1 编辑 2 派单 3 上传合同 4 确认完成 5 拒绝完成 6 调查反馈 7 归档关闭
		if (!flag) {
			throw new ServiceException("create_order_log_error");
		}
		return flag;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean confirmOrder(String sid, String oid, String content, int type) {
		SellerBean seller = sellerImpl.selectOneById(sid);
		boolean flag = false;
		if (seller == null) {
			throw new ServiceException("object_is_not_exist", "销售");
		}
		OrderBean order = orderImpl.selectOrderById(oid);
		if (order == null) {
			throw new ServiceException("object_is_not_exist", "工单");
		}
		if (order.getStatus() != 5) { // 服务商申请完成，销售确认工单状态
			throw new ServiceException("order_can_not_confirm");
		}
		// 1完成工单, 可反馈 2拒绝完成
		order.setStatus(type == 1 ? 6 : 7);
		flag = orderImpl.updateStatus(order) > 0;
		JSONObject sellerJson = new JSONObject();
		sellerJson.put("name", seller.getUsername());
		sellerJson.put("phone", seller.getPhoneNum());
		if (!flag) {
			throw new ServiceException("update_order_status_error");
		}
		int coType = type == 1 ? 4 : 5;
		ProviderBean proBean = providerImpl.selectOneById(order.getPid());
		JSONObject providerJson = new JSONObject();
		providerJson.put("name", proBean.getName());
		providerJson.put("phone", proBean.getPhoneNum());
		providerJson.put("contact", proBean.getContact());
		flag = insertOrderLog(oid, order.getOrderNo(), sid, sellerJson.toJSONString(), order.getPid(),
				providerJson.toJSONString(), coType, content, 0);// 0 创建并分配 1 编辑 2 派单 3 上传合同 4 确认完成 5 拒绝完成 6 调查反馈 7 归档关闭
		if (!flag) {
			throw new ServiceException("create_order_log_error");
		}
		return flag;
	}

	/**
	 * 调查反馈
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean feedback(String sid, String oid, String content, String score) {
		SellerBean seller = sellerImpl.selectOneById(sid);
		boolean flag = false;
		if (seller == null) {
			throw new ServiceException("object_is_not_exist", "销售");
		}
		OrderBean order = orderImpl.selectOrderById(oid);
		if (order == null) {
			throw new ServiceException("object_is_not_exist", "工单");
		}
		if (order.getStatus() != 6) { // 6 销售确认完成，等待反馈状态
			throw new ServiceException("order_can_not_feedback");
		}
		order.setStatus(1);// 1 已完成
		order.setStage(2);// 阶段已完成
		flag = orderImpl.updateStatus(order) > 0;

		JSONObject obj = JSON.parseObject(score);
		double s1 = NumUtil.toDouble(obj.get("quality"), 0d);
		double s2 = NumUtil.toDouble(obj.get("speed"), 0d);
		double s3 = NumUtil.toDouble(obj.get("attribute"), 0d);
		double averageScore = (s1 + s2 + s3) / 3;
		// 插入调查反馈表
		OrderFeedbackBean feedback = new OrderFeedbackBean();
		feedback.setAverageScore(averageScore);
		feedback.setContent(content);
		feedback.setOid(oid);
		feedback.setPid(order.getPid());
		feedback.setSid(sid);
		feedback.setServiceScore(score);
		flag = orderImpl.insertFeedBack(feedback) > 0;
		if (!flag) {
			throw new ServiceException("insert_feedback_error");
		}
		// TODO：给服务商评分
		JSONObject sellerJson = new JSONObject();
		sellerJson.put("name", seller.getUsername());
		sellerJson.put("phone", seller.getPhoneNum());
		if (!flag) {
			throw new ServiceException("update_order_status_error");
		}
		JSONObject contentJson = new JSONObject();
		contentJson.put("score", JSON.parse(score));
		contentJson.put("describe", content);
		ProviderBean proBean = providerImpl.selectOneById(order.getPid());
		Double totalScore = NumUtil.toDouble(proBean.getTotalScore(), 0) + averageScore;
		int num = NumUtil.toInt(proBean.getTotalScore(), 0) + 1;
		proBean.setScore(totalScore / num);
		proBean.setTotalScore(totalScore);
		proBean.setScoreNum(num);
		flag = providerImpl.updateScore(proBean) > 0;
		if (!flag) {
			throw new ServiceException("update_provider_score_error");
		}
		JSONObject providerJson = new JSONObject();
		providerJson.put("name", proBean.getName());
		providerJson.put("phone", proBean.getPhoneNum());
		providerJson.put("contact", proBean.getContact());
		flag = insertOrderLog(oid, order.getOrderNo(), sid, sellerJson.toJSONString(), order.getPid(),
				providerJson.toJSONString(), 6, contentJson.toJSONString(), 0);// 0 创建并分配 1 编辑 2 派单 3 上传合同 4 确认完成 5 拒绝完成
																				// 6 调查反馈 7 归档关闭
		if (!flag) {
			throw new ServiceException("create_order_log_error");
		}
		return flag;
	}

}
