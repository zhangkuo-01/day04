package com.xiaoshu.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Category;
import com.xiaoshu.entity.Content;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.ContentService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

@Controller
@RequestMapping("content")
public class ContentController {

	@Autowired
	ContentService contentService;
	
	@Autowired
	private OperationService operationService;
	
	
	@RequestMapping("contentIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Category> roleList = contentService.findAll();
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		return "content";
	}
	@RequestMapping(value="userList",method=RequestMethod.POST)
	public void userList(Content content,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<Content> userList= contentService.findContentAll(content,pageNum,pageSize);
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",userList.getTotal() );
			jsonObj.put("rows", userList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// 新增或修改
		@RequestMapping("reserveUser")
		public void reserveUser(HttpServletRequest request,Content content,HttpServletResponse response){
			JSONObject result=new JSONObject();
			try {
				   // 添加
					if(contentService.existContentWithContenttitle(content.getContenttitle())==null){  // 没有重复可以添加
						content.setCreatetime(new Date());
						contentService.addContent(content);
						result.put("success", true);
					} else {
						result.put("success", true);
						result.put("errorMsg", "该用户名被使用");
					}
				
			} catch (Exception e) {
				e.printStackTrace();
				result.put("success", true);
				result.put("errorMsg", "对不起，操作失败");
			}
			WriterUtil.write(response, result.toString());
		}
		@RequestMapping("deleteUser")
		public void delUser(HttpServletRequest request,HttpServletResponse response){
			JSONObject result=new JSONObject();
			try {
				String[] ids=request.getParameter("ids").split(",");
				for (String id : ids) {
					contentService.deleteContent(Integer.parseInt(id));
				}
				result.put("success", true);
				result.put("delNums", ids.length);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("errorMsg", "对不起，删除失败");
			}
			WriterUtil.write(response, result.toString());
		}@RequestMapping("inContent")
		public void inStudent(HttpServletResponse response,MultipartFile file){
			JSONObject result=new JSONObject();
			try {
				// 读取上传的文件，并生成对应的WorkBook对象
				HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
				
				// 获取期中sheet
				HSSFSheet sheet = wb.getSheetAt(0);
				// 获取最后一行的下标
				int lastRowNum = sheet.getLastRowNum();
				
				for (int i = 1; i < lastRowNum+1; i++) {
					HSSFRow row = sheet.getRow(i);
					
					Content c = new Content();
					
					String contenttitle = row.getCell(1).getStringCellValue();
					c.setContenttitle(contenttitle);
					
					String categoryname = row.getCell(2).getStringCellValue();
					Integer contentcategoryid = contentService.findID(categoryname);
					c.setContentcategoryid(contentcategoryid);
					
					String picpath = row.getCell(3).getStringCellValue();
					c.setPicpath(picpath);
					
					String contenturl = row.getCell(4).getStringCellValue();
					c.setContenturl(contenturl);
					
					Integer price = (int) row.getCell(5).getNumericCellValue();
					c.setPrice(price);
					
					String status = row.getCell(6).getStringCellValue();
					c.setStatus(status);
					
					String createtime = row.getCell(7).getStringCellValue();
					c.setCreatetime(TimeUtil.ParseTime(createtime, "yyyy-MM-dd HH:mm:ss"));
					
					
					// 放入数据库
					contentService.addContent(c);
					
				}
				
				
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("errorMsg", "对不起，删除失败");
			}
			WriterUtil.write(response, result.toString());
		}
}
