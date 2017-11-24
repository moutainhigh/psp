package com.psp.sellcenter.persist.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.psp.sellcenter.model.CategoryBean;
import com.psp.sellcenter.model.ProviderBean;
import com.psp.sellcenter.persist.dao.ProviderDao;

@Repository
public class ProviderImpl extends BaseImpl implements ProviderDao {
	
	final String NAME_SPACE = NAME_SPACE_HEADER + ".ProviderMapper";

	@Override
	public List<CategoryBean> selectAllCates() {
		return sqlSessionTemplate.selectList(NAME_SPACE + ".selectAllCates");
	}

	@Override
	public List<ProviderBean> selectListByCid(Integer categoryId) {
		return sqlSessionTemplate.selectList(NAME_SPACE + ".selectListByCid", categoryId);
	}
	
	@Override
	public ProviderBean selectOneById(String pid) {
		return sqlSessionTemplate.selectOne(NAME_SPACE + ".selectOneById", pid);
	}


}
