package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.DeptDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.DeptInfoEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.RoleUserMapping;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.acrabsoft.common.model.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeptService {
    @Autowired
    private DeptDao dao;

    @Autowired
    private BaseDao baseDao;

    private List<DeptInfoEntity> nodes;

    public List<DeptInfoEntity> getDeptTree() {
        List<DeptInfoEntity> list = this.dao.getDeptTree();
        if (list.size() > 0) {
            nodes = new ArrayList<DeptInfoEntity>();
            JSONArray jsonarray = JSONArray.parseArray(JSON.toJSONString(list));
            List<DeptInfoEntity> listnew = new ArrayList<>();
            for (int i = 0; i < jsonarray.size(); i++) {
                JSONObject jsonObject = jsonarray.getJSONObject(i);
                DeptInfoEntity dif = new DeptInfoEntity();
                dif.setObj_id(jsonObject.getString("objId"));
                dif.setDeptid(jsonObject.getString("deptid"));
                dif.setDeptname(jsonObject.getString("deptname"));
                dif.setParentid(jsonObject.getString("parentid"));
                dif.setParentobj(jsonObject.getString("parentobj"));
                /*dif.setAlias(jsonObject.getString("alias"));
                dif.setAreaid(jsonObject.getString("areaid"));
                dif.setFull_name(jsonObject.getString("fullName"));
                dif.setIsparent(jsonObject.getString("isparent"));
                dif.setUpdatetime(jsonObject.getString("updatetime"));
                dif.setOrdercode(jsonObject.getString("ordercode"));
                dif.setScope_id(jsonObject.getString("scopeId"));
                dif.setOrg_status(jsonObject.getInteger("orgStatus"));*/
                listnew.add(dif);
            }
//            List<DeptInfoEntity> listnew = JSONArray.parseArray(jsonarray.toString(),DeptInfoEntity.class);
            Iterator<DeptInfoEntity> it = listnew.iterator();
            while (it.hasNext()) {
                DeptInfoEntity ob = (DeptInfoEntity) it.next();
                nodes.add(ob);
            }
            return this.buildTree();
        }
        return null;
    }


    /**
     * ??????????????????
     *
     * @return
     */
    public List<DeptInfoEntity> buildTree() {
        List<DeptInfoEntity> treeNodes = new ArrayList<DeptInfoEntity>();
        List<DeptInfoEntity> rootNodes = getRootNodes();
        for (DeptInfoEntity rootNode : rootNodes) {
            buildChildNodes(rootNode);
            treeNodes.add(rootNode);
        }
        return treeNodes;
    }


    /**
     * ?????????????????????????????????
     *
     * @return
     */
    public List<DeptInfoEntity> getRootNodes() {
        List<DeptInfoEntity> rootNodes = new ArrayList<DeptInfoEntity>();
        for (DeptInfoEntity n : nodes) {
            if (rootNode(n)) {
                rootNodes.add(n);//???????????????????????????rootNodes?????????
            }
        }
        return rootNodes;
    }

    /**
     * ????????????????????????
     *
     * @param node
     * @return
     */
    public boolean rootNode(DeptInfoEntity node) {
        boolean isRootNode = true;
        if ("0" != node.getParentobj()) {//???????????????node????????????????????????????????????????????????node???????????????????????????????????????????????????????????????
            isRootNode = false;
        }
        return isRootNode;
    }


    /**
     * ???????????????
     *
     * @param node
     */
    public void buildChildNodes(DeptInfoEntity node) {
        List<DeptInfoEntity> children = getChildNodes(node);
        if (!children.isEmpty()) {
            for (DeptInfoEntity child : children) {
                buildChildNodes(child);
            }
            node.setChild(children);
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param pnode
     * @return
     */
    public List<DeptInfoEntity> getChildNodes(DeptInfoEntity pnode) {//???????????????????????????????????????????????????????????????????????????????????????
        List<DeptInfoEntity> childNodes = new ArrayList<DeptInfoEntity>();
        for (DeptInfoEntity n : nodes) {//???nodes??????????????????pnode????????????
            if (pnode.getObj_id().equals(n.getParentobj())) {
                childNodes.add(n);
            }
        }
        return childNodes;
    }

    public List<DeptInfoEntity> getDeptTreeById(String id) {
        List<DeptInfoEntity> list = null;
        if (StringUtils.isNotEmpty(id)) {
            list = this.dao.getchilds(id);
        } else {
            list = this.dao.getRoots();
        }
        return list;
    }

    public Pagination getUserByDeptid(int pageNum, int pageSize, String deptid, String username) {
        Pagination p = new Pagination(pageNum, pageSize);
        return this.dao.getUserByDeptid(p, deptid, username);
    }


    /**
     * @description  ????????????id??????????????????id
     * @param  deptId  ??????id
     * @param  deptname  ????????????
     * @param  njDeptId  ????????????id
     * @return  ????????????
     * @date  20/09/09 17:38
     * @author  wanghb
     * @edit
     */
    public String getFullName(String deptId, String deptname, String njDeptId) {
        String fullName = deptname;
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deptid", deptId));
        List<DeptInfoEntity> list = baseDao.get( DeptInfoEntity.class,queryConditions );
        String objId = null;
        if (list.size() == 0) {
            return null;
        }else{
            objId = list.get( 0 ).getObj_id();
        }

        while (true){
            if (objId == null) {
                return null;
            }
            DeptInfoEntity deptInfoEntity = baseDao.getById( DeptInfoEntity.class, objId );
            if(deptInfoEntity == null){
                return null;
            }
            String parentId = deptInfoEntity.getParentid();
            if (njDeptId.equals( parentId )) {
                return "";
            }else{
                objId = parentId;
            }
        }
    }

    /**
     * @description  ????????????
     * @param  deptId  ??????id
     * @return  ????????????
     * @date  20/09/14 20:43
     * @author  wanghb
     * @edit
     */
    public DeptInfoEntity getByDeptId(String deptId) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deptid", deptId));
        List<DeptInfoEntity> userInfoEntities = baseDao.get( DeptInfoEntity.class, queryConditions );
        return userInfoEntities.size() > 0 ? userInfoEntities.get(  0) : new DeptInfoEntity() ;
    }

    /**
     * @description  ????????????id  ??????????????????id
     * @param
     * @return  ????????????
     * @date  20/09/23 11:26
     * @author  wanghb
     * @edit
     */
    public List<Map<String, Object>> getDeptsByParentid(String deptId) {
        SQL sql = new SQL();
        sql.SELECT("deptid as id ,deptName as name");
        sql.FROM("T_SYS_DEPT_INFO_VIEW");
        sql.WHERE( new StringBuilder(" PARENTID = '" ).append( deptId ).append( "'" ).toString() );
        sql.ORDER_BY( "pxh" );
        List<Map<String, Object>> list = baseDao.getListByNactiveSql(sql);
        return list;
    }

    /**
     * @description  ????????????map  ???:id ???:name
     * @param  njDeptId  ??????id
     * @return  ????????????
     * @date  2020-12-1 14:52
     * @author  wanghb
     * @edit
     */
    public Map<String, String> getBranchDeptMap(String njDeptId) {
        List<Map<String, Object>> list = getDeptsByParentid(njDeptId);
        Map<String, String> temp = new HashMap<>();

        for (Map<String, Object> map : list) {
            String id = PowerUtil.getString( map.get( "id" ) );
            String name = PowerUtil.getString(map.get( "name" ) );
            temp.put(id,name);
        }
        return temp;
    }
}
