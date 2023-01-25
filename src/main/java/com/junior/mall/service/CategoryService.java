package com.junior.mall.service;

import com.github.pagehelper.PageInfo;
import com.junior.mall.model.pojo.Category;
import com.junior.mall.model.request.AddCategoryReq;
import com.junior.mall.model.request.UpdateCategoryReq;
import com.junior.mall.model.vo.CategoryVO;
import com.junior.mall.service.exception.CategoryException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {
    public void insertCategory(AddCategoryReq category) throws CategoryException;

    //更新目录
    @Transactional(rollbackFor = Exception.class)
    void updateCategory(UpdateCategoryReq category) throws CategoryException;

    void deleteCategory(Integer id) throws CategoryException;

    PageInfo selectAllAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> selectAllUser(Integer parentId);
}
