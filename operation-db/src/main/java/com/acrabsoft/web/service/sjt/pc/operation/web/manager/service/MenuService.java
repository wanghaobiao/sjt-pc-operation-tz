package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.constant.Constants;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.MenuDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.MenuInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenuService extends BaseController {
    @Autowired
    private MenuDao dao;

    @Autowired
    private BaseDao basedao;
    @Autowired
    private RoleService roleService;

    public List<MenuInfo> getChildMenusById(String menuId) {
        MenuInfo menuInfo = new MenuInfo();
        menuInfo.setParentId(menuId);
        menuInfo.setDeleted("0");
        List<QueryCondition> queryConditionList = new ArrayList<>();
        queryConditionList.add(new QueryCondition("deleted", "0"));
        queryConditionList.add(new QueryCondition("parentId",menuId));
        List<MenuInfo> list = this.basedao.get(MenuInfo.class, queryConditionList,"order_num");
        return list;
    }

    public JSONArray getChildMenus(MenuInfo menuInfo, String ownMenuId) {
        JSONArray jsonArray = new JSONArray();
        List<MenuInfo> list = this.getChildMenusById(menuInfo.getObjId());
        for (MenuInfo menuInfo0 : list) {
            if (ownMenuId.contains(menuInfo0.getObjId())) {
                JSONObject menuPath = JSONObject.parseObject(menuInfo0.getMenuPath());
                if ("1".equals(menuInfo0.getIsparent())) {
                    JSONArray childArr = this.getChildMenus(menuInfo0, ownMenuId);
                    if(!childArr.isEmpty()){
                        menuPath.put("children", childArr);
                    }
                }
                jsonArray.add(menuPath);
            }
        }
        return jsonArray;
    }


    /**
     * ???????????????????????????????????????
     * ???????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????PARENT_ID ??? 000
     * @param menuList
     * @return
     */
    public JSONObject shuffleMenuList(List<MenuInfo> menuList) {
        JSONObject topMenus = new JSONObject();
        // ??????????????????
        Iterator<MenuInfo> iterator = menuList.iterator();
        while (iterator.hasNext()) {
            MenuInfo menuInfo = iterator.next();
            if (MenuInfo.TOP_MENU_ID.equals(menuInfo.getParentId())) { // ?????????????????????
                topMenus.put(menuInfo.getObjId(), JSON.parseObject(JSON.toJSONString(menuInfo)));

                iterator.remove();
            }
        }
        // ??????????????????
        if (menuList.size() > 0) {
            Iterator<MenuInfo> level2It = menuList.iterator();
            while (level2It.hasNext()) {
                MenuInfo menuInfo = level2It.next();
                JSONObject parentMenu = topMenus.getJSONObject(menuInfo.getParentId());
                if (parentMenu != null) {
                    JSONArray childs = parentMenu.getJSONArray("childs");
                    if (childs == null) {
                        childs = new JSONArray();
                    }
                    childs.add(menuInfo);
                    parentMenu.put("childs", childs);

                    level2It.remove();
                }
            }
        }

        return topMenus;
    }

    public String addMenu(MenuInfo mi) {
        if (StringUtils.isNotEmpty(mi.getParentId())) {
            mi = this.basedao.saveAndFlush(mi);  // 1.?????????????????????
            if (StringUtils.isNotEmpty(mi.getObjId())) { // 2.??????????????????
                MenuInfo parentmenu = this.basedao.getById(MenuInfo.class, mi.getParentId());
                if (null == parentmenu) {
                    return null;
                }
                parentmenu.setIsparent("1");
                this.basedao.update(parentmenu);
                return mi.getObjId();
            }
        } else {
            mi = this.basedao.saveAndFlush(mi);
            return mi.getObjId();
        }
        return null;
    }

    public void updateMenu(MenuInfo mi) {
        this.basedao.update(mi);
    }

    public Result deleteMenu(String objId) {
        //1.????????????
        MenuInfo mi = this.basedao.getById(MenuInfo.class, objId);
        //2.??????????????????????????????????????????????????????????????????
        if (mi.getIsparent().equals("1")) {
            return BuildResult.buildOutResult(ResultEnum.ERROR, "???????????????????????????????????????");
        }
        //3.????????????
        mi.setDeleted(Constants.DELETED_STATE.IS_DELETED);
        this.basedao.update(mi);
        //4.??????parentid???????????????????????????????????????
        String parentid = mi.getParentId();
        if (StringUtils.isNotEmpty(parentid)) {
            // ??????????????????
            MenuInfo menu = new MenuInfo();
            menu.setParentId(mi.getParentId());
            List<MenuInfo> list = this.basedao.get(MenuInfo.class, menu);
            if (list.size() <= 0) { // ???????????????????????????????????????????????????????????????isparent?????????
                MenuInfo parentmenu = this.basedao.getById(MenuInfo.class, mi.getParentId());
                parentmenu.setIsparent("0");
                this.basedao.update(parentmenu);
            }
        }
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, "???????????????");
    }
}
