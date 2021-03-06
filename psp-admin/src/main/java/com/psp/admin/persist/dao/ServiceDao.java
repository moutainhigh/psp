package com.psp.admin.persist.dao;

import java.util.List;

import com.psp.admin.model.CategoryBean;

public interface ServiceDao {

	List<CategoryBean> selectAllCates();

	List<CategoryBean> selectService(String parentId);

	int selectServiceCountByPid(int parentId);

	List<CategoryBean> selectServiceByPid(int parentId);

	int insert(CategoryBean cate);

	CategoryBean selectServiceById(int cid);

	int update(CategoryBean cate);

	List<CategoryBean> selectProviderCates(String pid);

	List<CategoryBean> selectServiceByCids(List<CategoryBean> provider);

	int selectProviderCountByCid(int cid);

	int deleteService(int cid);


}
