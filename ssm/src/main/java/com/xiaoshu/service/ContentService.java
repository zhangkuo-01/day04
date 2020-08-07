package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.CategoryMapper;
import com.xiaoshu.dao.ContentMapper;
import com.xiaoshu.entity.Category;
import com.xiaoshu.entity.Content;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.UserExample.Criteria;

@Service
public class ContentService {

	@Autowired
	ContentMapper contentMapper;
	
	@Autowired
	CategoryMapper categoryMapper;

	public List<Category> findAll() {
		// TODO Auto-generated method stub
		return categoryMapper.selectAll();
	}

	public PageInfo<Content> findContentAll(Content content, Integer pageNum, Integer pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		List<Content> userList = contentMapper.findAll(content);
		PageInfo<Content> pageInfo = new PageInfo<Content>(userList);
		return pageInfo;
	}

	public Content existContentWithContenttitle(String contenttitle) {
		// TODO Auto-generated method stub
		Content content=contentMapper.existContentWithContenttitle(contenttitle);
		return content;
	}

	public void addContent(Content content) {
		// TODO Auto-generated method stub
		contentMapper.insert(content);
	}

	public void deleteContent(int parseInt) {
		// TODO Auto-generated method stub
		contentMapper.deleteByPrimaryKey(parseInt);
	}

	public Integer findID(String categoryname) {
		// TODO Auto-generated method stub
		return categoryMapper.findId(categoryname);
	}

}
